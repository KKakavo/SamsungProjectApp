package com.samsung.samsungproject.feature.map.ui;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.repository.ShapeRepository;
import com.samsung.samsungproject.databinding.FragmentMapBinding;
import com.samsung.samsungproject.data.db.dao.shape.ShapeDao;
import com.samsung.samsungproject.data.db.dao.shape.ShapeDaoSqlite;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.map.presentation.MapStatus;
import com.samsung.samsungproject.feature.map.presentation.MapUtils;
import com.samsung.samsungproject.feature.map.presentation.MapViewModel;
import com.samsung.samsungproject.feature.map.ui.spinner.ColorAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private MapViewModel viewModel;
    Resources.Theme theme;
    private GoogleMap googleMap;
    private List<Polygon> drawedPolygonList = new ArrayList<>();
    private final Handler handler = new Handler();
    private final String LOG_TAG = "MapFragment";
    private User authorizedUser;
    private float zIndex = 0;
    private final Integer[] spinner_items = {R.color.red_EB, R.color.orange, R.color.yellow,
            R.color.green_21, R.color.green_6F, R.color.blue_2F, R.color.blue_56,
            R.color.purple, R.color.gray_33, R.color.gray_BD, R.color.white};
    private Polyline currentPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapFragmentArgs args = MapFragmentArgs.fromBundle(requireArguments());
        authorizedUser = args.getUser();
        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        viewModel.location.observe(this, this::renderCamera);
        viewModel.cachedPolygonList.observe(this, this::renderCachedPolygon);
        viewModel.currentPathPoints.observe(this, this::renderCurrentPath);
        viewModel.newPolygonPoints.observe(this, this::renderNewPolygon);
        viewModel.status.observe(this, this::renderStatus);
        if (!isLocationPermissionGranted())
            launchLocationPermissionRequest();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater);
        theme = binding.getRoot().getContext().getTheme();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fr_google_map);
        supportMapFragment.getMapAsync(this);
        binding.btSettings.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).navigate(MapFragmentDirections.actionMapFragmentToSettingsFragment()));
        binding.btLeaderboard.setOnClickListener(v -> Navigation.findNavController(binding.getRoot())
                .navigate(MapFragmentDirections.actionMapFragmentToLeaderboardFragment(authorizedUser)));
        binding.btMyLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isGeoEnabled()) {
                if (isChecked) viewModel.enableCamera();
                else viewModel.disableCamera();
            } else {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                binding.btMyLocation.setChecked(false);
                viewModel.disableCamera();
            }
        });
        binding.btStart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.enableDrawing();
                binding.btDelete.setVisibility(View.VISIBLE);
            } else {
                viewModel.disableDrawing();
                viewModel.saveAllPolygons(authorizedUser);
                binding.btDelete.setVisibility(View.INVISIBLE);
            }
        });
        binding.btDelete.setOnClickListener(v -> {
            currentPath.remove();
            drawedPolygonList.forEach(Polygon::remove);
            drawedPolygonList.clear();
            viewModel.clearSession();
        });
        ColorAdapter adapter = new ColorAdapter(requireContext(), R.layout.spinner_item, Arrays.asList(spinner_items));
        binding.colorSpinner.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (isLocationPermissionGranted())
            enableMyLocation();
        if (currentPath == null) {
            currentPath = googleMap.addPolyline(new PolylineOptions().width(0));
            viewModel.getAllPolygons();
        }
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.style_json));
        googleMap.setTrafficEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewModel.getRecentPolygons();
                handler.postDelayed(this, 5000);
            }
        }, 1000);
    }


    private void launchLocationPermissionRequest() {
        registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        enableMyLocation();
                    } else
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
            if(googleMap != null)
                googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private boolean isGeoEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGPSEnabled && isNetworkEnabled;
    }


    private void renderCamera(LatLng latLng) {
        if(googleMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    private void renderCachedPolygon(List<PolygonOptions> polygonOptions) {
        if(googleMap != null) {
            polygonOptions.forEach(options -> options.zIndex(zIndex++));
            polygonOptions.forEach(googleMap::addPolygon);
        }
    }

    private void renderCurrentPath(List<LatLng> points) {
        if(googleMap != null) {
            Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .width(20)
                    .color(requireContext().getColor((int) binding.colorSpinner.getSelectedItem()))
                    .startCap(new RoundCap())
                    .endCap(new RoundCap())
                    .zIndex(Float.MAX_VALUE));
            currentPath.remove();
            currentPath = polyline;
        }
    }

    private void renderStatus(MapStatus status) {
        switch (status) {
            case SUCCESS:
                viewModel.clearSession();
                currentPath.remove();
                drawedPolygonList.clear();
                break;
            case FAILURE:
                binding.btStart.setChecked(true);
                viewModel.enableDrawing();
                Toast.makeText(requireContext(), "Сервер не отвечает", Toast.LENGTH_LONG).show();
                break;
            case LOADING:
                break;
        }
    }

    private void renderNewPolygon(List<LatLng> points) {
        if(googleMap != null) {
            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(PolyUtil.simplify(points, points.size() / 200.0))
                    .strokeWidth(20)
                    .strokeColor(requireContext().getColor((int) binding.colorSpinner.getSelectedItem()))
                    .fillColor(requireContext().getColor((int) binding.colorSpinner.getSelectedItem()))
                    .zIndex(zIndex++);
            drawedPolygonList.add(googleMap.addPolygon(polygonOptions));
            viewModel.polygonOptionsList.add(polygonOptions);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isLocationEnabled", binding.btMyLocation.isChecked());
        outState.putBoolean("isDrawEnabled", binding.btStart.isChecked());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            binding.btStart.setChecked(savedInstanceState.getBoolean("isDrawEnabled"));
            binding.btMyLocation.setChecked(savedInstanceState.getBoolean("isLocationEnabled"));
        }
    }
}