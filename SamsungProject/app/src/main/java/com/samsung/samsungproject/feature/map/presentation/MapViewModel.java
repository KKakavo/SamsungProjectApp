package com.samsung.samsungproject.feature.map.presentation;

import android.app.Application;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.samsung.samsungproject.data.repository.ShapeRepository;
import com.samsung.samsungproject.data.repository.communication.Result;
import com.samsung.samsungproject.domain.model.User;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends AndroidViewModel {
    private final MutableLiveData<LatLng> _location = new MutableLiveData<>();
    public LiveData<LatLng> location = _location;
    private final MutableLiveData<List<PolygonOptions>> _cachedPolygonList = new MutableLiveData<>();
    public LiveData<List<PolygonOptions>> cachedPolygonList = _cachedPolygonList;
    private final MutableLiveData<List<LatLng>> _currentPathPoints = new MutableLiveData<>();
    public LiveData<List<LatLng>> currentPathPoints = _currentPathPoints;
    private final MutableLiveData<List<LatLng>> _newPolygonPoints = new MutableLiveData<>();
    public LiveData<List<LatLng>> newPolygonPoints = _newPolygonPoints;
    public List<PolygonOptions> polygonOptionsList = new ArrayList<>();
    private final MutableLiveData<MapStatus> _status = new MutableLiveData<>();
    public LiveData<MapStatus> status = _status;
    private final LocationCallback drawingLocationCallback;
    private final LocationCallback cameraLocationCallback;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationRequest locationRequest;
    private final String LOG_TAG = "MapViewModel";
    private final ShapeRepository repository;
    public boolean isLocationCheckChange = false;
    public MapViewModel(@NonNull Application application) {
        super(application);
        repository = new ShapeRepository(application);
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(getApplication().getApplicationContext());
        _currentPathPoints.setValue(new ArrayList<>());
        locationRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 500)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setWaitForAccurateLocation(true)
                .build();
        drawingLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    List<LatLng> currentPath = _currentPathPoints.getValue();
                    LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    if (currentPath.size() > 2) {
                        for (int i = 0; i < currentPath.size() - 2; i++) {
                            LatLng crossingPoint = MapUtils.crossingPoint(currentPath.get(currentPath.size() - 1), currentPosition,
                                    currentPath.get(i), currentPath.get(i + 1));
                            if (crossingPoint != null) {
                                List<LatLng> polygonPoints = new ArrayList<>(currentPath.subList(i + 1, currentPath.size() - 1));
                                currentPath = new ArrayList<>(currentPath.subList(0, i));
                                polygonPoints.add(polygonPoints.get(0));
                                _newPolygonPoints.setValue(polygonPoints);
                                currentPath.add(crossingPoint);
                            }
                        }
                    }
                        currentPath.add(currentPosition);
                        _currentPathPoints.setValue(currentPath);
                }
            }
        };
        cameraLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    _location.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        };
    }
    public void     getRecentPolygons(){
        repository.getRecentPolygons(result -> {
            if (result instanceof Result.Success){
                List<PolygonOptions> data = ((Result.Success<List<PolygonOptions>>) result).data;
                if(data.size() > 0)
                    _cachedPolygonList.setValue(data);
            }
        });
    }
    public void getAllPolygons(){
        repository.getAllPolygons(result -> {
            if(result instanceof Result.Success){
                _cachedPolygonList.setValue(((Result.Success<List<PolygonOptions>>) result).data);
            }
        });
    }
    public void saveAllPolygons(User user){
        repository.saveAllPolygons(polygonOptionsList, user,
                result -> {
                    if (result instanceof Result.Success)
                        _status.setValue(MapStatus.SUCCESS);
                    else
                        _status.setValue(MapStatus.FAILURE);
                });
    }
    public void clearSession(){
        polygonOptionsList.clear();
        _currentPathPoints.setValue(new ArrayList<>());
    }
    public void enableCamera(){
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, cameraLocationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
    public void disableCamera(){
        fusedLocationProviderClient.removeLocationUpdates(cameraLocationCallback);
    }
    public void enableDrawing(){
        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, drawingLocationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
    public void disableDrawing(){
        fusedLocationProviderClient.removeLocationUpdates(drawingLocationCallback);
    }
    public void setCameraLocation(LatLng latLng){
        _location.setValue(latLng);
        isLocationCheckChange = true;
    }

}
