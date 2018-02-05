package me.shihab.caranimation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 1/12/2017.
 */

public class MapUtils {

    public static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    // // TODO: 1/22/2017 please check
    void fixZoomProblem(LatLng source, LatLng distance, List<LatLng> points, GoogleMap googleMap) {

        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        bc.include(source);
        bc.include(distance);

    /*  for (LatLng item : points) {
            bc.include(item);
        }
*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
    }

    public static void fitZoomWithScreen(Location source, Location distance, GoogleMap googleMap, Context context) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //the include method will calculate the min and max bound.
        builder.include(new LatLng(source.getLatitude(), source.getLongitude()));
        builder.include(new LatLng(distance.getLatitude(), distance.getLongitude()));

        //        builder.include(marker3.getPosition());
        //        builder.include(marker4.getPosition());
        LatLngBounds bounds = builder.build();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels - 250;
        int padding = (int) (width * 0.20);
        // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        googleMap.animateCamera(cu);

    }

    // https://stackoverflow.com/questions/24812483/how-to-create-bounds-of-a-android-polyline-in-order-to-fit-the-screen

    public static CameraUpdate moveToBounds(Context context, Polyline p) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> arr = p.getPoints();
        for (int i = 0; i < arr.size(); i++) {
            builder.include(arr.get(i));
        }
        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels

        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels / 2;

//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);


        return cu;
//        mMap.animateCamera(cu);
    }
    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
}
