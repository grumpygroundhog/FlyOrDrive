package edu.gvsu.cis.getterj.flyordrive;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends Activity {
    EditText startLoc;
    EditText endLoc;
    EditText carMake;
    EditText carModel;
    EditText carYear;
    Button goButton;

    String apiKey = "AIzaSyCjFdDt_AKA3uxkPJP_OSnrQrp4e9QbVyM";
    /*what is the point of this section??? */
//    String destination = "Miami";
//    String origin = "Lansing";
//    String make = "GMC";
//    String model = "Sierra";
//    String year = "1998";
    String googleMapUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=";

    String milesToTravel = "";
    /* no need for a method. Just set the string in the asyncTask and cleared it in pre-execute -charles*/
//    public void setMilesToTravel(String milesToTravel) {
//        this.milesToTravel = milesToTravel;
//    }


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
                /*
                shouldn't we put an intent in here that launches all the entered
                info into the next activity?? -charles
                 */

                //took out unnecessary variables for memory and efficiency -charles
               String w = googleMapUrl + startLoc.getText().toString() + "&destination="
                       + endLoc.getText().toString();
                //JSON now takes in the url as a param to shorten the async task & be more efficient -charles
                JsonRequest getDirections = new JsonRequest();
                getDirections.execute(w);
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

    private class JsonRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            milesToTravel = "";
        }
        @Override
        protected String doInBackground(String... strings) {
//            String jsonString = null;
//            HttpClient client = new DefaultHttpClient();
//            String localUrl = url;
//            HttpGet hget = new HttpGet(localUrl);
//            HttpResponse response = null;
//            try {
//                response = client.execute(hget);
//
//                HttpEntity entity = response.getEntity();
//                String jsonResult = EntityUtils.toString(entity);//above gets response from Google Maps to get distance.//
//
//               /* String uri =
//                        "http://www.fueleconomy.gov/ws/rest/vehicle/menu/options?year=" + year + "&make=" + make + "&model=" + model;
//
//                URL url = new URL(uri);
//                HttpURLConnection connection =
//                        (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Accept", "application/xml");
//
//                InputStream xml = connection.getInputStream();
//
//                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//                DocumentBuilder db = dbf.newDocumentBuilder();
//                Document doc = db.parse(xml);*/
//
//                return jsonResult;

            /*I shortened up this code a bit using the URL class in java for efficiency and shorter code -charles*/
            String json = "";
            try {
                URL theURL = new URL(strings[0]);
                Scanner scan = new Scanner(theURL.openStream());
                while (scan.hasNextLine()) {
                    json += scan.nextLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return (json);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //removed unnecessary initializers -charles
//            JSONObject topJsonObject;
//            JSONArray routes = new JSONArray();
//            JSONObject holder1 = new JSONObject();
//            JSONArray legs = new JSONArray();
//            JSONObject holder2 = new JSONObject();
//            JSONObject distance = new JSONObject();
            try {
                JSONObject topJsonObject = new JSONObject(s);
                JSONArray routes = topJsonObject.getJSONArray("routes");
                JSONObject holder1 = routes.getJSONObject(0);
                JSONArray legs = holder1.getJSONArray("legs");
                JSONObject holder2 = legs.getJSONObject(0);
                JSONObject distance = holder2.getJSONObject("distance");
                milesToTravel = distance.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
