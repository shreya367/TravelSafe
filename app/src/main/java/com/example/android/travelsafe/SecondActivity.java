package com.example.android.travelsafe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBigs6lbBsUjCG3XFWxWZyC5JKJLNpTSl0";
    public String destination_desc = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public class DownloadPathJSON extends AsyncTask<String, Void, String>{

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

                ArrayList<String> resultList = null;
                JSONObject jsonObj = new JSONObject(result);
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
                resultList = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }
                Log.i("IINFO - best match",resultList.get(0));
                return resultList.get(0);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.i("IINFO - API Reply", result);
            try {
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
                intent.putExtra("dest", result);
                startActivity(intent);


            } catch (Exception e){

            }
        }
    }

    public void fetchRoutes(View v) throws UnsupportedEncodingException {
        EditText source = (EditText)findViewById(R.id.source);
        EditText destination = (EditText)findViewById(R.id.destination);

        String sourceValue = source.getText().toString();
        String destinationValue = destination.getText().toString();

        Log.i("IINFO - source", sourceValue);
        Log.i("IINFO - destination", destinationValue);

        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
        sb.append("?key=" + API_KEY);
        sb.append("&input=" + URLEncoder.encode(destinationValue, "utf8"));


        //PlaceAPI placeAPI = new PlaceAPI();
        //ArrayList<String> temp =  placeAPI.autocomplete(sourceValue);

        DownloadPathJSON downloadPathJSON = new DownloadPathJSON();
        downloadPathJSON.execute(sb.toString());

    }
}
