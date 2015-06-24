package llavender.afiupload;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmChanges extends Fragment {

    private Holder holder;

    public ConfirmChanges() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_changes, container, false);

        Button updateButton = (Button) view.findViewById(R.id.commitButton);
        TextView confirmDetailsText = (TextView) view.findViewById(R.id.detailsText);
        holder = Holder.getInstance();
        final ArrayList<String> commentsList = holder.getCommentsList();
        confirmDetailsText.setText("Would you like to commit these changes?");

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ParseObject> parseObjects = holder.getParseObjects();

                for(int i = 0; commentsList.size() > i; i++) {

                    parseObjects.get(i).put("Comment", commentsList.get(i));
                    parseObjects.get(i).saveInBackground();
                }

                Toast.makeText(getActivity(), "Your changes have been commited", Toast.LENGTH_SHORT).show();


                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment())
                        .commit();
            }
        });
        //allow editing of the options menu
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }
}
