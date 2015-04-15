package edu.gvsu.cis.getterj.flyordrive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,
LocationListener{
    EditText startLoc;
    EditText endLoc;
    ImageButton goButton;
    Spinner yearSpinner;
    Spinner makeSpinner;
    Spinner modelSpinner;
    RadioButton currentLoc;
    Spinner optionsSpinner;
    EditText driveHours;
    private static DocumentBuilderFactory dbFactory;
    ArrayList<String> carModelArrayList;
    ArrayList<String> carMakeArrayList;
    ArrayList<String> carYearArrayList;
    ArrayList<String> carOptionsArrayList;
    ArrayList<String> carIdArrayList;
    String url;
//    String apiKey = "AIzaSyCjFdDt_AKA3uxkPJP_OSnrQrp4e9QbVyM";
    String googleMapUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=";
//    String airportCodeLookup = "http://airports.pidgets.com/v1/airports?near=37.77,-122.39&format=json";
    String milesToTravel;
    String carMPG;
    String regularGasPrice;
    String driveDuration;
    Double driveTimeInHours;
    Double driveCost;
    Double hotelCostPerNight = 100.0;
    String startLat;
    String startLon;
    String endLat;
    String endLon;
    String flightPrice;
    ArrayList<String> airportCodesList;
    ArrayList <String> airportCityList;
    boolean anyAirport = false;
    Double currLongitude;
    Double currLatitude;
    Double tempLat;
    Double tempLon;
    ProgressDialog prog;
    String flyingTime;
    int flightMileage = 0;
    int straightDistance;
    String estimatedFlightTime;
    Double estimatedFlightCost;
    String polylineEncoded;
    private static final String TAG = "GglPlayServicesActivity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        startLoc = (EditText) findViewById(R.id.startLoc);
        endLoc = (EditText) findViewById(R.id.endLoc);
        goButton = (ImageButton) findViewById(R.id.goButton);
        currentLoc = (RadioButton) findViewById(R.id.currLocRadioButton);
        yearSpinner = (Spinner) findViewById(R.id.spinner);
        makeSpinner = (Spinner) findViewById(R.id.makeSpinner);
        modelSpinner = (Spinner) findViewById(R.id.modelSpinner);
        optionsSpinner = (Spinner) findViewById(R.id.carOptionsSpinner);
        driveHours = (EditText) findViewById(R.id.driveHours);
        dbFactory = DocumentBuilderFactory.newInstance();
        carModelArrayList = new ArrayList<>();
        carYearArrayList = new ArrayList<>();
        carMakeArrayList = new ArrayList<>();
        carOptionsArrayList = new ArrayList<>();
        carIdArrayList = new ArrayList<>();
        airportCodesList = new ArrayList<>();
        airportCityList = new ArrayList<>();


//        final String carId;
        url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/year";
        JsonRequest getYears = new JsonRequest();
        getYears.execute("http://www.fueleconomy.gov/ws/rest/vehicle/menu/year");
        url = "http://www.fueleconomy.gov/ws/rest/fuelprices";
        JsonRequest getFuelPrice = new JsonRequest();
        getFuelPrice.execute(url);

        prog = new ProgressDialog(MainActivity.this);
        prog.setTitle("Processing Travel Information");
        prog.setIndeterminate(true);
        prog.setMessage("Please wait.....");


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prog.show();
              if(currentLoc.isChecked())
              {
                  url = googleMapUrl + currLatitude + "," + currLongitude + "&destination="
                          + endLoc.getText().toString();
              }
                else {
                  url = googleMapUrl + startLoc.getText().toString() + "&destination="
                          + endLoc.getText().toString();
              }

                JsonRequest getDirections = new JsonRequest();
                getDirections.execute(url);


            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/make?year=" +
                        yearSpinner.getSelectedItem().toString();

                carMakeArrayList.clear();
                JsonRequest getMakes = new JsonRequest();
                getMakes.execute(url);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/model?year="
                        + yearSpinner.getSelectedItem().toString() + "&make="
                        + makeSpinner.getSelectedItem().toString();

                carModelArrayList.clear();
                JsonRequest getModels = new JsonRequest();
                getModels.execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/options?year="
                        + yearSpinner.getSelectedItem().toString() + "&make="
                        + makeSpinner.getSelectedItem().toString() + "&model="
                        + modelSpinner.getSelectedItem().toString();
                carOptionsArrayList.clear();
                carIdArrayList.clear();
                JsonRequest getOptions = new JsonRequest();
                getOptions.execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/"
                        + carIdArrayList.get(optionsSpinner.getSelectedItemPosition());
                JsonRequest getMPG = new JsonRequest();
                getMPG.execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        LocationRequest req = new LocationRequest();
        req.setInterval (3000); /* every 3 seconds */
        req.setFastestInterval (1000); /* how fast our app can handle the notifications */
        req.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates (
                mGoogleApiClient,   /* fill in with the name of your GoogleMap object */
                req,
                this);  /* this class is the LocationListener */
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = currentLoc.isChecked();

        if (checked && !startLoc.getText().toString().equals("Use Current Location")){
            startLoc.setText("Use Current Location");
            startLoc.setFocusable(false);
            startLoc.setFocusableInTouchMode(false);
            startLoc.setClickable(false);
            currentLoc.setChecked(true);

            currLongitude = tempLon;
            currLatitude = tempLat;




        }
        else if (startLoc.getText().toString().equals("Use Current Location")){
            startLoc.setText("");
            startLoc.setFocusable(true);
            startLoc.setFocusableInTouchMode(true);
            startLoc.setClickable(true);
            currentLoc.setChecked(false);

        }
        else{
            startLoc.setText("");
            startLoc.setFocusable(true);
            startLoc.setFocusableInTouchMode(true);
            startLoc.setClickable(true);
            currentLoc.setChecked(false);
        }
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

    @Override
    public void onLocationChanged(Location location) {
    tempLat = location.getLatitude();
    tempLon = location.getLongitude();
    System.out.print(tempLat + tempLon);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class JsonRequest extends AsyncTask<String, Void, BackgroundHolder> {

        @Override
        protected void onPreExecute() {

        }
        @Override
        protected BackgroundHolder doInBackground(String... strings) {

            if(strings[0].contains("maps.google") || strings[0].contains("airport") || strings[0].contains("hotwire"))
            {
            String json = "";
                try {
                    URL theURL = new URL(strings[0].replaceAll(" ", "%20"));
                    Scanner scan = new Scanner(theURL.openStream());
                    while (scan.hasNextLine()) {
                        json += scan.nextLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return (new BackgroundHolder(json,strings[0]));
                //previously called holder
            }
            if(strings[0].contains("qpxExpress"))
            {

                try {
                    Calendar cal = Calendar.getInstance();
                    //cal.setTime(cal.getTime());
                    cal.add(Calendar.DATE, 14);
                    Date futureDate = cal.getTime();
                    String intMonth = (String) android.text.format.DateFormat.format("MM", futureDate); //06
                    String year = (String) android.text.format.DateFormat.format("yyyy", futureDate); //2013
                    String day = (String) android.text.format.DateFormat.format("dd", futureDate); //20

                    String json = "{\"request\":{\"slice\":[{\"origin\":\"" + airportCodesList.get(0)
                            + "\",\"destination\":\"" + airportCodesList.get(1)
                            + "\",\"date\":\"" + year + "-" + intMonth + "-" + day
                            + "\"}],\"passengers\":{\"adultCount\":1,\"infantInLapCount\":0,\"" +
                            "infantInSeatCount\":0,\"childCount\":0,\"seniorCount\":0},\"solutions" +
                            "\":20,\"refundable\":false}}";
                    JSONObject jsonOfString = new JSONObject(json);

                    HttpsURLConnection httpConn;
                    URL u = new URL(strings[0]);
                    StringBuilder sb = new StringBuilder();

                    httpConn = (HttpsURLConnection)u.openConnection();

                    httpConn.setDoOutput(true);
                    httpConn.setRequestMethod("POST");
                    httpConn.setRequestProperty("Content-Type", "application/json");

                    httpConn.connect();
                    OutputStreamWriter wr= new OutputStreamWriter(httpConn.getOutputStream());
                    wr.write(jsonOfString.toString());
                    wr.close();

                    int HttpResult =httpConn.getResponseCode();

                    if(HttpResult ==HttpURLConnection.HTTP_OK){

                        BufferedReader br = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),"utf-8"));

                        String line;

                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }

                        br.close();
                        return new BackgroundHolder(sb.toString(),strings[0]);
                        //previously called googleFlightHolder

                    }else{
                        return new BackgroundHolder("Exceeded Google","Exceeded Google");
                    }


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            HttpURLConnection uconn;
            try {
                DocumentBuilder docBuilder;
                docBuilder = dbFactory.newDocumentBuilder();

                URL u = new URL(strings[0].replaceAll(" ","%20"));
                uconn = (HttpURLConnection) u.openConnection();
                Document doc = docBuilder.parse(uconn.getInputStream());
                return (new BackgroundHolder(doc,strings[0]));
                //previously called xmlHolder
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            return null;

        }

            @Override
        protected void onPostExecute(BackgroundHolder s) {
            super.onPostExecute(s);

                if(s.getUrl().contains("maps.google"))
                {
                    String json = s.getJsonHolder();
                    if(json.contains("ZERO_RESULTS"))
                    {
                        prog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Please make sure your cities are valid.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else{
                    try {
                         JSONObject topJsonObject = new JSONObject(json);
                         JSONArray routes = topJsonObject.getJSONArray("routes");
                         JSONObject holder1 = routes.getJSONObject(0);
                         JSONArray legs = holder1.getJSONArray("legs");
                         JSONObject holder2 = legs.getJSONObject(0);
                         JSONObject polyline = holder1.getJSONObject("overview_polyline");
                        polylineEncoded = polyline.get("points").toString();
                         JSONObject distance = holder2.getJSONObject("distance");
                         milesToTravel = distance.getString("text").replaceAll("[^0-9]","");
                         JSONObject duration = holder2.getJSONObject("duration");
                        driveDuration = duration.getString("text");
                        driveTimeInHours = Double.parseDouble(duration.getString("value"))/3600;
                        JSONObject startLoc = holder2.getJSONObject("start_location");
                        startLon = startLoc.getString("lng");
                        startLat = startLoc.getString("lat");
                        JSONObject endLoc = holder2.getJSONObject("end_location");
                        endLon = endLoc.getString("lng");
                        endLat = endLoc.getString("lat");
                        float[] results = new float[1];
                        Location.distanceBetween(Double.parseDouble(startLat),
                                Double.parseDouble(startLon),Double.parseDouble(endLat),
                                Double.parseDouble(endLon),results);
                        straightDistance = (int) (results[0] * 0.00062137);
                        estimatedFlightTime = "" + (int)((straightDistance / 9.3) + 45);
                        estimatedFlightCost = 50 + (straightDistance * .11);



                        double stopCost = ((Math.ceil(driveTimeInHours/
                                Double.parseDouble(driveHours.getText().toString()))-1)
                                * hotelCostPerNight);
                        driveCost = (Double.parseDouble(milesToTravel)/
                                Double.parseDouble(carMPG))*
                                Double.parseDouble(regularGasPrice) + stopCost;

                        airportCodesList.clear();
                        JsonRequest getStartAirCode = new JsonRequest();
                        getStartAirCode.execute("http://airports.pidgets.com/v1/airports?near=" +
                                startLat + "," + startLon + "&format=json");
                        JsonRequest getEndAirCode = new JsonRequest();
                        getEndAirCode.execute("http://airports.pidgets.com/v1/airports?near=" +
                                endLat + "," + endLon+ "&format=json");

                    } catch (JSONException e)
                         {
                                 e.printStackTrace();
                            }

                }
                }
                else {
                    if (s.getUrl().contains("airport")) {
                        int carriers;
                        if(airportCodesList.size() >= 2 || airportCityList.size() >= 2)
                        {
                            airportCodesList.clear();
                            airportCityList.clear();
                        }
                        if(anyAirport)
                        {
                            carriers = 2;
                        }
                        else
                        {
                            carriers = 20;
                        }
                        String toConvert = s.getJsonHolder().substring(s.getJsonHolder().indexOf("["),
                                s.getJsonHolder().length());

                        try {
                            JSONArray top = new JSONArray(toConvert);
                            String tempCarrier = "0";
                            int k = 0;
                            JSONObject currObj = null;

                            while (Integer.parseInt(tempCarrier) < carriers && k < top.length()) {
                                currObj = top.getJSONObject(k);
                                tempCarrier = currObj.getString("carriers");


                                k++;
                            }

                            if (currObj != null) {
                                airportCodesList.add(currObj.getString("code"));
                                airportCityList.add(currObj.getString("city"));
                            }
                            if (airportCodesList.size() == 2 && !anyAirport) {
                                JsonRequest getFlightPriceHotwire = new JsonRequest();
                                getFlightPriceHotwire.execute("http://api.hotwire.com" +
                                        "/v1/tripstarter/air?&sort=price&sortorder=asc&apikey=" +
                                        "me7km7cggj34uffqyavrfazg&dest=" + airportCityList.get(1) +
                                        "&dist=300&origin=" + airportCityList.get(0) + "&format=json");

                            }
                            if(anyAirport && airportCodesList.size()==2)
                            {
                                anyAirport = false;
                                JsonRequest googleFlightRequest = new JsonRequest();
                                googleFlightRequest.execute("https://www.googleapis.com" +
                                        "/qpxExpress/v1/trips/search?key=" +
                                        "AIzaSyBy1E3Ad0eF6HSiezWSd0SJ98V5ZPYLxYc");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (s.getUrl().contains("hotwire")) {

                            try {
                                JSONObject main = new JSONObject(s.getJsonHolder());
                                JSONArray results = main.getJSONArray("Result");
                                if (results.length() != 0) {
                                    JSONObject cheapFlight = results.getJSONObject(0);
                                    flightPrice = cheapFlight.getString("AveragePrice");
                                    flightMileage = straightDistance;
                                    flyingTime = estimatedFlightTime;
                                    Intent launchme = new Intent(MainActivity.this, ResultsActivity.class);
                                    launchme.putExtra("driveCost", driveCost);
                                    launchme.putExtra("driveMiles", milesToTravel);
                                    launchme.putExtra("driveDuration", driveDuration);
                                    launchme.putExtra("startLat", startLat);
                                    launchme.putExtra("startLon", startLon);
                                    launchme.putExtra("endLon", endLon);
                                    launchme.putExtra("endLat", endLat);
                                    launchme.putExtra("flightPrice", flightPrice);
                                    launchme.putExtra("flightMileage",flightMileage);
                                    launchme.putExtra("flyingTime",flyingTime);
                                    launchme.putExtra("pointsEncoded",polylineEncoded);
                                    if (prog.isShowing()) {
                                        prog.hide();
                                    }
                                    startActivity(launchme);

                                } else {
                                    anyAirport = true;
                                    JsonRequest getNewAirports = new JsonRequest();
                                    getNewAirports.execute("http://airports.pidgets.com/v1/airports?near="
                                            + startLat + "," + startLon + "&format=json");
                                    JsonRequest getNewAirports2 = new JsonRequest();
                                    getNewAirports2.execute("http://airports.pidgets.com/v1/airports?near="
                                            + endLat + "," + endLon+ "&format=json");

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                        if(s.getUrl().contains("qpxExpress")) {
                            try {
                                flightMileage = 0;
                                flightPrice = null;
                                flyingTime = null;
                                JSONObject googleFlight = new JSONObject(s.getJsonHolder());
                                JSONObject trips = googleFlight.getJSONObject("trips");
                                JSONArray tripOptions = trips.getJSONArray("tripOption");
                                JSONObject cheapestFlight = tripOptions.getJSONObject(0);
                                flightPrice = cheapestFlight.getString("saleTotal").replaceAll("USD","");
                                JSONArray slice = cheapestFlight.getJSONArray("slice");
                                JSONObject segmentHolder = slice.getJSONObject(0);
                                JSONArray segments = segmentHolder.getJSONArray("segment");
                                flyingTime = segmentHolder.get("duration").toString();
                                for(int i = 0; i < segments.length(); i++)
                                {
                                    JSONObject currObj = segments.getJSONObject(i);
                                    JSONArray currArray = currObj.getJSONArray("leg");
                                    JSONObject currLeg = currArray.getJSONObject(0);
                                    flightMileage = flightMileage + Integer.parseInt(currLeg.get("mileage").toString());

                                }



                                Intent launchme = new Intent(MainActivity.this, ResultsActivity.class);
                                launchme.putExtra("driveCost", driveCost);
                                launchme.putExtra("driveMiles", milesToTravel);
                                launchme.putExtra("driveDuration", driveDuration);
                                launchme.putExtra("startLat", startLat);
                                launchme.putExtra("startLon", startLon);
                                launchme.putExtra("endLon", endLon);
                                launchme.putExtra("endLat", endLat);
                                launchme.putExtra("flightPrice",flightPrice);
                                launchme.putExtra("flyingTime", flyingTime);
                                launchme.putExtra("flightMileage",flightMileage);
                                launchme.putExtra("pointsEncoded",polylineEncoded);
                                if (prog.isShowing()) {
                                    prog.hide();
                                }
                                startActivity(launchme);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            if (s.getUrl().contains("Exceeded")) {
                                Intent launchme = new Intent(MainActivity.this, ResultsActivity.class);
                                launchme.putExtra("driveCost", driveCost);
                                launchme.putExtra("driveMiles", milesToTravel);
                                launchme.putExtra("driveDuration", driveDuration);
                                launchme.putExtra("startLat", startLat);
                                launchme.putExtra("startLon", startLon);
                                launchme.putExtra("endLon", endLon);
                                launchme.putExtra("endLat", endLat);
                                launchme.putExtra("flightPrice", estimatedFlightCost.toString());
                                launchme.putExtra("flyingTime", estimatedFlightTime);
                                launchme.putExtra("flightMileage", straightDistance);
                                launchme.putExtra("pointsEncoded",polylineEncoded);
                                if (prog.isShowing()) {
                                    prog.hide();
                                }
                                startActivity(launchme);
                            } else {
                                Document xmlDoc = s.getDocHolder();
                                String root = s.getDocHolder().getDocumentElement().getNodeName();

                                if (root.equals("vehicle")) {
                                    NodeList xmlElements = xmlDoc.getElementsByTagName("vehicle");
                                    NodeList level2 = xmlElements.item(0).getChildNodes();
                                    Node mpg = level2.item(19);
                                    carMPG = mpg.getTextContent();
                                }
                                if (root.equals("fuelPrices")) {
                                    NodeList xmlElements = xmlDoc.getElementsByTagName("fuelPrices");
                                    NodeList level2 = xmlElements.item(0).getChildNodes();
                                    Node regGasPrice = level2.item(7);
                                    regularGasPrice = regGasPrice.getTextContent();

                                } else {
                                    NodeList xmlElements = xmlDoc.getElementsByTagName("menuItem");
                                    for (int k = 0; k < xmlElements.getLength(); k++) {
                                        NodeList level2 = xmlElements.item(k).getChildNodes();
                                        Node temp = level2.item(0);
                                        Node id = level2.item(1);
                                        if (s.getUrl().contains("make?")) {

                                            carMakeArrayList.add(temp.getTextContent());
                                            ArrayAdapter<String> adapter;
                                            adapter = new ArrayAdapter<>(MainActivity.this,
                                                    android.R.layout.simple_spinner_dropdown_item, carMakeArrayList);
                                            makeSpinner.setAdapter(adapter);
                                        }
                                        if (s.getUrl().contains("/year")) {
                                            carYearArrayList.add(temp.getTextContent());
                                            ArrayAdapter<String> adapter;
                                            adapter = new ArrayAdapter<>(MainActivity.this,
                                                    android.R.layout.simple_spinner_dropdown_item, carYearArrayList);
                                            yearSpinner.setAdapter(adapter);
                                        }
                                        if (s.getUrl().contains("model?")) {
                                            carModelArrayList.add(temp.getTextContent());
                                            ArrayAdapter<String> adapter;
                                            adapter = new ArrayAdapter<>(MainActivity.this,
                                                    android.R.layout.simple_spinner_dropdown_item, carModelArrayList);
                                            modelSpinner.setAdapter(adapter);
                                        }
                                        if (s.getUrl().contains("options?")) {
                                            carOptionsArrayList.add(temp.getTextContent());

                                            carIdArrayList.add(id.getTextContent());

                                            ArrayAdapter<String> adapter;
                                            adapter = new ArrayAdapter<>(MainActivity.this,
                                                    android.R.layout.simple_spinner_dropdown_item, carOptionsArrayList);
                                            optionsSpinner.setAdapter(adapter);
                                        }


                                    }
                                }
                            }
                        }}
                    }
                }

        }
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);

        if (prog.isShowing()) {
            prog.hide();
        }
    }


}
