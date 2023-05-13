package com.samsung.samsungproject.feature.map.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.repository.ShapeRepository;
import com.samsung.samsungproject.databinding.FragmentMapBinding;
import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.map.presentation.MapHelper;
import com.samsung.samsungproject.feature.map.presentation.MapViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapViewModel viewModel;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<LatLng> polylinePoints;
    List<Polygon> polygonList;
    List<PolygonOptions> downloadedPolygons;
    private final String LOG_TAG = "TAG";
    private LatLng currentLocation;
    private boolean isPaintingActive = false;
    private boolean isCameraMoving = false;
    private SharedPreferences sharedPreferences;
    private User authorizedUser;
    private final static String NICKNAME_KEY = "NICKNAME_KEY";
    private final static String EMAIL_KEY = "EMAIL_KEY";
    private final static String PASSWORD_KEY = "PASSWORD_KEY";
    private final static String ID_KEY = "ID_KEY";
    private final static String ROLE_KEY = "ROLE_KEY";

    Polyline polyline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLocationPermissionGranted())
            launchLocationPermissionRequest();
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        authorizedUser = new User(sharedPreferences.getLong(ID_KEY, 0),
                sharedPreferences.getString(EMAIL_KEY, null),
                sharedPreferences.getString(NICKNAME_KEY, null),
                sharedPreferences.getString(PASSWORD_KEY, null),
                sharedPreferences.getString(ROLE_KEY, null));
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        polylinePoints = new ArrayList<>();
        polygonList = new ArrayList<>();
        downloadedPolygons = new ArrayList<>();
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
                    LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
                    currentLocation = newPoint;
                    if (isPaintingActive) {
                        if (polylinePoints.size() >= 2) {
                            for (int i = 0; i < polylinePoints.size() - 2; i++) {
                                LatLng crossingPoint = MapHelper.crossingPoint(polylinePoints.get(polylinePoints.size() - 1), newPoint,
                                        polylinePoints.get(i), polylinePoints.get(i + 1));
                                if (crossingPoint != null) {
                                    List<LatLng> polygonPoints = new ArrayList<>(polylinePoints.subList(i + 1, polylinePoints.size() - 1));
                                    polylinePoints = polylinePoints.subList(0, i);
                                    polygonList.add(googleMap.addPolygon(new PolygonOptions()
                                            .addAll(polygonPoints)
                                            .strokeWidth(20)
                                            .strokeColor(Color.YELLOW)
                                            .fillColor(Color.YELLOW)));
                                    polylinePoints.add(crossingPoint);
                                }
                            }
                        }

                        polylinePoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
                        polyline.remove();
                        polyline = googleMap.addPolyline(new PolylineOptions()
                                .addAll(polylinePoints)
                                .width(20)
                                .color(Color.RED)
                                .startCap(new RoundCap())
                                .endCap(new RoundCap()));
                    }
                    if (isCameraMoving) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 18);
                        googleMap.animateCamera(cameraUpdate);
                    }
                }

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
        binding.btLeaderboard.setOnClickListener(v -> Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_mapFragment_to_leaderboardFragment));
        binding.btMyLocation.setOnClickListener(v -> {
            if (isGeoEnabled()) {
                if (currentLocation != null) {
                    isCameraMoving = !isCameraMoving;
                    if (isCameraMoving) {
                        binding.btMyLocation.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
                        binding.btMyLocation.setImageTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_F2));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 18);
                        googleMap.animateCamera(cameraUpdate);
                    } else {
                        binding.btMyLocation.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_F2));
                        binding.btMyLocation.setImageTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_33));
                    }
                }
            } else {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        binding.btStart.setOnClickListener(v -> {
            isPaintingActive = !isPaintingActive;
            if (isPaintingActive) {
                binding.btStart.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green));
                binding.btStart.setImageTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_F2));
            } else {
                binding.btStart.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_F2));
                binding.btStart.setImageTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray_33));
                polylinePoints.clear();
                polyline.remove();
                saveAllShapes();
                polyline = googleMap.addPolyline(new PolylineOptions()
                        .addAll(polylinePoints)
                        .width(20)
                        .color(Color.RED)
                        .startCap(new RoundCap())
                        .endCap(new RoundCap()));
                polygonList.clear();
            }
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
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (isLocationPermissionGranted())
            enableMyLocation();
        polyline = googleMap.addPolyline(new PolylineOptions().width(0));
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.style_json));
        googleMap.setTrafficEnabled(false);

        dowloadAllShapes();
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
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private boolean isGeoEnabled() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnabled && isNetworkEnabled;
    }

    private void saveAllShapes() {
        Log.d("TAG", "loading");
        List<Shape> shapeList = new ArrayList<>();
        for (Polygon polygon : polygonList)
            shapeList.add(polygonToShape(polygon));
        ShapeRepository.saveAllShapes(shapeList).enqueue(new Callback<List<Shape>>() {
            @Override
            public void onResponse(Call<List<Shape>> call, Response<List<Shape>> response) {
                Log.d(LOG_TAG, response.code() + "");
            }

            @Override
            public void onFailure(Call<List<Shape>> call, Throwable t) {
                Log.d(LOG_TAG, "failure");
            }
        });
    }
    private void dowloadAllShapes(){
        ShapeRepository.getAllShapes().enqueue(new Callback<List<Shape>>() {
            @Override
            public void onResponse(Call<List<Shape>> call, Response<List<Shape>> response) {
                if(response.isSuccessful())
                    downloadedPolygons = response.body().stream().map(shape -> shapeToPolygon(shape)).collect(Collectors.toList());
                for (PolygonOptions downloadedPolygon : downloadedPolygons) {
                    googleMap.addPolygon(downloadedPolygon);
                }
            }

            @Override
            public void onFailure(Call<List<Shape>> call, Throwable t) {
            }
        });
    }
    public LatLng pointToLatLng(Point point){
        return new LatLng(point.getLatitude(), point.getLongitude());
    }
    public Point latLngToPoint(LatLng latLng){
        return new Point(latLng.latitude, latLng.longitude);
    }
    public Shape polygonToShape(Polygon polygon){
        return new Shape(authorizedUser,
                polygon.getPoints().stream().map(this::latLngToPoint).collect(Collectors.toList()));
    }
    public PolygonOptions shapeToPolygon(Shape shape){
        return new PolygonOptions()
                .addAll(shape.getPointList().stream().map(this::pointToLatLng).collect(Collectors.toList()))
                .fillColor(Color.GREEN)
                .strokeWidth(20)
                .strokeColor(Color.GREEN);
    }
}