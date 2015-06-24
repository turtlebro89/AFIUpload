package llavender.afiupload;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactResults extends Fragment {

    private ArrayList<ParseObject> availableContacts;
    private ArrayList<ParseObject> policies;
    private PolicyResults results;


    public ContactResults() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_results, container, false);

        //wire up widgets
        TextView contactsFound = (TextView) view.findViewById(R.id.contactFoundView);
        ListView contacts = (ListView) view.findViewById(R.id.listOfContacts);

        results = new PolicyResults();

        //populate global variables
        availableContacts = Holder.getClients();

        ObjectArrayAdapter adapter = new ObjectArrayAdapter(getActivity(), R.layout.display_clients_layout, availableContacts);

        //set the number of contacts found
        contactsFound.setText(availableContacts.size() + " contact(s) found matching search criteria");
        //set the adapter
        contacts.setAdapter(adapter);
        //set the onItemClick listener
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Call the Parse service query for each policy with the Contact's object ID
                ParseObject client = availableContacts.get(position);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Policy");
                query.whereEqualTo("ClientID", client.getObjectId());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if(e == null){
                            //Success!
                            if(list.size() < 1){
                                Toast.makeText(getActivity(), "There are no policies associated with this client", Toast.LENGTH_SHORT).show();
                            } else {
                                // set the global variable
                                policies = (ArrayList<ParseObject>) list;
                                Holder.setPolicies(policies);
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, results)
                                        .addToBackStack(null)
                                        .commit();
                            }

                        } else {
                            //Failure
                            Log.d("Parse Failure", e.toString());
                        }
                    }
                });
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
        private ArrayList clients;


        /**
         * Constructor overrides constructor for array adapter
         * The only variable we care about is the ArrayList<PlatformVersion> objects
         * it is the list of the objects we want to display
         *
         * @param context The current context.
         * @param resource The resource ID for a layout file containing a layout to use when
         *                           instantiating views.
         * @param clients The objects to represent in the ListView.
         */
        public ObjectArrayAdapter(Context context, int resource, ArrayList<ParseObject> clients) {
            super(context, resource, clients);
            this.clients = clients;
        }

        /**
         * Creates a custom view for our list View and populates the data
         *
         * @param position the position in the ListView
         * @param convertView the listItem to inflate
         * @param parent the calling method
         * @return the view
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            /**
             * Checking to see if the view is null. If it is we must inflate the view
             * "inflate" means to render/show the view
             */

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.display_clients_layout, null);
            }

            /**
             * Remember the variable position is sent in as an argument to this method.
             * The variable simply refers to the position of the current object on the list\
             * The ArrayAdapter iterate through the list we sent it
             */
            ParseObject client = (ParseObject) clients.get(position);

            if (client != null) {
                // obtain a reference to the widgets in the defined layout
                TextView nameText = (TextView) view.findViewById(R.id.nameText);
                TextView addressText = (TextView) view.findViewById(R.id.addressText);
                TextView cityStatZipText = (TextView) view.findViewById(R.id.cityStateZipText);

                if(nameText != null) {
                    nameText.setText(client.getString("LastName") + ", " + client.getString("FirstName"));
                }
                if (addressText != null) {
                    addressText.setText(client.getString("Address"));
                }
                if (cityStatZipText != null) {
                    cityStatZipText.setText(client.getString("City") + ", " + client.getString("State") + " " + client.getInt("ZIP"));
                }
            }

            // view must be returned to our current activity
            return view;
        }
    }
}
