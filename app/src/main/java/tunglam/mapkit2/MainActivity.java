package tunglam.mapkit2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //Constant
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 100;

    // Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser mCurrentUser;
    DatabaseReference mUsersRef;

    // View
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("Users");

        //Check if the user already login
        if (mCurrentUser != null) {
            //Move to signed in app
            Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        }

        //If the user does not log in, allow user to login with FirebaseUI
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder().build(),
                        RC_SIGN_IN
                );
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleSignInResponse(resultCode, data);
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Toast toast;

        if (resultCode == RESULT_OK) { //Successfully sign in
            // Start signed-in app
            Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
            MainActivity.this.startActivity(myIntent);
            //FIREBASE CLOUD MESSAGE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
                mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300});
                mNotificationManager.createNotificationChannel(mChannel);
            }

        /*
        * Displaying a notification locally
        */
            MyNotificationManager.getInstance(this).displayNotification("Greetings", "Welcome Back");
            finish();
            return;
        } else { //Failed to sign in
            if (response == null) { //User pressed back button
                toast = Toast.makeText(this, "Sign in was cancelled", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                toast = Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                toast = Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

        }
        toast = Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT);
        toast.show();
    }

    // Public functions
    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        return intent;
    }
}


