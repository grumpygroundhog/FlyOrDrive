package edu.gvsu.cis.getterj.flyordrive;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class ResultsActivity extends Activity {
    private TextView flyCost;
    private TextView flyTime;
    private TextView flyMiles;
    private TextView driveCost;
    private TextView driveTime;
    private TextView driveMiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        flyCost = (TextView) findViewById(R.id.fCost);
        flyTime = (TextView) findViewById(R.id.fTime);
        flyMiles = (TextView) findViewById(R.id.fMiles);
        driveCost = (TextView) findViewById(R.id.dCost);
        driveTime = (TextView) findViewById(R.id.dTime);
        driveMiles= (TextView) findViewById(R.id.dMiles);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
}
