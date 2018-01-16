package tunglam.mapkit2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// start this activity by tapping a marker
public class CustomizeLocation extends AppCompatActivity {

    ImageView imageView;
    EditText name;
    EditText address;
    int id;
    static double lat;
    static double lng;
    static String nameString;
    static String addressString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_location);
        Intent intent = getIntent();

        //get fields
        imageView = findViewById(R.id.img_icon);
        name = findViewById(R.id.edit_text_custom_name);
        address = findViewById(R.id.edit_text__custom_address);

        // set fields
        id = intent.getIntExtra("id", R.drawable.star); // icon id got from IconPicker
        if (intent.getStringExtra("activity") != null && intent.getStringExtra("activity").equals("maps")) {
            lat = intent.getDoubleExtra("lat", 0);
            lng = intent.getDoubleExtra("lng", 0);
            addressString = intent.getStringExtra("address");
        }
        //}
        if (addressString == null) addressString = "";
        if (nameString == null) nameString = "";
        imageView.setImageResource(id);
        name.setText(nameString);
        address.setText(addressString);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nameString = name.getText().toString();
    }

    public void onChooseIconButtonTap(View view) {
        startActivity(new Intent(this, IconPicker.class)); // start IconPicker for an ID
    }

    public void onSaveButtonTap(View view) {
        // get location list from local storage to add a new location
        Type type = new TypeToken<List<UserPlace>>(){}.getType();
        List<UserPlace> userPlaces = new ArrayList<>();
        File file = new File(getFilesDir(), "userdata.json");
        try {
            FileReader fileReader = new FileReader(file);
            userPlaces = new Gson().fromJson(fileReader, type);
            if (userPlaces == null) {
                userPlaces = new ArrayList<>();
            }
            userPlaces.add(new UserPlace(name.getText().toString(), address.getText().toString(), id, lat, lng));
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // then save the list
        try {
            FileWriter fileWriter = new FileWriter(file);
            new Gson().toJson(userPlaces, fileWriter);
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, MapsActivity.class));
    }
}
