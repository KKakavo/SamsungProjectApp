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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.common.util.MapUtils;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.SphericalUtil;
import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.repository.ShapeRepository;
import com.samsung.samsungproject.data.repository.UserRepository;
import com.samsung.samsungproject.databinding.FragmentMapBinding;
import com.samsung.samsungproject.domain.db.AppDbOpenHelper;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDao;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDaoSqlite;
import com.samsung.samsungproject.domain.model.Point;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.map.presentation.MapHelper;
import com.samsung.samsungproject.feature.map.presentation.MapViewModel;
import com.samsung.samsungproject.feature.map.presentation.ShapeDbThread;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
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
    List<PolygonOptions> polygonList;
    private final String LOG_TAG = "TAG";
    private LatLng currentLocation;
    private boolean isPaintingActive = false;
    private boolean isCameraMoving = false;
    private User authorizedUser;
    private ShapeDao shapeDao;
    private MapFragmentArgs args;


    Polyline polyline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isLocationPermissionGranted())
            launchLocationPermissionRequest();
        args = MapFragmentArgs.fromBundle(requireArguments());
        authorizedUser = args.getUser();
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        polylinePoints = new ArrayList<>();
        polygonList = new ArrayList<>();
        shapeDao = new ShapeDaoSqlite(requireContext());
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
                                    PolygonOptions options = new PolygonOptions()
                                            .addAll(polygonPoints)
                                            .strokeWidth(20)
                                            .strokeColor(Color.YELLOW)
                                            .fillColor(Color.YELLOW);
                                    googleMap.addPolygon(options);
                                    polygonList.add(options);
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
        binding.btDelete.setOnClickListener(v -> {System.out.println(shapeDao.findAll());
            System.out.println(shapeDao.getSize() + "");
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
        //Thread thread = new Thread(new ShapeDbThread(requireContext(), googleMap));
        //thread.start();
        downloadRecentShapes();
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
        List<Shape> shapeList = new ArrayList<>();
        long score = authorizedUser.getScore();
        for (PolygonOptions polygon : polygonList) {
            shapeList.add(MapHelper.polygonToShape(polygon, authorizedUser));
            score += Math.round(SphericalUtil.computeArea(polygon.getPoints()));
            System.out.println(Math.round(SphericalUtil.computeArea(polygon.getPoints())));
        }
        shapeList.forEach(shape -> shapeDao.insert(shape));
        shapeDao.findAll().forEach(shape -> googleMap.addPolygon(MapHelper.shapeToPolygon(shape)));
        ShapeRepository.saveAllShapes(shapeList).enqueue(new Callback<List<Shape>>() {
            @Override
            public void onResponse(Call<List<Shape>> call, Response<List<Shape>> response) {
            }

            @Override
            public void onFailure(Call<List<Shape>> call, Throwable t) {
                Log.d(LOG_TAG, "failure");
            }
        });
        UserRepository.updateUserScoreById(authorizedUser.getId(), score).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    private void downloadRecentShapes(){
        ShapeRepository.getRecentShapes(shapeDao.getSize()).enqueue(new Callback<List<Shape>>() {
            @Override
            public void onResponse(Call<List<Shape>> call, Response<List<Shape>> response) {
                if(response.isSuccessful()) {
                    response.body().forEach(shapeDao::insert);
                    List<PolygonOptions> polygonOptionsList = new ArrayList<>(
                            response.body().stream().map(MapHelper::shapeToPolygon).collect(Collectors.toList()));
                    for (PolygonOptions polygonOptions : polygonOptionsList) {
                        googleMap.addPolygon(polygonOptions);
                        System.out.println(SphericalUtil.computeArea(polygonOptions.getPoints()));
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Shape>> call, Throwable t) {
            }
        });
    }
}