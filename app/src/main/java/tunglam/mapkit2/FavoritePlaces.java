package tunglam.mapkit2;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_places);

        // get location list from local storage
        Type type = new TypeToken<List<UserPlace>>(){}.getType();
        List<UserPlace> userPlaces = new ArrayList<>();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(openFileInput("userdata.json"));
            userPlaces = new Gson().fromJson(inputStreamReader, type);
            inputStreamReader.close();
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

    }
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
