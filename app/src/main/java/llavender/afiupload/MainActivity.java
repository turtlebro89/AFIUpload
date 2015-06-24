package llavender.afiupload;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.parse.Parse;
import com.parse.ParseUser;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "JFiVPwvdrJ1Td0A327bgWst4x0NK5ubWHTkZltqM", "zmjyhmO4MuNbbnRYWqR4eqgyZKdwyp2bfX1jLt9f");

        //if there is a user logged into the app already start the main fragment
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).addToBackStack(null).commit();
        } else {
            // show the signup or login screen
            if (findViewById(R.id.fragment_container) != null){
                if(savedInstanceState == null) {
                    LoginFragment loginFragment = new LoginFragment();
                    // In case the activity was started with special instructions from an Intent
                    // pass the Intent's extras to the fragment as arguments
                    loginFragment.setArguments(getIntent().getExtras());

                    getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loginFragment).commit();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_upload_quality).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            //logout the parseUser
            ParseUser.logOut();
            //Restart the Login fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();
            Uri targetUri;
            ArrayList<Uri> images = new ArrayList<>();
            if(clipData != null) {
                for(int i =0; clipData.getItemCount() > i; i++) {
                    // get the target Uri
                    targetUri = clipData.getItemAt(i).getUri();
                    //add to images array
                    images.add(targetUri);
                }
            } else {
                //get target Uri
                targetUri = data.getData();
                images.add(targetUri);
            }
            MyUploads uploads = new MyUploads();
            Holder.setImages(images);

            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, uploads).commit();

        } else {
            Toast.makeText(this, "You did not select any photos", Toast.LENGTH_SHORT).show();
        }
    }


}

