package edu.gvsu.cis.getterj.flyordrive;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends Activity {
    EditText startLoc;
    EditText endLoc;
//    EditText carMake;
//    EditText carModel;
//    EditText carYear;
    Button goButton;
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
    String apiKey = "AIzaSyCjFdDt_AKA3uxkPJP_OSnrQrp4e9QbVyM";
    String googleMapUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=";
    String airportCodeLookup = "http://airports.pidgets.com/v1/airports?near=37.77,-122.39&format=json";
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
    ArrayList<String> airportCodesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startLoc = (EditText) findViewById(R.id.startLoc);
        endLoc = (EditText) findViewById(R.id.endLoc);
//        carMake = (EditText) findViewById(R.id.carMake);
//        carModel = (EditText) findViewById(R.id.carModel);
//        carYear = (EditText) findViewById(R.id.carYear);
        goButton = (Button) findViewById(R.id.goButton);
        currentLoc = (RadioButton) findViewById(R.id.currLocRadioButton);
        yearSpinner = (Spinner) findViewById(R.id.spinner);
        makeSpinner = (Spinner) findViewById(R.id.makeSpinner);
        modelSpinner = (Spinner) findViewById(R.id.modelSpinner);
        optionsSpinner = (Spinner) findViewById(R.id.carOptionsSpinner);
        driveHours = (EditText) findViewById(R.id.driveHours);
        dbFactory = DocumentBuilderFactory.newInstance();
        carModelArrayList = new ArrayList<String>();
        carYearArrayList = new ArrayList<String>();
        carMakeArrayList = new ArrayList<String>();
        carOptionsArrayList = new ArrayList<String>();
        carIdArrayList = new ArrayList<String>();
        airportCodesList = new ArrayList<String>();

        final String carId;
        url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/year";
        JsonRequest getYears = new JsonRequest();
        getYears.execute("http://www.fueleconomy.gov/ws/rest/vehicle/menu/year");
        url = "http://www.fueleconomy.gov/ws/rest/fuelprices";
        JsonRequest getFuelPrice = new JsonRequest();
        getFuelPrice.execute(url);


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              url = googleMapUrl + startLoc.getText().toString() + "&destination="
                       + endLoc.getText().toString();

                JsonRequest getDirections = new JsonRequest();
                getDirections.execute(url);

                //launches second activity, commented out to prevent error until we have all variables needed to pass.

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/make?year=" + yearSpinner.getSelectedItem().toString();

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
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/model?year=" + yearSpinner.getSelectedItem().toString() + "&make=" + makeSpinner.getSelectedItem().toString();

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
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/options?year=" + yearSpinner.getSelectedItem().toString() + "&make=" + makeSpinner.getSelectedItem().toString() + "&model=" + modelSpinner.getSelectedItem().toString();
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
                url = "http://www.fueleconomy.gov/ws/rest/vehicle/" + carIdArrayList.get(optionsSpinner.getSelectedItemPosition());
                JsonRequest getMPG = new JsonRequest();
                getMPG.execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    public void onRadioButtonClicked(View view) {
        boolean checked = currentLoc.isChecked();

        if (checked && !startLoc.getText().toString().equals("Use Current Location")){
            startLoc.setText("Use Current Location");
            startLoc.setFocusable(false);
            startLoc.setFocusableInTouchMode(false);
            startLoc.setClickable(false);
            currentLoc.setChecked(true);
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

     private class JsonRequest extends AsyncTask<String, Void, BackgroundHolder> {

        @Override
        protected void onPreExecute() {

        }
        @Override
        protected BackgroundHolder doInBackground(String... strings) {

            if(strings[0].contains("google") || strings[0].contains("airport"))
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
                BackgroundHolder holder = new BackgroundHolder(json,strings[0]);
                return (holder);
            }
            HttpURLConnection uconn;
            try {
                DocumentBuilder docBuilder;
                docBuilder = dbFactory.newDocumentBuilder();

                URL u = new URL(strings[0].replaceAll(" ","%20"));
                uconn = (HttpURLConnection) u.openConnection();
                Document doc = docBuilder.parse(uconn.getInputStream());
                //Log.d("HANS", "XML parsed: " + doc.getNodeName());
                BackgroundHolder xmlHolder = new BackgroundHolder(doc,strings[0]);
                return (xmlHolder);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

            @Override
        protected void onPostExecute(BackgroundHolder s) {
            super.onPostExecute(s);
                if(s.getUrl().contains("google"))
                {
                    String json = s.getJsonHolder();
                    try {
                         JSONObject topJsonObject = new JSONObject(json);
                         JSONArray routes = topJsonObject.getJSONArray("routes");
                         JSONObject holder1 = routes.getJSONObject(0);
                         JSONArray legs = holder1.getJSONArray("legs");
                         JSONObject holder2 = legs.getJSONObject(0);
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


                        double stopCost = ((Math.ceil(driveTimeInHours/ Double.parseDouble(driveHours.getText().toString()))-1) * hotelCostPerNight);
                        driveCost = (Double.parseDouble(milesToTravel)/Double.parseDouble(carMPG))*Double.parseDouble(regularGasPrice) + stopCost;

                        airportCodesList.clear();
                        JsonRequest getStartAirCode = new JsonRequest();
                        getStartAirCode.execute("http://airports.pidgets.com/v1/airports?near=" + startLat + "," + startLon + "&format=json");
                        JsonRequest getEndAirCode = new JsonRequest();
                        getEndAirCode.execute("http://airports.pidgets.com/v1/airports?near=" + endLat + "," + endLon+ "&format=json");




                    } catch (JSONException e)
                         {
                                 e.printStackTrace();
                            }

                }
                else {
                    if(s.getUrl().contains("airport"))
                    {
                        String toConvert = s.getJsonHolder().substring(s.getJsonHolder().indexOf("["),s.getJsonHolder().length());

                        try {
                            JSONArray top = new JSONArray(toConvert);
                            String tempCarrier = "0";
                            int k = 0;
                            JSONObject currObj = null;
                            while(Integer.parseInt(tempCarrier) < 2 && k < top.length())
                            {
                                currObj = top.getJSONObject(k);
                                tempCarrier = currObj.getString("carriers");
                                k++;
                            }

                            airportCodesList.add(currObj.getString("code"));
                            if(airportCodesList.size() == 2)
                            {
                                Intent launchme = new Intent (MainActivity.this, ResultsActivity.class);
                                launchme.putExtra("driveCost",driveCost);
                                launchme.putExtra("driveMiles",milesToTravel);
                                launchme.putExtra("driveDuration",driveDuration);

                                startActivity (launchme);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
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
                                    adapter = new ArrayAdapter<String>(MainActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, carMakeArrayList);
                                    makeSpinner.setAdapter(adapter);
                                }
                                if (s.getUrl().contains("/year")) {
                                    carYearArrayList.add(temp.getTextContent());
                                    ArrayAdapter<String> adapter;
                                    adapter = new ArrayAdapter<String>(MainActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, carYearArrayList);
                                    yearSpinner.setAdapter(adapter);
                                }
                                if (s.getUrl().contains("model?")) {
                                    carModelArrayList.add(temp.getTextContent());
                                    ArrayAdapter<String> adapter;
                                    adapter = new ArrayAdapter<String>(MainActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, carModelArrayList);
                                    modelSpinner.setAdapter(adapter);
                                }
                                if (s.getUrl().contains("options?")) {
                                    carOptionsArrayList.add(temp.getTextContent());

                                    carIdArrayList.add(id.getTextContent());

                                    ArrayAdapter<String> adapter;
                                    adapter = new ArrayAdapter<String>(MainActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, carOptionsArrayList);
                                    optionsSpinner.setAdapter(adapter);
                                }


                            }
                        }
                    }
                }



                //ABOVE PARSES XML





        }
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }


}
