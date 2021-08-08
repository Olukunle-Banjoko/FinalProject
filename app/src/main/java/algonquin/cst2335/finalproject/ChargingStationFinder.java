package algonquin.cst2335.finalproject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChargingStationFinder extends AppCompatActivity {

    private EditText edttext;
    private EditText edttext2;
    private Button btn;
    private String stringURL;
    String locationTitle = null;
    String latitude = null;
    String longitude = null;
    String contact = null;
    Builder builder;
    ArrayList<ChargingStation> details = new ArrayList<>();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.finder_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.help:
                builder = new Builder(this);
                builder.setMessage("To use this interface, input your longitude & latitude values and click search to find charging stations near you");
                builder.setTitle("Using the interface");
                builder.setNegativeButton("Cancel", (dialog, cl) -> {
                });
                builder.setPositiveButton("Ok", (dialog, cl) -> {
                });
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_station_finder);
        edttext = findViewById(R.id.longitude);
        edttext2 = findViewById(R.id.latitude);
        RecyclerView recyclerView = findViewById(R.id.myrecyclerview);
        btn = findViewById(R.id.searchbtn);
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener((item)-> {
            onOptionsItemSelected(item); //calls the function for the other toolbar
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });
        String search;
        String searchtxt;
        builder = new Builder(this);
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        search = prefs.getString("edttext", "");
        searchtxt = prefs.getString("edttext2", "");
        edttext.setText(search);
        edttext2.setText(searchtxt);

        Toast.makeText(getApplicationContext(), "Enter Value", Toast.LENGTH_LONG).show();


        builder.setMessage("Do you want to continue with the search");
        builder.setTitle("Question");
        builder.setNegativeButton("No", (dialog, cl) -> {
        });
        builder.setPositiveButton("Yes", (dialog, cl) -> {
            Snackbar.make(btn, "Search in progress", Snackbar.LENGTH_LONG).show();
        });
        builder.create().show();


    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("edttext", edttext.getText().toString());
        editor.putString("edttext2", edttext2.getText().toString());
        editor.apply();

    }

    /*
    Data model class
     */
    //implement recyclerview
    private class ChargingStation {
        public ChargingStation() {
            btn.setOnClickListener((click) -> {
                Executor newThread = Executors.newSingleThreadExecutor();
                newThread.execute(() -> {
                    try {
                        stringURL = "https://api.openchargemap.io/v3/poi/?key=294325e9-6da5-47c3-8499-5c869e115a35&output=xml&countrycode=CA&latitude=" + edttext2.getText() + "&longitude=" + edttext.getText() + "&maxresults=10";
                        URL url = new URL(stringURL);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(false);
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(in, "UTF-8");
                        ;
                        while (xpp.next() != XmlPullParser.END_DOCUMENT) {
                            switch (xpp.getEventType()) {
                                case XmlPullParser.START_TAG:
                                    if (xpp.getName().equals("AddressInfo")) {
                                        locationTitle = xpp.getAttributeValue(null, "locationTitle");  //this gets the location title
                                        latitude = xpp.getAttributeValue(null, "latitude"); //this gets the latitude
                                        longitude = xpp.getAttributeValue(null, "longitude"); //this gets the longitude
                                        contact = xpp.getAttributeValue(null, "contactTelephone1"); //this gets the contact telephone
                                        break;
                                    }
                            }
                        }

                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                    }

                });

            });
        }
    }
}