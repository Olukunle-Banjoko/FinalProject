package algonquin.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/*
  Charging Station Details Activity class which displays the details of the selected charging station
  from the search and points to direction

 */
public class ChargingStationDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback{

    private static final int REQUEST_LOCATION = 1;
    TextView locinfo, longinfo, latinfo, telinfo;
    LocationManager locationManager;
    Button btn;
    MapView mapView;
    float longitude, latitude;
    private GoogleMap mMap;
    Context ct;
    Polyline currentPolyLine;
    Button favbtn;

    private static final String MAPVIEW_BUNDLE_KEY="MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EvOpenHelper opener = new EvOpenHelper( this );
        SQLiteDatabase dbase = opener.getWritableDatabase();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.finder_fragment);

        locinfo = (TextView) findViewById(R.id.locinfo);
        longinfo = (TextView) findViewById(R.id.longinfo);
        latinfo = (TextView) findViewById(R.id.latinfo);
        telinfo = (TextView) findViewById(R.id.telinfo);
        favbtn = (Button) findViewById(R.id.favadded);
        btn = (Button) findViewById(R.id.directioninfo);
        ct = getApplicationContext();

        Intent i = getIntent();
        locinfo.setText("Location Title: "+i.getStringExtra("name"));
        longinfo.setText("Longitude: "+i.getStringExtra("long"));
        longitude = Float.parseFloat(i.getStringExtra("long"));
        latinfo.setText("Latitude: "+i.getStringExtra("lat"));
        latitude = Float.parseFloat(i.getStringExtra("lat"));
        if (i.getStringExtra("phone").equals("")){
            telinfo.setText("Telephone: --- NOT FOUND ---");
        }
        else{
            telinfo.setText("Telephone: "+i.getStringExtra("phone"));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        favbtn.setOnClickListener((clk) -> {
            ContentValues newRow = new ContentValues();

        });
    }

    public void onClick(View view) {
        String latitude = String.valueOf(45.347571);
        String longitude = String.valueOf(-75.756140);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private String getUrl(LatLng origin, LatLng dest, String mode){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String mode1 = "mode="+mode;
        String params = str_origin+"&"+str_dest+"&"+mode1;

        String output = "json";

        String url = "https://www.googleapis.com/maps/api/directions/"+output+"?"+params+"&key=AIzaSyDu2VMatt56gV-VGuxHh9Ey0VPxaNT5JcY";
        return url;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker at "+locinfo.getText()));
        float zoomLevel = 12.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyLine != null){
            currentPolyLine.remove();
        }
        currentPolyLine = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
