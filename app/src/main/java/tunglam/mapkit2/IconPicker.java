package tunglam.mapkit2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

// Choose icon to customize location picked by tapping a marker
public class IconPicker extends AppCompatActivity {

    ListView listView;
    String[] names = {"Favorite", "Home", "Hospital", "Park", "School", "Shopping", "Restaurant"};
    int[] icons = {R.drawable.star, R.drawable.home, R.drawable.hospital, R.drawable.park, R.drawable.school, R.drawable.shopping, R.drawable.restaurant};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_picker);

        listView = findViewById(R.id.icon_list); // list view in this activity's layout
        IconAdapter adapter = new IconAdapter(this, names, icons);
        listView.setAdapter(adapter); // set custom list view's row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Choose icon, send this icon to CustomizeLocation
                Intent intent = new Intent(getBaseContext(), CustomizeLocation.class);
                intent.putExtra("id", icons[i]);
                startActivity(intent);
            }
        });
    }

}

class IconAdapter extends ArrayAdapter<String> {

    Context context;
    int[] icons;
    String[] names;

    public IconAdapter(@NonNull Context context, String[] names, int[] icons) {
        super(context, R.layout.icon_picker_row, R.id.title, names); // custom list view's row
        this.context = context;
        this.icons = icons;
        this.names = names;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) { // inflate only once for each row to optimize speed & heap usage
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.icon_picker_row, parent, false);
        }
        ImageView icon = row.findViewById(R.id.icon);
        TextView icon_name = row.findViewById(R.id.icon_name);

        icon.setImageResource(icons[pos]);
        icon_name.setText(names[pos]);
        return row;
    }
}
