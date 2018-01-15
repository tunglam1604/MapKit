package tunglam.mapkit2;

/**
 * Created by User on 1/14/2018.
 */


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

      //Generate Token
        String token = FirebaseInstanceId.getInstance().getToken();

        //Display Token in Log
        Log.d("MyRefreshedToken", token);
    }
}