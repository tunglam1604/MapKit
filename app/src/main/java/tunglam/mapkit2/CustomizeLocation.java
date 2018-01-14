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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_location);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0); // icon id got from IconPicker

        // Set image, get fields
        imageView = findViewById(R.id.img_icon);
        name = findViewById(R.id.edit_text_custom_name);
        address = findViewById(R.id.edit_text__custom_address);
        imageView.setImageResource(id);
    }

    public void onChooseIconButtonTap(View view) {
        startActivity(new Intent(this, IconPicker.class)); // start IconPicker for an ID
    }

    public void onSaveButtonTap(View view) {
        // get location list from local storage to add a new location
        Type type = new TypeToken<List<UserPlace>>(){}.getType();
        List<UserPlace> userPlaces = new ArrayList<>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(openFileInput("userdata.json"));
            userPlaces = new Gson().fromJson(inputStreamReader, type);
            if (userPlaces == null) {
                userPlaces = new ArrayList<>();
                userPlaces.add(new UserPlace(name.getText().toString(), address.getText().toString(), id));
            }
            inputStreamReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // then save the list
        try {
            FileOutputStream fileOutputStream = openFileOutput("userdata.json", Context.MODE_PRIVATE);
            new Gson().toJson(userPlaces, new OutputStreamWriter(fileOutputStream));
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, MapsActivity.class));
    }
}
