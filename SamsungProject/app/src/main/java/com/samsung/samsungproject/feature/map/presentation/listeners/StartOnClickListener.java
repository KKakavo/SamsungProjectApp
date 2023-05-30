package com.samsung.samsungproject.feature.map.presentation.listeners;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.samsung.samsungproject.R;
import com.samsung.samsungproject.data.repository.ShapeRepository;
import com.samsung.samsungproject.databinding.FragmentMapBinding;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDao;
import com.samsung.samsungproject.domain.db.dao.shape.ShapeDaoSqlite;
import com.samsung.samsungproject.domain.model.Shape;
import com.samsung.samsungproject.domain.model.User;
import com.samsung.samsungproject.feature.map.presentation.MapHelper;
import com.samsung.samsungproject.feature.map.ui.MapFragmentArgs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartOnClickListener implements View.OnClickListener {
    private final FragmentMapBinding binding;
    private final Handler handler;
    private static boolean isPaintingActive;
    private final ShapeDao shapeDao;
    private final Context context;
    private final User user;
    private final Resources.Theme theme;

    public StartOnClickListener(Fragment fragment, Handler handler) {
        this.binding = FragmentMapBinding.inflate(fragment.getLayoutInflater());
        this.user = MapFragmentArgs.fromBundle(fragment.requireArguments()).getUser();
        this.handler = handler;
        this.context = binding.getRoot().getContext();
        this.shapeDao = new ShapeDaoSqlite(context);
        this.theme = context.getTheme();
    }

    @Override
    public void onClick(View v) {
        if (!isPaintingActive) {
            buttonOn();
            isPaintingActive = !isPaintingActive;
        } else {
            //saveAllShapes();
        }
    }
    /*private void saveAllShapes() {
        Thread thread = new Thread(() -> {
            List<Shape> shapeList = new ArrayList<>();
            long score = user.getScore();
            for (Polygon polygon : polygonList) {
                PolygonOptions polygonOptions = new PolygonOptions().addAll(PolyUtil.simplify(polygon.getPoints(), polygon.getPoints().size() / 200.0))
                        .fillColor(polygon.getFillColor());
                shapeList.add(MapHelper.polygonToShape(polygonOptions, user));
                score += Math.round(SphericalUtil.computeArea(polygon.getPoints()));
            }
            if (shapeList.size() > 0) {
                ShapeRepository.saveAllShapes(shapeList, score).enqueue(new Callback<List<Shape>>() {
                    @Override
                    public void onResponse(Call<List<Shape>> call, Response<List<Shape>> response) {
                        if (response.isSuccessful()) {
                            response.body().forEach(shape -> shapeDao.insert(shape));
                            ShapeRepository.controlSum += response.body().size();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                buttonOff();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Shape>> call, Throwable t) {
                        Log.d(LOG_TAG, "failure");
                    }
                });

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        buttonOff();
                    }
                });
            }
        });
        thread.start();
    }*/
    private void buttonOn(){
        binding.btStart.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
        binding.btStart.setImageTintList(ContextCompat.getColorStateList(context, R.color.gray_F2));
        binding.btStart.setImageResource(R.drawable.ic_done);
        binding.btDelete.setVisibility(View.VISIBLE);
    }
    private void buttonOff(){
        TypedValue background = new TypedValue();
        TypedValue icon = new TypedValue();
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, background, true);
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, icon, true);
        binding.btStart.setBackgroundTintList(ContextCompat.getColorStateList(context, background.resourceId));
        binding.btStart.setImageTintList(ContextCompat.getColorStateList(context, icon.resourceId));
        binding.btStart.setImageResource(R.drawable.ic_drawing);
        binding.btDelete.setVisibility(View.INVISIBLE);
       /* polylinePoints.clear();
        polyline.remove();
        polygonList.clear();*/
        isPaintingActive = !isPaintingActive;
    }
}
