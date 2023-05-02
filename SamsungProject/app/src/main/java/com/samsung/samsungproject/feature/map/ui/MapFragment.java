package com.samsung.samsungproject.feature.map.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.internal.IStreetViewPanoramaDelegate;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.samsung.samsungproject.R;
import com.samsung.samsungproject.databinding.FragmentMapBinding;
import com.samsung.samsungproject.feature.map.presentation.MapViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapViewModel viewModel;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<PolygonOptions> polygonList;
    private LinkedList<LatLng> polylinePoints;
    private LatLng currentLocation;
    private final String LOG_TAG = "TAG";

    Polyline polyline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLocationPermissionGranted())
            launchLocationPermissionRequest();
        //enableLocationSettings();
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        polylinePoints = new LinkedList<>();
        polygonList = new ArrayList<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        LocationRequest locationRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 500)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setWaitForAccurateLocation(true)
                .build();
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    if (polylinePoints.size() >= 2) {
                        LatLng[] points = PolylineSelfCrossingPoint(new LatLng(location.getLatitude(), location.getLongitude()));
                        if (points != null) {
                            List<LatLng> polygonPoints = new LinkedList<>
                                    (polylinePoints.subList(polylinePoints.indexOf(points[0]) + 1, polylinePoints.size() - 1));
                            Log.d(LOG_TAG, location.getLatitude() + " " + location.getLongitude()
                                    + "\n" + points[1].latitude + " " + points[1].longitude
                                    + "\n" + SphericalUtil.computeArea(polygonPoints));
                            polylinePoints = new LinkedList<>(polylinePoints.subList(0, polylinePoints.indexOf(points[0])));
                            polygonList.add(new PolygonOptions()
                                    .addAll(polygonPoints)
                                    .strokeWidth(20)
                                    .strokeColor(Color.YELLOW)
                                    .fillColor(Color.YELLOW));
                            Polygon polygon = googleMap.addPolygon(polygonList.get(polygonList.size() - 1));
                            polylinePoints.add(points[1]);
                        }
                    }
                    polylinePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                }
                polyline.remove();
                polyline = googleMap.addPolyline(new PolylineOptions()
                        .addAll(polylinePoints)
                        .width(20)
                        .color(Color.RED)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap()));

            }
        };
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fr_google_map);
        supportMapFragment.getMapAsync(this);
        binding.btClear.setOnClickListener(v -> {
            polyline.remove();
            polylinePoints.clear();
            polyline.setPoints(polylinePoints);
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (isLocationPermissionGranted())
            enableMyLocation();
        polyline = googleMap.addPolyline(new PolylineOptions().width(0));

        //googleMap.setBuildingsEnabled(false);
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.style_json));

        googleMap.setTrafficEnabled(false);
        //googleMap.moveCamera(new ICameraUpdateFactoryDelegate().newCameraPosition());
        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions().fillColor(Color.GREEN)
                .add(new LatLng(0, 0), new LatLng(1, 0),
                        new LatLng(1,1), new LatLng(0,1),
                        new LatLng(0.5, 0.5)));
        Polygon polygon2 = googleMap.addPolygon(new PolygonOptions().fillColor(Color.GREEN)
                .add(new LatLng(0, 0), new LatLng(-0.5, 0.5),
                        new LatLng(0,1) ));


    }


    private void launchLocationPermissionRequest() {
        registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result)
                        enableMyLocation();
                    else
                        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                }).launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean isLocationPermissionGranted() {

        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private void enableMyLocation() {
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

   /* private void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(requireActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
            } catch (ApiException apiException) {

                switch (apiException.getStatusCode()) {

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) apiException;
                            resolvableApiException.startResolutionForResult(requireActivity(), 2);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(LOG_TAG, "Location is now available on this device");
                }

            }
        });

    }*/

    private LatLng[] PolylineSelfCrossingPoint(LatLng newLocationPoint) {
        LatLng AEndPoint = newLocationPoint;
        LatLng AStartPoint = polylinePoints.peekLast();
        double v = AEndPoint.latitude - AStartPoint.latitude;
        double w = AEndPoint.longitude - AStartPoint.longitude;
        LatLng BStartPoint = null;
        LatLng BEndPoint = null;
        for (LatLng point : polylinePoints) {
            BStartPoint = BEndPoint;
            BEndPoint = point;
            if (BStartPoint != null && BEndPoint != AStartPoint){
                double v2 = BEndPoint.latitude - BStartPoint.latitude;
                double w2 = BEndPoint.longitude - BStartPoint.longitude;
                double lenA = Math.sqrt(v * v + w * w);
                double lenB = Math.sqrt(v2 * v2 + w2 * w2);
                double diff = 0.000001;
                if(Math.abs(v/lenA - v2/lenB) < diff
                        && Math.abs(w/lenA - w2/lenB) < diff)
                    continue;
                double t2 = (-w * BStartPoint.latitude + w * AStartPoint.latitude + v * BStartPoint.longitude - v * AStartPoint.longitude) / (w * v2 - v * w2);
                double t = (BStartPoint.latitude - AStartPoint.latitude + v2 * t2) / v;
                if(t > 0 && t < 1 && t2 > 0 && t2 < 1){
                    return new LatLng[]{BStartPoint, new LatLng(BStartPoint.latitude + v2 * t2, BStartPoint.longitude + w2 * t2)};
                }
            }
        }
        return null;
    }
}