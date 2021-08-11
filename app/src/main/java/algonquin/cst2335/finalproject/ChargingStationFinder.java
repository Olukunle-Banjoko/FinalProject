package algonquin.cst2335.finalproject;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
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

/*
  Charging Station finder class: Extends AppcompatActivity and searches for the charging stations near
  based on user inputed longitude and latitude
  @author Olukunle Banjoko
  @version 1.0.0
  @param edttext: editable longitude
         edttext2 editable latitude
         btn search button

 */
public class ChargingStationFinder extends AppCompatActivity {

    private EditText edttext;
    private EditText edttext2;
    private Button btn;
    private String stringURL;
    String name = "";
    String address;
    float _lat;
    float _long;
    float distance;
    String contact;
    ArrayList<ChargingStation> cs;
    Builder builder;
    RecyclerView recyclerView;
    RecyclerView.Adapter rvAdapter;
    RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);

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
        cs = new ArrayList<>();
        Context ct = this;
        setContentView(R.layout.activity_charging_station_finder);
        edttext = findViewById(R.id.longitude);
        edttext2 = findViewById(R.id.latitude);
        ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar1);
        recyclerView = (RecyclerView) findViewById(R.id.favrecyclerview);
        spinner.setVisibility(recyclerView.GONE);
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

        btn.setOnClickListener((click) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Latinput", edttext2.getText().toString());
            editor.putString("Longinput", edttext.getText().toString());
            editor.apply();
            spinner.setVisibility(recyclerView.VISIBLE);
            cs.clear();
            Executor newThread = Executors.newSingleThreadExecutor();
            newThread.execute(() -> {
                try {
                    String lat = edttext2.getText().toString();
                    String longi = edttext.getText().toString();
                    stringURL = "https://api.openchargemap.io/v3/poi/?key=294325e9-6da5-47c3-8499-5c869e115a35&output=xml&countrycode=CA&latitude="
                    +lat
                    +"&longitude="
                    +longi
                    +"&maxresults=10";
                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);

                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(in, "UTF-8");

                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                switch (xpp.getName()){
                                    case "LocationTitle":
                                        if (!name.equals("")) {
                                            cs.add(new ChargingStation(name, address, _lat, _long, distance, contact));
                                        }
                                        name = xpp.nextText();
                                        System.out.println(name);
                                        break;
                                    case "AddressLine1":
                                        address = xpp.nextText();
                                        break;
                                    case "Latitude":
                                        _lat = Float.parseFloat(xpp.nextText());
                                        break;
                                    case "Longitude":
                                        _long = Float.parseFloat(xpp.nextText());
                                        break;
                                    case "Distance":
                                        distance = Float.parseFloat(xpp.getAttributeValue(null, "Value"));
                                        break;
                                    case "ContactTelephone1":
                                        contact = xpp.nextText();
                                        break;
                                }
                        }
                        eventType = xpp.next();
                    }

                } catch (IOException | XmlPullParserException e) {
                    e.printStackTrace();
                }

                cs.add(new ChargingStation(name, address, _lat, _long, distance, contact));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(cs.size());
                        spinner.setVisibility(recyclerView.GONE);
                        rvAdapter = new ChargingStationAdapter(ct, cs);
                        recyclerView.setLayoutManager(rLayoutManager);
                        recyclerView.setAdapter(rvAdapter);
                    }
                });
            });
            Snackbar.make(btn, "Search in progress", Snackbar.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Searching EV Charging Stations near you", Toast.LENGTH_SHORT).show();
        });
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
}