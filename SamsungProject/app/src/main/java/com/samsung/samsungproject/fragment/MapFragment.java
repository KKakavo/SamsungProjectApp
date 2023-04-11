package com.samsung.samsungproject.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.samsung.samsungproject.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location lastLocation;
    private Boolean isLocationPermissionGranted;
    private final String LOG_TAG = "TAG";

    private final int LOCATION_PERMISSION_REQUEST = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fr_google_map);
        supportMapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        checkLocationPermisson();
        updateLocationUI();
        getLastLocation();
        Location location = googleMap.getMyLocation();

        //Toast.makeText(getContext(), location.getLongitude() + " " + location.getAltitude(), Toast.LENGTH_LONG).show();
    }

    private void checkLocationPermisson() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            isLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST:
                if(grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    isLocationPermissionGranted = true;
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
        updateLocationUI();
    }

    private void updateLocationUI(){
        if(googleMap == null)
            return;
        try {
            if (isLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                checkLocationPermisson();
            }
        } catch (SecurityException e){
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void getLastLocation() {
        try {
            if (isLocationPermissionGranted) {
                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        lastLocation = task.getResult();
                        if (lastLocation != null)
                            Toast.makeText(getContext(), lastLocation.getLongitude() + " " + lastLocation.getLatitude(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (SecurityException e){
            Log.e(LOG_TAG, e.getMessage());
        }
    }


}