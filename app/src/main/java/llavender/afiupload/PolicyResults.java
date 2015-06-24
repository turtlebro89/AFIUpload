package llavender.afiupload;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.parse.ParseObject;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PolicyResults extends Fragment {


    private ArrayList<ParseObject> policies;



    public PolicyResults() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_policy_results, container, false);

        //Wire up the widgets
        TextView policyFoundText = (TextView) view.findViewById(R.id.policyFoundView);
        TextView instructions = (TextView) view.findViewById(R.id.instructions);
        ListView policyListView = (ListView) view.findViewById(R.id.listOfPolicies);
        policies = Holder.getPolicies();

        instructions.setText("Tap the policy you would like to upload photo's for");

        //set the text for # of policies found
        policyFoundText.setText(policies.size() + "policie(s) found");

        ObjectArrayAdapter adapter = new ObjectArrayAdapter(getActivity(), R.layout.display_policies_layout, policies);
        policyListView.setAdapter(adapter);

        policyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Holder.setPolicy(policies.get(position));
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent.createChooser(intent, "Select Pictures"), 2);

            }
        });

        //enable the Action Bar
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }

    public class ObjectArrayAdapter extends ArrayAdapter<ParseObject> {

        //declare Array List of items we create
        private ArrayList policies;


        /**
         * Constructor overrides constructor for array adapter
         * The only variable we care about is the ArrayList<PlatformVersion> objects
         * it is the list of the objects we want to display
         *
         * @param context The current context.
         * @param resource The resource ID for a layout file containing a layout to use when
         *                           instantiating views.
         * @param policies The objects to represent in the ListView.
         */
        public ObjectArrayAdapter(Context context, int resource, ArrayList<ParseObject> policies) {
            super(context, resource, policies);
            this.policies = policies;
        }

        /**
         * Creates a custom view for our list View and populates the data
         *
         * @param position position in the ListView
         * @param convertView the view to be inflated
         * @param parent the parent view
         * @return the view created
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            /**
             * Checking to see if the view is null. If it is we must inflate the view
             * "inflate" means to render/show the view
             */

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.display_policies_layout, null);
            }

            /**
             * Remember the variable position is sent in as an argument to this method.
             * The variable simply refers to the position of the current object on the list\
             * The ArrayAdapter iterate through the list we sent it
             */
            ParseObject policy = (ParseObject) policies.get(position);

            if (policy != null) {
                // obtain a reference to the widgets in the defined layout
                TextView policyText = (TextView) view.findViewById(R.id.policyNumberText);
                TextView addressText = (TextView) view.findViewById(R.id.policyInfoText);


                if(policyText != null) {
                    policyText.setText("Policy Number: " + policy.getInt("PolicyNumber"));
                }
                if (addressText != null) {
                    addressText.setText(policy.getString("Line1") + ", " + policy.getString("Line2"));
                }

            }

            // view must be returned to our current activity
            return view;
        }
    }

}
