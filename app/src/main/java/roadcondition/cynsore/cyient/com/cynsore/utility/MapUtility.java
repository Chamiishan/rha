package roadcondition.cynsore.cyient.com.cynsore.utility;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roadcondition.cynsore.cyient.com.cynsore.direction.PathJSONParser;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerAsyncTask;
import roadcondition.cynsore.cyient.com.cynsore.network.ServerHelper;

public class MapUtility {

    //    private static MapUtility mapUtility;
    public static final int TOM_TOM_API = 0;
    public static final int GOOGLE_API = 1;

    private final String TAG = MapUtility.class.getName();

    public MapUtility() {
    }

//    public static MapUtility getInstance(MapUtilityData utilityData) {
//        if (mapUtility == null)
//            mapUtility = new MapUtility();
//        return mapUtility;
//    }

    public void drawRoute(GoogleMap map, LatLng src, LatLng dest, String wayPoints, int useApi, Context context) {
        map.clear();
//        routeJson = null;
//        mImgGraph.setVisibility(View.INVISIBLE);
//        setMyLoc = false;
//        mImgMyLoc.setVisibility(View.VISIBLE);

        final double slat = src.latitude;
        final double slng = src.longitude;

        com.google.android.gms.maps.model.LatLng barcelona = new com.google.android.gms.maps.model.LatLng(slat, slng);
        MarkerOptions mo = new MarkerOptions()
                .position(barcelona)
                .title("Source")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(mo);

        final double dlat = dest.latitude;
        final double dlng = dest.longitude;

        LatLng madrid = new LatLng(dlat, dlng);
        map.addMarker(new MarkerOptions().position(madrid).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        //Define list to get all latlng for the route

        Handler handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
//                List<LatLng> path = new ArrayList<>();
                String origin = slat + "," + slng;
                String dest = dlat + "," + dlng;
//                String googleApiKey = "AIzaSyC7V9uWEfYabkwgSNF2zyDedJcHilCDIpM";
                String googleApiKey = "AIzaSyCFZyKKvqUSacq3gDLw5rCfgFohQMYWyKI";
                String tomtomApiKey = "93lGMTmQuN5JmgZqR0HGb3TwYvrDSz6i";

//                    URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
//                            + origin + "&destination=" + dest + "&travel_mode=DRIVING" +
//                            "&key=" + googleApiKey);
                String data = "";
                InputStream iStream = null;
                HttpURLConnection urlConnection = null;
                try {
                    URL url = null;
                    if (useApi == GOOGLE_API) {
                        /* for google */
                        url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
                                + origin + "&destination=" + dest + "&travel_mode=DRIVING" +
                                "&key=" + googleApiKey);
                    } else if (useApi == TOM_TOM_API) {
                        /* for tomtom */
                        if (wayPoints != null && wayPoints.length() > 0) {
                            url = new URL("https://api.tomtom.com/routing/1/calculateRoute/" + origin +
                                    ":" + wayPoints + ":" + dest +
                                    "/json?&travelMode=car&key=" + tomtomApiKey);
                        } else {
                            url = new URL("https://api.tomtom.com/routing/1/calculateRoute/" + origin +
                                    ":" + dest +
                                    "/json?&travelMode=car&key=" + tomtomApiKey);
                        }
                        ServerAsyncTask serverAsyncTask = new ServerAsyncTask(context, serverHelper);
                        serverHelper.setUrl(url.toString());
                        serverHelper.setTag(map);
                        serverAsyncTask.showCallProgress(false);
                        serverAsyncTask.execute();
                    }
                } catch (Exception e) {
                    Helper.printLogMsg("Exception while reading url", e.toString());
                }

//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(new com.google.android.gms.maps.model.LatLng(slat, slng)).zoom(16).build();
//                map.animateCamera(CameraUpdateFactory
//                        .newCameraPosition(cameraPosition));
            }
        });
    }

    ServerHelper serverHelper = new ServerHelper() {

        @Override
        public void onFailure(Object o) throws NullPointerException {
        }

        @Override
        public void onSuccess(Object o) throws NullPointerException {
//            Helper.printLogMsg(TAG, "url: " + url.toString());
            if (o != null && String.valueOf(o).length() > 0) {
                try {
                    String data = String.valueOf(o);
                    if (data != null && data.length() > 0) {
                        GoogleMap map = (GoogleMap) getTag();
                        setRouteData(data);

                        JSONObject object = new JSONObject(data);
                        String status = object.optString("status");
                        if (status.equals("OVER_QUERY_LIMIT")) {
                            //                                attempt += 1;
                            //                                if (attempt == 2) {
                            //                                    googleApiKey = Constants.GOOGLE_API_KEY_LIVE2;
                            //                                } else if (attempt == 3) {
                            //                                    googleApiKey = Constants.GOOGLE_API_KEY_LIVE3;
                            //                                }
                            //                                data = getDirections(origin, wayPts, destination, attempt, googleApiKey);
                        } else {
//                            mImgGraph.setVisibility(View.VISIBLE);
                            JSONObject jObject = new JSONObject(data);
                            PathJSONParser parser = new PathJSONParser();
                            List<List<HashMap<String, String>>> routes = parser.parse(jObject, TOM_TOM_API);
                            drawRoute(map, routes);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServerError(String message) {
        }
    };

    private void drawRoute(GoogleMap map, List<List<HashMap<String, String>>> routes) {

        ArrayList<com.google.android.gms.maps.model.LatLng> points = null;
        PolylineOptions polyLineOptions = null;
        if (routes != null && routes.size() > 0) {
            points = new ArrayList<com.google.android.gms.maps.model.LatLng>();
//                for (int i = 0; i < routes.size(); i++) {
            polyLineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = routes.get(0);

//                LatLng tmpPos = null;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            String lineStr = "'LINESTRING(";
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                com.google.android.gms.maps.model.LatLng position = new com.google.android.gms.maps.model.LatLng(lat, lng);
                lineStr += lng + " " + lat;
                if (j < path.size() - 1) {
                    lineStr += ", ";
                }
//                    if (tmpPos != null && j % 9 == 0) {
//                        double headRotation = SphericalUtil.computeHeading(tmpPos, position);
//                        googleMap.addMarker(new MarkerOptions().position(tmpPos).flat(true).rotation((float) headRotation).icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_map)));
//                    }
//                    tmpPos = position;

                points.add(position);
                builder.include(position);
            }
            lineStr += ")'";

            try {
                JSONObject routeJson = new JSONObject();
                routeJson.put("Data", lineStr);
//                Helper.printLogMsg(TAG, routeJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(8);
            polyLineOptions.color(Color.BLACK);

//                }
            if (polyLineOptions != null) {
                Polyline polyline = map.addPolyline(polyLineOptions);
                setRoute(polyline);
                LatLngBounds latLngBounds = builder.build();

                int padding = 10;
                map.setPadding(10, 300, 10, 10);
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));

            }
        }
    }

    public String route;
    public Polyline polyline;

    private void setRouteData(String route) {
        this.route = route;
    }

    private void setRoute(Polyline polyline) {
        this.polyline = polyline;
    }

    public Polyline getRoute() {
        return polyline;
    }

    public String getRouteData() {
        return route;
    }

    public void zoom(GoogleMap map, Polyline polyline) {
        int padding = 10;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        List<LatLng> points = polyline.getPoints();

        for (LatLng latLng : points) {
            builder.include(latLng);
        }

        LatLngBounds latLngBounds = builder.build();

        map.setPadding(10, 300, 10, 10);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, padding));
    }

}