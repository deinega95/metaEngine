package com.meta_engine.common.utils;

import android.graphics.Point;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import static com.meta_engine.services.GeoPositionServiceKt.PATH_WIDTH;

public class UtilsOnJava {

    static public float metertopixel(GoogleMap map) {
        LatLng center = map.getCameraPosition().target;
        float requiredWidth = PATH_WIDTH; // in meters
        Projection proj = map.getProjection();
        Point pointCenter = proj.toScreenLocation(center); // point in pixels
        LatLng neighbor = proj.fromScreenLocation(new Point(pointCenter.x + 1000, pointCenter.y));
        float[] distance = new float[1];
        Location.distanceBetween(center.latitude, center.longitude, neighbor.latitude, neighbor.longitude, distance); // return distance in meters
        float pixelsWidth = requiredWidth / (distance[0] / 1000f);
        return pixelsWidth;
    }

}
