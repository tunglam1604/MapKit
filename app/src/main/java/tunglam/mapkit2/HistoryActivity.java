package tunglam.mapkit2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class HistoryActivity extends AppCompatActivity {

//BOTTOM NAVIGATION BAR
private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
        = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_account:
                startActivity(new Intent(HistoryActivity.this,DashBoard.class));
                return true;
            case R.id.navigation_map:
                startActivity(new Intent(HistoryActivity.this,MapsActivity.class));
                return true;
            case R.id.navigation_history:
                startActivity(new Intent(HistoryActivity.this,HistoryActivity.class));
                return true;
        }
        return false;
    }
};

    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
