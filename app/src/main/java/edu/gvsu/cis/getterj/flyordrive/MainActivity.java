package edu.gvsu.cis.getterj.flyordrive;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
    Spinner yearSpinner;
    Spinner makeSpinner;
    Spinner modelSpinner;
    private static DocumentBuilderFactory dbFactory;
    ArrayList<String> carModelArrayList;
    ArrayList<String> carMakeArrayList;
    ArrayList<String> carYearArrayList;
    String url;


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
        yearSpinner = (Spinner) findViewById(R.id.spinner);
        makeSpinner = (Spinner) findViewById(R.id.makeSpinner);
        modelSpinner = (Spinner) findViewById(R.id.modelSpinner);
        dbFactory = DocumentBuilderFactory.newInstance();
        carModelArrayList = new ArrayList<String>();
        carYearArrayList = new ArrayList<String>();
        carMakeArrayList = new ArrayList<String>();
        url = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/year";
        JsonRequest getYears = new JsonRequest();
        getYears.execute("http://www.fueleconomy.gov/ws/rest/vehicle/menu/year");



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

    private class JsonRequest extends AsyncTask<String, Void, Document> {

        @Override
        protected void onPreExecute() {
            milesToTravel = "";
        }
        @Override
        protected Document doInBackground(String... strings) {
            String uri = "http://www.fueleconomy.gov/ws/rest/vehicle/menu/model?year=" + carYear.getText().toString()+ "&make=" + carMake.getText().toString();

            int status;
            HttpURLConnection uconn;
            try {
                DocumentBuilder docBuilder;
                docBuilder = dbFactory.newDocumentBuilder();

                URL u = new URL(strings[0].replaceAll(" ","%20"));
                uconn = (HttpURLConnection) u.openConnection();
                status = uconn.getResponseCode();
                Document doc = docBuilder.parse(uconn.getInputStream());
                //Log.d("HANS", "XML parsed: " + doc.getNodeName());
                return doc;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            //ABOVE GETS XML DATA

            /*I shortened up this code a bit using the URL class in java for efficiency and shorter code -charles*/
            /*String json = "";
            try {
                URL theURL = new URL(strings[0]);
                Scanner scan = new Scanner(theURL.openStream());
                while (scan.hasNextLine()) {
                    json += scan.nextLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return (json);*/
        }

            @Override
        protected void onPostExecute(Document s) {
            super.onPostExecute(s);

           /* NodeList test = s.getElementsByTagName("menuItem");
                for(int k = 0; k < test.getLength(); k++)
                {
                   NodeList level2 = test.item(k).getChildNodes();
                   Node temp = level2.item(0);
                   carModelArrayList.add(temp.getTextContent());

                }*/

                NodeList xmlElements = s.getElementsByTagName("menuItem");
                for(int k = 0; k < xmlElements.getLength(); k++)
                {
                    NodeList level2 = xmlElements.item(k).getChildNodes();
                    Node temp = level2.item(0);
                    if(url.contains("make?"))
                    {

                        carMakeArrayList.add(temp.getTextContent());
                        ArrayAdapter<String> adapter;
                        adapter = new ArrayAdapter<String>(getApplication(),android.R.layout.simple_spinner_dropdown_item, carMakeArrayList);
                        makeSpinner.setAdapter(adapter);
                    }
                    if(url.contains("/year"))
                    {
                        carYearArrayList.add(temp.getTextContent());
                        ArrayAdapter<String> adapter;
                        adapter = new ArrayAdapter<String>(getApplication(),android.R.layout.simple_spinner_dropdown_item, carYearArrayList);
                        yearSpinner.setAdapter(adapter);
                    }
                    if(url.contains("model?"))
                    {
                        carModelArrayList.add(temp.getTextContent());
                        ArrayAdapter<String> adapter;
                        adapter = new ArrayAdapter<String>(getApplication(),android.R.layout.simple_spinner_dropdown_item, carModelArrayList);
                        modelSpinner.setAdapter(adapter);
                    }


                }



                //ABOVE PARSES XML




            //removed unnecessary initializers -charles
//            JSONObject topJsonObject;
//            JSONArray routes = new JSONArray();
//            JSONObject holder1 = new JSONObject();
//            JSONArray legs = new JSONArray();
//            JSONObject holder2 = new JSONObject();
//            JSONObject distance = new JSONObject();
            /*try {
                JSONObject topJsonObject = new JSONObject(s);
                JSONArray routes = topJsonObject.getJSONArray("routes");
                JSONObject holder1 = routes.getJSONObject(0);
                JSONArray legs = holder1.getJSONArray("legs");
                JSONObject holder2 = legs.getJSONObject(0);
                JSONObject distance = holder2.getJSONObject("distance");
                milesToTravel = distance.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }*/


        }
    }
}
