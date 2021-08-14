/** OC Transpo App
 * @Author Cody Varrette
 * @Version August 13th, 2021
 */

package algonquin.cst2335.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class OCTranspo extends AppCompatActivity {

    RecyclerView busList;
    ArrayList<busInfo> busses = new ArrayList<>();
    MyChatAdapter adt = new MyChatAdapter();

    /** These string values build the URLS used to create JSON Files */
    String stringURL;
    String stringURL2;

    /** Declarations for the JSON Objects taken from the API */
    JSONObject tripDest;
    JSONObject latit;
    JSONObject longi;
    JSONObject gpsS;
    JSONObject startT;
    JSONObject schedAdj;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.octranspo);

        busList = findViewById(R.id.results);
        busList.setAdapter(adt);
        busList.setLayoutManager(new LinearLayoutManager(this));

        Button enterButton = findViewById(R.id.search_button);
        Button menuButton = findViewById(R.id.menuButton);
        Button helpButton = findViewById(R.id.helpButton);
        EditText enterStop = findViewById(R.id.busRouteNumber);
        EditText enterBus = findViewById(R.id.busNumber);

        menuButton.setOnClickListener( clk -> {
            Intent nextPage = new Intent (OCTranspo.this, MainActivity.class);
            startActivity(nextPage);
        });

        helpButton.setOnClickListener( clk -> {
            AlertDialog.Builder helpAlert = new AlertDialog.Builder(this);
            helpAlert.setMessage("Enter Bus Number and Stop Number to view schedule information about the chosen route");
            helpAlert.setNeutralButton("OK", (arg0, arg1) -> finish());
            helpAlert.show();
        });

        enterButton.setOnClickListener( clk -> {
            Executor newThread = Executors.newSingleThreadExecutor();
            newThread.execute( () -> {
                String enterStopNum = enterStop.getText().toString();
                try {

                    stringURL = "https://api.octranspo1.com/v2.0/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo="
                            + URLEncoder.encode(enterStopNum, "UTF-8")
                            + "&format=json";
                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String text = (new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    JSONObject theDocument = new JSONObject(text);
                } catch (IOException | JSONException ioe) {
                    Log.e("Connection error", ioe.getMessage());
                }
                try {
                    String enterBusNum = enterBus.getText().toString();

                    stringURL2 = "https://api.octranspo1.com/v2.0/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo="
                            + URLEncoder.encode(enterStopNum, "UTF-8")
                            +"&routeNO="
                            +URLEncoder.encode(enterBusNum, "UTF-8")
                            + "&format=json";
                    URL url2 = new URL(stringURL2);
                    HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
                    InputStream in2 = new BufferedInputStream(urlConnection2.getInputStream());

                    String text2 = (new BufferedReader(new InputStreamReader(in2, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    JSONObject theDocument2 = new JSONObject( text2 );
                    tripDest = theDocument2.getJSONObject("TripDestination");
                }
                catch (IOException | JSONException ioe){

                }
            });

            busInfo thisRoute = new busInfo("Stop Number: " + enterStop.getText().toString(), "Bus Number: " + enterBus.getText().toString(), "destination" , "lat", "long", "gps", "start", "sch");
            busses.add(thisRoute);
            adt.notifyItemInserted(busses.size()-1);
        });
    }

    /** This class contains everything within the recycler view
     *  makes use of the textviews from the busrouteinfo.xml class
     */
    private class MyRowViews extends RecyclerView.ViewHolder{

        TextView numberText;
        TextView busText;
        TextView destinationText;
        TextView latText;
        TextView longText;
        TextView gpsText;
        TextView startTimeText;
        TextView scheduleText;
        int position = - 1;

        public MyRowViews(View itemView) {
            super(itemView);

            itemView.setOnClickListener( click -> {
                AlertDialog.Builder builder = new AlertDialog.Builder( OCTranspo.this );
                builder.setMessage("Do you want to delete this result for " + numberText.getText() + "?")
                        .setTitle("Question:")
                        .setNegativeButton("No", (dialog, cl) -> { })
                        .setPositiveButton("Yes", (dialog, cl) -> {

                            position = getAdapterPosition();

                            busInfo removedResult = busses.get(position);
                            busses.remove(position);
                            adt.notifyItemRemoved(position);

                            Snackbar.make(numberText, "You deleted result #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        busses.add(position, removedResult);
                                        adt.notifyItemInserted(position);
                                    })
                                    .show();
                        })
                        .create().show();


            });

            numberText = itemView.findViewById(R.id.stopNumberView);
            busText = itemView.findViewById(R.id.busNumberView);
            destinationText = itemView.findViewById(R.id.destinationView);
            latText = itemView.findViewById(R.id.latitudeView);
            longText = itemView.findViewById(R.id.longitudeView);
            gpsText = itemView.findViewById(R.id.gpsView);
            startTimeText = itemView.findViewById(R.id.startView);
            scheduleText = itemView.findViewById(R.id.scheduleTimeView);
        }

        public void setPosition(int p) { position = p; }
    }

    /** This class is responsible for controlling the MyRowView class and will signal it to redraw
     * everytime a new object is added, removed, or replaced.
     *
     * @Param position , where in the array the object is stored
     * @Returns instances of MyRowViews each time it is changed
     * @Returns The array size of busses
     */
    private class MyChatAdapter extends RecyclerView.Adapter<MyRowViews> {
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyRowViews(getLayoutInflater().inflate(R.layout.busrouteinfo, parent, false));
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.numberText.setText(busses.get(position).getStopNum());
            holder.busText.setText(busses.get(position).getBusNum());
            holder.destinationText.setText(busses.get(position).getDestination());
            holder.latText.setText(busses.get(position).getLatitude());
            holder.longText.setText(busses.get(position).getLongitude());
            holder.gpsText.setText(busses.get(position).getSpeed());
            holder.startTimeText.setText(busses.get(position).getTime());
            holder.scheduleText.setText(busses.get(position).getSchedule());
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return busses.size();
        }
    }

    /** This class contains all the variables to be returned to the user after parsing the JSON File
     *
     * @Return stopNum , the stop number the user searched for
     * @Return busNum , the bus number the user searched for
     * @Return destination, where the bus is going
     * @Return latitude , the latitude of the bus
     * @Return longitude , the longitude of the bus
     * @Return speed , the gps speed of the bus
     * @Return time , the trip start time
     * @Return schedule , how late or early the bus is going to be
     */
    private class busInfo {
        String stopNum;
        String busNum;
        String destination;
        String latitude;
        String longitude;
        String speed;
        String time;
        String schedule;

        public busInfo(String stopNum, String busNum, String destination, String latitude, String longitude, String speed, String time, String schedule) {
            this.stopNum = stopNum;
            this.busNum = busNum;
            this.destination = destination;
            this.latitude = latitude;
            this.longitude = longitude;
            this.speed = speed;
            this.time = time;
            this.schedule = schedule;
        }

        public String getStopNum() {
            return stopNum;
        }

        public String getBusNum() {
            return busNum;
        }

        public String getDestination() {
            return destination;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public String getSpeed() {
            return speed;
        }

        public String getTime() {
            return time;
        }

        public String getSchedule() {
            return schedule;
        }
    }
}