package roadcondition.cynsore.cyient.com.cynsore.direction;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import roadcondition.cynsore.cyient.com.cynsore.R;

/**
 * Created by vv42523 on 18-07-2018.
 */

public class DirectionsTask extends AsyncTask<String, Integer, String> {

    //private Fragment mContext;
    private Context mContext;

    private List<LatLng> polyz;
    private ProgressDialog pDialog;
    private Location mLocation;
    private static final String TAG = DirectionsTask.class.getSimpleName();
    private String startLocation;
    private String endLocation;
    private LatLng startLatng, destLatlng;

    public DirectionsTask(Context context, Location location) {
        this.mContext = context;
        this.mLocation = location;
    }

    public DirectionsTask(Context context, String startLoc, String endLoc) {
        this.mContext = context;
        this.startLocation = startLoc;
        this.endLocation = endLoc;
    }

    public DirectionsTask(Context context, LatLng startLatlng0, LatLng endLatlng0) {
        this.mContext = context;
        this.startLatng = startLatlng0;
        this.destLatlng = endLatlng0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mContext, R.style.MyTheme);
        pDialog.setMessage("Loading route. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pDialog.show();

    }

    protected String doInBackground(String... args) {
        // Intent i = getIntent();
        StringBuilder response = new StringBuilder();
        List<List<HashMap<String, String>>> routes = null;
        ArrayList<DirectionBean> beanList = new ArrayList<DirectionBean>();
//        String startLocation = getAddressFromLocation(mContext, mLocation);// i.getStringExtra("startLoc");
//        String endLocation = "punjagutta,hyderabad,telangana";//i.getStringExtra("endLoc");
//        if (!TextUtils.isEmpty(startLocation) && !TextUtils.isEmpty(endLocation)) {
        if (startLatng != null && destLatlng != null) {

           /* startLocation = startLocation.replace(" ", "+").replace("\n", "");

            endLocation = endLocation.replace(" ", "+").replace("\n", "");*/

           /* LatLng start = Util.getLocationFromAddress(mContext, startLocation);
            LatLng dest = Util.getLocationFromAddress(mContext, endLocation);

            Log.d("Direct ", "" + start.latitude + " , " + start.longitude + "\n" + dest.latitude + " , " + dest
                    .longitude);*/
            String stringUrl = "http://maps.google.com/maps/api/directions/json?origin=" +
                    startLatng.latitude + ",%20" + startLatng.longitude + "&destination=" + destLatlng
                    .latitude + ",%20" + destLatlng.longitude + "&sensor=true";
            /*String stringUrl = "http://maps.google" +
                    ".com/maps/api/directions/json?origin=" + start.latitude + "," +
                    "" + start.longitude + "&destination=" + dest.latitude + "," + dest
                    .longitude + "&sensor=true";*/

            Log.d("DIRECTION ", stringUrl);
//            String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLocation + ",+india&destination=" + endLocation + ",+india&sensor=false";
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection httpconn = (HttpURLConnection) url
                        .openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()),
                            8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                } else if (httpconn.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    return "Bad Request";
                } else if (httpconn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    return "Not Found";
                } else if (httpconn.getResponseCode() == HttpURLConnection.HTTP_UNAVAILABLE) {
                    return "Service Unavailable";
                } else if (httpconn.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                    return "Forbidden";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        } else {
            Log.d(TAG, "start location or end location s  empty");
        }


        return response.toString();

    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        List<List<HashMap<String, String>>> routes = null;
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        if (!TextUtils.isEmpty(response)) {
            if (response.equals("Bad Request")) {
                Toast.makeText(mContext, "No route found", Toast.LENGTH_SHORT).show();
            } else if (response.equals("Not Found")) {
                Toast.makeText(mContext, "No route found", Toast.LENGTH_SHORT).show();
            } else if (response.equals("Service Unavailable")) {
                Toast.makeText(mContext, "No route found", Toast.LENGTH_SHORT).show();
            } else if (response.equals("Forbidden")) {
                Toast.makeText(mContext, "No route found", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<LatLng> points = null;
                ArrayList<DirectionBean> beanList = null;
                PolylineOptions lineOptions = null;
                String distance = null;
                String duration = null;
                JSONObject jObject;
                try {
                    jObject = new JSONObject(response);
                    Log.d("ParserTask", response.toString());
                    DataParser parser = new DataParser();
                    Log.d("ParserTask", parser.toString());

                    // Starts parsing data
                    routes = parser.parse(jObject);
                    Log.d("ParserTask", "Executing routes");
                    Log.d("ParserTask", routes.toString());
                    final JSONObject json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");
                    JSONObject routesObj = routeArray.getJSONObject(0);

                    JSONArray newTempARr = routesObj.getJSONArray("legs");
                    JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                    JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                    JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                    distance = distOb.getString("text");
                    duration = timeOb.getString("text");
                    Log.i("Diatance :", distOb.getString("text"));
                    Log.i("Time :", timeOb.getString("text"));

                } catch (Exception e) {
                    Log.d("ParserTask", e.toString());
                    e.printStackTrace();
                }

                // Traversing through all the routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<>();
                    //beanList = new ArrayList<DirectionBean>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
//                    lineOptions.color(Color.parseColor("white"));
                    lineOptions.color(Color.parseColor("#C9F4FF"));

                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    // draw polyline
                    if (mContext != null) {
//                        if (Constants.isSimulation) {
//                        ((StatsActivity) mContext).drawPolyline(lineOptions, points);
//                        } else {
//                            ((OutdoorMapActivity) mContext).drawPolyline(lineOptions, points);
//                            ((OutdoorMapActivity) mContext).setDirectionPoints(points);
//                            ((OutdoorMapActivity) mContext).updateFooterData(distance, duration);
//                        }
                    }
                } else {
                    Toast.makeText(mContext, "No route found", Toast.LENGTH_SHORT).show();
                    Log.d("onPostExecute", "without Polylines drawn");
                }
            }
        } else {

        }
    }

    /* Method to decode polyline points */
    private List<LatLng> decodePoly(String encoded) {

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

    public String getAddressFromLocation(Context context, Location location) {
        String loc = null;
        if (context != null) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());


            List<Address> addresses = null;


            try {


                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // In this sample, get just a single address.
                        1);

            } catch (IOException ioException) {
                // Catch network or other I/O problems.

            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.

            }
            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                Log.e(TAG, "no_address_found");

            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }

                loc = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            }
        }

        return loc;
    }


}
