package edu.gvsu.cis.getterj.flyordrive;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends Activity {
    EditText startLoc;
    EditText endLoc;
    EditText carMake;
    EditText carModel;
    EditText carYear;
    Button goButton;

    String apiKey = "AIzaSyCjFdDt_AKA3uxkPJP_OSnrQrp4e9QbVyM";
    String destination = "Miami";
    String origin = "Lansing";
    String make = "GMC";
    String model = "Sierra";
    String year = "1998";
    String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + origin + "&destination=" + destination;

    public void setMilesToTravel(String milesToTravel) {
        this.milesToTravel = milesToTravel;
    }

    String milesToTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLoc = (EditText) findViewById(R.id.startLoc);
        endLoc = (EditText) findViewById(R.id.endLoc);
        carMake = (EditText) findViewById(R.id.carMake);
        carModel = (EditText) findViewById(R.id.carModel);
        carYear = (EditText) findViewById(R.id.carYear);
        goButton = (Button) findViewById(R.id.goButton);




        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String userStartLoc = startLoc.getText().toString();
               String userEndLoc = endLoc.getText().toString();
                JsonRequest getDirections = new JsonRequest();
                getDirections.execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class JsonRequest extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String jsonString = null;
            HttpClient client = new DefaultHttpClient();
            String localUrl = url;
            HttpGet hget = new HttpGet(localUrl);
            HttpResponse response = null;
            try {
                response = client.execute(hget);

                HttpEntity entity = response.getEntity();
                String jsonResult = EntityUtils.toString(entity);//above gets response from Google Maps to get distance.//

               /* String uri =
                        "http://www.fueleconomy.gov/ws/rest/vehicle/menu/options?year=" + year + "&make=" + make + "&model=" + model;

                URL url = new URL(uri);
                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/xml");

                InputStream xml = connection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(xml);*/

                return jsonResult;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject = null;
            JSONArray routes = new JSONArray();
            JSONObject holder1 = new JSONObject();
            JSONArray legs = new JSONArray();
            JSONObject holder2 = new JSONObject();
            JSONObject distance = new JSONObject();
            try {
                jsonObject = new JSONObject(s);
                routes = jsonObject.getJSONArray("routes");
                holder1 = routes.getJSONObject(0);
                legs = holder1.getJSONArray("legs");
                holder2 = legs.getJSONObject(0);
                distance = holder2.getJSONObject("distance");
                String tempResult = distance.getString("text");
                setMilesToTravel(tempResult);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
