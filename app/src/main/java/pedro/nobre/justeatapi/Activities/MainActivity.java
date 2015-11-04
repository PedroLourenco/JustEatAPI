package pedro.nobre.justeatapi.Activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pedro.nobre.justeatapi.Util.GPSTracker;
import pedro.nobre.justeatapi.R;

public class MainActivity extends AppCompatActivity {


    Button btnShowLocation;
    // GPSTracker class
    GPSTracker gps;
    TextView tvPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button btFind = (Button) findViewById(R.id.buttonFind);
        btFind.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                tvPostalCode = (TextView) findViewById(R.id.edtPostalCode);

                String postalCode = tvPostalCode.getText().toString();

                if (postalCode.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.postalCodeEmpty), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, RestaurantListActivity.class);
                    intent.putExtra("POSTALCODE", postalCode);
                    startActivity(intent);
                }
            }
        });


        btnShowLocation = (Button) findViewById(R.id.buttonFindLocation);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    String code = GetPostalCode(latitude, longitude);
                    Log.d("TAG", "GPS: " + code);
                    tvPostalCode = (TextView) findViewById(R.id.edtPostalCode);
                    tvPostalCode.setText(code);

                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

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


    private String GetPostalCode(double latitude, double longitude) {
        String postalCode = null;
        if(latitude != 0 && longitude != 0) {

            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                postalCode = addresses.get(0).getPostalCode();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return postalCode;
    }

}
