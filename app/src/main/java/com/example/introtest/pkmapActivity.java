package com.example.introtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.opencsv.CSVReader;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;


public class pkmapActivity extends Activity implements MapView.POIItemEventListener {

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Intent intent = new Intent(getApplicationContext(), PkInfoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    ArrayList<MapPoint> mapPoint = new ArrayList<MapPoint>();

    ArrayList<String> ItemName = new ArrayList<String>();

    ArrayList<String> addr = new ArrayList<String>();

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((TextView) mCalloutBalloon.findViewById(R.id.ball_title)).setText(poiItem.getItemName());
            ((TextView) mCalloutBalloon.findViewById(R.id.ball_addr)).setText(addr.get(poiItem.getTag()));
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parkinglot_map);

        final MapView mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        // 중심점 변경
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.75180342846313, 128.8760571298616), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(4, true);

        // 줌 인
        mapView.zoomIn(true);

        // 줌 아웃
        mapView.zoomOut(true);

        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());


        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getResources().openRawResource(R.raw.data));
            BufferedReader read = new BufferedReader(inputStreamReader);
            CSVReader reader = new CSVReader(read);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[28].equals("위도")) continue;
                mapPoint.add(MapPoint.mapPointWithGeoCoord(Double.parseDouble(nextLine[28]), Double.parseDouble(nextLine[29])));
                ItemName.add(nextLine[1]);
                addr.add(nextLine[5]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        MapPOIItem marker[] = new MapPOIItem[mapPoint.size()];

        for(int i = 0; i < mapPoint.size(); i++) {
            marker[i] = new MapPOIItem();
            marker[i].setItemName(ItemName.get(i));
            marker[i].setTag(i);
            marker[i].setMapPoint(mapPoint.get(i));

            // 마커 모양
            marker[i].setMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker[i].setCustomImageResourceId(R.drawable.marker);

            // 마커를 클릭했을때, 마커 모양
            marker[i].setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker[i].setCustomSelectedImageResourceId(R.drawable.sl_marker);

            mapView.addPOIItem(marker[i]);
        }

        mapView.setPOIItemEventListener(this);

        ImageButton btn_zoomin = (ImageButton) findViewById(R.id.btn_zoomin);
        ImageButton btn_zoomout = (ImageButton) findViewById(R.id.btn_zoomout);

        btn_zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int zoomLevel = mapView.getZoomLevel();
                zoomLevel--;
                mapView.setZoomLevel(zoomLevel, true);
            }
        });

        btn_zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int zoomLevel = mapView.getZoomLevel();
                zoomLevel++;
                mapView.setZoomLevel(zoomLevel, true);
            }
        });

        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_map_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}