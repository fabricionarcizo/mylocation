package dk.itu.moapd.mylocation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        assert getArguments() != null;
        final double longitude = getArguments().getDouble("longitude");
        final double latitude = getArguments().getDouble("latitude");

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(latLng)
                        .title("Current Location"));
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
            }
        });

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_map, mapFragment)
                .commit();

        return view;
    }

}
