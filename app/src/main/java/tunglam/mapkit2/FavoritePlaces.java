package tunglam.mapkit2;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritePlaces extends AppCompatActivity {
    ListView listView;
    String[] titles;
    String[] subtitles;
    int[] images;
    List<UserPlace> userPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_places);

        // navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().findItem(R.id.navigation_favorite).setChecked(true);

        // get location list from local storage
        Type type = new TypeToken<List<UserPlace>>(){}.getType();
        userPlaces = new ArrayList<>();
        File file = new File(getFilesDir(), "userdata.json");
        try {
            FileReader fileReader = new FileReader(file);
            userPlaces = new Gson().fromJson(fileReader, type);
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userPlaces != null) {
            int length = userPlaces.size();
            titles = new String[length];
            subtitles = new String[length];
            images = new int[length];
            for (int i = 0; i < length; i++) {
                titles[i] = userPlaces.get(i).getName();
                subtitles[i] = userPlaces.get(i).getAddress();
                images[i] = userPlaces.get(i).getImage();
            }
            listView = findViewById(R.id.list_view); // list view in this activity's layout
            RowAdapter adapter = new RowAdapter(this, titles, images, subtitles);
            listView.setAdapter(adapter); // set custom list view's row
        }
        // on row tap
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                intent.putExtra("pos", i);
                intent.putExtra("class", "favorite");
                startActivity(intent);
            }
        });

        // on row long tap
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
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
                    userPlaces.remove(i);
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
                finish();
                startActivity(getIntent());
                return false;
            }
        });

    }

    //BOTTOM NAVIGATION BAR
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_account:
                    startActivity(new Intent(FavoritePlaces.this,DashBoard.class));
                    return true;
                case R.id.navigation_map:
                    startActivity(new Intent(FavoritePlaces.this,MapsActivity.class));
                    return true;
                case R.id.navigation_chat:
                    startActivity(new Intent(FavoritePlaces.this,ChatActivity.class));
                    return true;
                case R.id.navigation_favorite:
                    startActivity(new Intent(FavoritePlaces.this, FavoritePlaces.class));
                    return true;
            }
            return false;
        }
    };
}

class RowAdapter extends ArrayAdapter<String> {

    Context context;
    int[] images;
    String[] titles;
    String[] subtitles;

    public RowAdapter(@NonNull Context context, String[] titles, int[] images, String[] subtitles) {
        super(context, R.layout.favorite_places_row, R.id.title, titles); // custom list view's row
        this.context = context;
        this.images = images;
        this.titles = titles;
        this.subtitles = subtitles;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) { // inflate only once for each row to optimize speed & heap usage
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.favorite_places_row, parent, false);
        }
        ImageView placeImage = row.findViewById(R.id.place_image);
        TextView title = row.findViewById(R.id.title);
        TextView subtitle = row.findViewById(R.id.subtitle);

        placeImage.setImageResource(images[pos]);
        title.setText(titles[pos]);
        subtitle.setText(subtitles[pos]);

        return row;
    }

}
