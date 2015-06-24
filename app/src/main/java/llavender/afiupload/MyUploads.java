package llavender.afiupload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class MyUploads extends Fragment {







    public MyUploads() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_uploads, container, false);
        //wire the widgets
        ListView imageInfo = (ListView) view.findViewById(R.id.listOfUploads);
        TextView numberOfImages = (TextView) view.findViewById(R.id.imageNumberView);
        Button saveButton = (Button) view.findViewById(R.id.updateButton);
        Holder.setCommentsList(new ArrayList<String>());
        //create list of Images
        final ArrayList<Uri> images = Holder.getImages();
        //Create Custom adapter
        ObjectArrayAdapter adapter = new ObjectArrayAdapter(getActivity(), R.layout.my_uploads_list_layout, images);
        //Set the list adapter
        imageInfo.setAdapter(adapter);
        //set the text areas
        numberOfImages.append("You selected " + images.size() + "images");
        //get the current user
        final ParseUser currentUser = ParseUser.getCurrentUser();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0;images.size() > i; i++){
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(images.get(i)));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] imageBytes = stream.toByteArray();
                        ParseFile image = new ParseFile("photo.jpeg",  imageBytes, "jpeg");
                        image.save();

                        ParseObject photoUpload = new ParseObject("Photos");
                        photoUpload.put("AgentID", currentUser.getObjectId());
                        photoUpload.put("Comment", Holder.getCommentsList().get(i));
                        photoUpload.put("Photo", image);
                        photoUpload.put("Policy", (Holder.getPolicy()).getObjectId());
                        photoUpload.saveInBackground();

                    } catch(FileNotFoundException e) {
                        Log.d("File not found", e.toString());
                    } catch(ParseException e){
                        Log.d("Parse Exception", e.toString());
                    }

                }

                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment())
                        .commit();
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

    public class ObjectArrayAdapter extends ArrayAdapter<Uri> {

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
        public ObjectArrayAdapter(Context context, int resource, ArrayList<Uri> images) {
            super(context, resource, images);
            this.images = images;
        }

        /**
         * Creates a custom view for our list View and populates the data
         *
         * @param position position in the ListView
         * @param convertView View to change to
         * @param parent the calling class
         * @return view the inflated view
         */
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder vHolder;

            /**
             * Checking to see if the view is null. If it is we must inflate the view
             * "inflate" means to render/show the view
             */

            if (view == null) {
                vHolder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.my_uploads_list_layout, null);
//                view.setTag(position);
                vHolder.commentsText = (MultiAutoCompleteTextView) view.findViewById(R.id.CommentsText);
                vHolder.imageToUpload = (ImageView) view.findViewById(R.id.imageToUpload);

                view.setTag(vHolder);

            }
            else{
                vHolder = (ViewHolder)convertView.getTag();
            }

            vHolder.index = position;

            if(position >= Holder.getCommentsList().size()){
                Holder.getCommentsList().add("");
            }

            /**
             * Remember the variable position is sent in as an argument to this method.
             * The variable simply refers to the position of the current object on the list\
             * The ArrayAdapter iterate through the list we sent it
             */
            Uri imageUri = (Uri) images.get(position);

            if (imageUri != null) {
                // obtain a reference to the widgets in the defined layout
                if (vHolder.imageToUpload != null) {
                    Picasso.with(getContext()).load(imageUri).fit().centerInside().into(vHolder.imageToUpload);
                }
                if (vHolder.commentsText != null) {
                    Log.d("Comment", String.valueOf(position) + ":" + Holder.getCommentsList().get(position));
                    vHolder.commentsText.setText(Holder.getCommentsList().get(position));
                }
            }

            vHolder.commentsText.setText(Holder.getCommentsList().get(position));
            vHolder.commentsText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Holder.putInCommentsList(s.toString(), vHolder.index);
                    }
                });



            // view must be returned to our current activity
            return view;
        }

        private class ViewHolder {
            MultiAutoCompleteTextView commentsText;
            ImageView imageToUpload;
            int index;
        }
    }


}
