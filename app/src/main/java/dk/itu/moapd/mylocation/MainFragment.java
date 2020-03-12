package dk.itu.moapd.mylocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainFragment extends Fragment {

    private double mLongitude;
    private double mLatitude;

    private EditText mEditLongitude;
    private EditText mEditLatitude;
    private EditText mEditAddress;

    private final ArrayList<String> mPermissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 5000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mEditLongitude = view.findViewById(R.id.longitude);
        mEditLongitude.setKeyListener(null);

        mEditLatitude = view.findViewById(R.id.latitude);
        mEditLatitude.setKeyListener(null);

        mEditAddress = view.findViewById(R.id.address);
        mEditAddress.setKeyListener(null);

        Button mapButton = view.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putExtra("longitude", mLongitude);
                intent.putExtra("latitude", mLatitude);
                startActivity(intent);
            }
        });

        mPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        mPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        ArrayList<String> permissionsToRequest = permissionsToRequest(mPermissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(
                        permissionsToRequest.toArray(new String[0]), ALL_PERMISSIONS_RESULT);
            }
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;

                for (Location location : locationResult.getLocations()) {
                    mLongitude = location.getLongitude();
                    mLatitude = location.getLatitude();
                    mEditLongitude.setText(String.valueOf(mLongitude));
                    mEditLatitude.setText(String.valueOf(mLatitude));
                    mEditAddress.setText(getAddress(mLongitude, mLatitude));
                }
            }
        };

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(
                        Objects.requireNonNull(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> permissions) {

        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions)
            if (!hasPermission(permission))
                result.add(permission);

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return Objects.requireNonNull(getActivity())
                    .checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        return true;
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(
                    Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION
               ) != PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(
                    Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_COARSE_LOCATION
               ) != PackageManager.PERMISSION_GRANTED;
    }

    //@SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (checkPermission())
            return;

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient
                .removeLocationUpdates(mLocationCallback);
    }

    private String getAddress(double longitude, double latitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        StringBuilder stringBuilder = new StringBuilder();

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                stringBuilder.append(address.getAddressLine(0)).append("\n");
                stringBuilder.append(address.getLocality()).append("\n");
                stringBuilder.append(address.getPostalCode()).append("\n");
                stringBuilder.append(address.getCountryName());
            } else
                return "No address found";
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return stringBuilder.toString();
    }

}
