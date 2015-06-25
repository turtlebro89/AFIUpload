package llavender.afiupload;


import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchContacts extends Fragment {

    //load UI elements
    private EditText firstNameView;
    private EditText lastNameView;
    private EditText cityView;
    private EditText zipCode;
    private Spinner stateSpinner;
    private Button searchButton;
    private ArrayAdapter<String> adapter;
    private ArrayList clientList;
    ContactResults results;

    public SearchContacts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_contacts, container, false);
        firstNameView = (EditText) view.findViewById(R.id.firstNameView);
        lastNameView = (EditText) view.findViewById(R.id.lastNameView);
        cityView = (EditText) view.findViewById(R.id.cityView);
        zipCode = (EditText) view.findViewById(R.id.zipCodeView);
        stateSpinner = (Spinner) view.findViewById(R.id.stateSpinner);
        searchButton = (Button) view.findViewById(R.id.searchBtn);
        clientList = new ArrayList();
        results = new ContactResults();

                //create the spinner adapter
        adapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.states));
        //set the spinner adapter
        stateSpinner.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNameView.getText().toString().equals("")) {
                    lastNameView.setError("You must enter a Last Name");
                } else {
                    //load the information from parse
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
                    query.whereEqualTo("AgentID", ParseUser.getCurrentUser().getObjectId());
                    if (!firstNameView.getText().toString().equals("")) {
                        query.whereEqualTo("FirstName", firstNameView.toString());
                    }
                    if (!lastNameView.getText().toString().equals("")){
                        query.whereEqualTo("LastName", lastNameView.getText().toString());
                    }
                    if (!cityView.getText().toString().equals("")) {
                        query.whereEqualTo("City", cityView.toString());
                    }
                    if (!zipCode.getText().toString().equals("")) {
                        query.whereEqualTo("ZIP", zipCode.toString());
                    }
                    if (!stateSpinner.getSelectedItem().toString().equals("Select One")) {
                        query.whereEqualTo("State", stateSpinner.getSelectedItem().toString());
                    }

                    //Complete the query in the background
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> clients, ParseException e) {
                            if (e == null) {
                                Log.d("score", "Retrieved " + clients.size() + " clients");
                                clientList.addAll(clients);
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }

                            if (clientList.size() < 1) {
                                Toast.makeText(getActivity(), "You are not registered to any clients that meet these criteria", Toast.LENGTH_SHORT).show();
                            } else {
                                Holder.setClients(clientList);
                                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, results)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        }
                    });
                }
            }
        });
        return view;
    }
}
