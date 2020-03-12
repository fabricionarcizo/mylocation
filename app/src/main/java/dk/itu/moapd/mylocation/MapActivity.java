package dk.itu.moapd.mylocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment);

        if (fragment == null) {
            double longitude = getIntent().getDoubleExtra("longitude", 0);
            double latitude = getIntent().getDoubleExtra("latitude", 0);

            Bundle bundle = new Bundle();
            bundle.putDouble("longitude", longitude);
            bundle.putDouble("latitude", latitude);

            fragment = new MapFragment();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        }
    }

}
