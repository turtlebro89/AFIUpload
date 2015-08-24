package llavender.afiupload;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyUploadsFromMain extends Fragment implements ConfirmDeleteDialogFragment.NoticeDialogListener{

    private ListView imageInfo;
    private ArrayList<String> comments;
    private Button saveButton;
    private ObjectArrayAdapter adapter;
    private ArrayList<ParseObject> parseObjects;
    private ArrayList<String> imageUrls;
    private int currentPosition;

    public MyUploadsFromMain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_uploads, container, false);

        imageInfo = (ListView) view.findViewById(R.id.listOfUploads);
        saveButton = (Button) view.findViewById(R.id.updateButton);
        ParseUser currentUser = ParseUser.getCurrentUser();
        final ArrayList<String> commentsList = new ArrayList<>();
        comments = new ArrayList<>();
        parseObjects = new ArrayList<>();
        imageUrls = new ArrayList<>();

        //get the images and files from Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photos");
        query.whereEqualTo("AgentID", currentUser.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    //add all items returned to list
                    parseObjects.addAll(list);
                    for (int i = 0; list.size() > i; i++) {
                        String url = list.get(i).getParseFile("Photo").getUrl();
                        imageUrls.add(url);
                        comments.add(list.get(i).getString("Comment"));
                        adapter = new ObjectArrayAdapter(getActivity(), R.layout.my_uploads_list_layout, imageUrls);
                        imageInfo.setAdapter(adapter);
                    }
                    Holder.setParseObjects(parseObjects);
                    Holder.setComments(comments);
                    Holder.setImageUrls(imageUrls);


                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for(int i = 0; imageUrls.size() > i; i++){

                                commentsList.add(comments.get(i));
                            }

                            Holder.setCommentsList(commentsList);
                            Holder.setParseObjects(parseObjects);
                            Holder.setImageUrls(imageUrls);


                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConfirmChanges())
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });

                    imageInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            ConfirmDeleteDialogFragment confirmationDialog = new ConfirmDeleteDialogFragment();
                            confirmationDialog.setTargetFragment(MyUploadsFromMain.this, 0);
                            confirmationDialog.show(getFragmentManager(), null);
                            currentPosition = position;
                            return false;
                        }
                    });

                } else {
                    //Something went wrong
                    Log.d("Error retrieving rows", e.toString());
                }
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

    //Removes the object from the database and from the local lists
    public void removeEntry(int position){
        ParseObject removeObject = parseObjects.get(position);

        removeObject.deleteInBackground();

        imageUrls.remove(position);
        comments.remove(position);
        parseObjects.remove(position);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDialogPositiveClick() {
        removeEntry(currentPosition);
    }

    @Override
    public void onDialogNegativeClick() {
        Toast.makeText(getActivity(), "Deletion Cancelled", Toast.LENGTH_SHORT).show();
    }

    public class ObjectArrayAdapter extends ArrayAdapter<String> {

        //declare Array List of items we create
        private ArrayList images;


        /**
         * Constructor overrides constructor for array adapter
         * The only variable we care about is the ArrayList<PlatformVersion> objects
         * it is the list of the objects we want to display
         *
         * @param context The current context.
         * @param resource The resource ID for a layout file containing a layout to use when
         *                           instantiating views.
         * @param images The objects to represent in the ListView.
         */
        public ObjectArrayAdapter(Context context, int resource, ArrayList<String> images) {
            super(context, resource, images);
            this.images = images;
        }

        /**
         * Creates a custom view for our list View and populates the data
         *
         * @param position The position in the ListView
         * @param convertView the View to inflate
         * @param parent the calling method
         * @return the view
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final Holder holder = Holder.getInstance();
            final ViewHolder vHolder;

            /**
             * Checking to see if the view is null. If it is we must inflate the view
             * "inflate" means to render/show the view
             */

            if (view == null) {
                vHolder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.my_uploads_list_layout, null);
                vHolder.commentsText = (MultiAutoCompleteTextView) view.findViewById(R.id.CommentsText);
                vHolder.imageToUpload = (ImageView) view.findViewById(R.id.imageToUpload);

                view.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }

            vHolder.index = position;

            /**
             * Remember the variable position is sent in as an argument to this method.
             * The variable simply refers to the position of the current object on the list\
             * The ArrayAdapter iterate through the list we sent it
             */
            String imageUrl = (String)images.get(position);

            if (imageUrl != null) {
                // obtain a reference to the widgets in the defined layout

                if(vHolder.imageToUpload != null){
                    Picasso.with(getContext()).load(imageUrl).fit().centerInside().into(vHolder.imageToUpload);
                }
                if(vHolder.commentsText != null) {
                    vHolder.commentsText.setText(comments.get(position));
                    vHolder.commentsText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            Holder.putInComments(s.toString(),vHolder.index);
                        }
                    });
                }
            }

            // view must be returned to our current activity
            return view;
        }

        public class ViewHolder{
            MultiAutoCompleteTextView commentsText;
            ImageView imageToUpload;
            int index;
        }
    }
}
