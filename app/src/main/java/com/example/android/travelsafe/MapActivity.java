package com.example.android.travelsafe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    //private static final LatLng Source=new LatLng(23.8144,86.4412);
    //private static final LatLng Dest=new LatLng(23.7881,86.4181);
    //private static final LatLng LOWER_MANHATTAN = new LatLng(40.722543, -73.998585);
    //private static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    //private static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);
    final String TAG = "MapActivity";
    String Source,Dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        String sessionId= getIntent().getStringExtra("dest");
        Log.i("IINFO - MAP Activity", "Activated");
        Log.i("IINFO - sessionId", sessionId);

        try {
            Source = URLEncoder.encode("Civil Engineering VT, Jamshedpur", "utf8");
            Dest = URLEncoder.encode(sessionId, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d("Myapp","Created");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap=mMap;
       // MarkerOptions options = new MarkerOptions();
        //options.position(Source);
        //options.position(Dest);
        //options.position(WALL_STREET);
        //googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

      // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Dest, 13));
        Log.d("Myapp","camera");
        //addMarkers();
        Log.d("Myapp","Markers");
        // Add a marker in Sydney and move the camera
        LatLng nit = new LatLng(22.777015,86.144621);
        googleMap.addMarker(new MarkerOptions().position(nit).title("Marker in nit"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nit,15));

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline)
            {
                int strokeColor = polyline.getColor() ^ 0x0000CC00;
                polyline.setColor(strokeColor);
                List<LatLng>list=polyline.getPoints();
                Log.e("TAG", "Polyline points @ " + polyline.getPoints());
                int n=list.size();
                String Lat=Double.toString(list.get(n/2).latitude);
                String Long=Double.toString(list.get(n/2).longitude);
                Log.d("POINT",Lat+Long);
               Toast.makeText(MapActivity.this, "Polyline click: " + Lat+" "+Long, Toast.LENGTH_LONG).show();

                BitmapDescriptor transparent = BitmapDescriptorFactory.fromResource(R.drawable.transparent);
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(Double.valueOf(Lat), Double.valueOf(Long)))
                        .title("Overall Score")
                        .snippet("someSnippet")
                        .icon(transparent)
                        .anchor((float) 0.5, (float) 0.5); //puts the info window on the polyline

                Marker marker = googleMap.addMarker(options);

//open the marker's info window
                marker.showInfoWindow();
            }
        });

    }

        private String getMapsApiDirectionsUrl() {
          /*  String waypoints = "waypoints=optimize:true|"
                    + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                    + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                    + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                    + WALL_STREET.longitude;*/
            String origin="origin="+Source;
            String destination="destination="+Dest;
            String key="key=AIzaSyBigs6lbBsUjCG3XFWxWZyC5JKJLNpTSl0";
            String sensor = "sensor=false";
            String params = origin+"&"+destination+"&"+key+"&alternatives=true";
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;
            Log.d("MyApp",url);
            return url;
        }


     /*   private void addMarkers() {
            if (googleMap != null) {
                googleMap.addMarker(new MarkerOptions().position(Source)
                        .title("First Point"));
                googleMap.addMarker(new MarkerOptions().position(Dest)
                        .title("Second Point"));
            }
        }*/

        private class ReadTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                String result = "";
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(urls[0]);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while(data!=-1){
                        char current = (char) data;
                        result += current;
                        data = reader.read();
                    }
                    return result;
                }  catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                new ParserTask().execute(result);
            }
        }

        private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            @Override
            protected List<List<HashMap<String, String>>> doInBackground(
                    String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonData[0]);
                    Log.d("myApp",jObject.toString());
                    PathJSONParser parser = new PathJSONParser();
                    routes = parser.parse(jObject);
                    String listString = "";

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
                ArrayList<LatLng> points = null;
                PolylineOptions polyLineOptions = null;
                int[] colors=new int[4];
                colors[0]=0xFF0000FF;
                colors[1]=0xFFFF0000;
                colors[2]=0xFF888888;
                colors[3]=0xFF00FF00;

                Log.d("Routes",""+routes.size());
                // traversing through routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(10);
                    polyLineOptions.color(colors[i%4]);
                    Polyline polyline= googleMap.addPolyline(polyLineOptions);
                    polyline.setClickable(true);
                }
                String listString = "";

                for (LatLng s : points)
                {
                    listString += s + "\t";
                }
                Log.d("myApp",listString);
                //googleMap.addPolyline(polyLineOptions);
                Log.d("Line","Line drawn");
            }

            /* googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
            {
                @Override
                public void onPolylineClick(Polyline polyline)
                {
                    //do something with polyline
                    Log.d("PolyLine","Polyline clicked");
                }
            });*/
        }

}
