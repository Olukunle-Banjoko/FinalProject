package algonquin.cst2335.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class ChargingStationFinder extends AppCompatActivity {
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_station_finder);
        name = findViewById(R.id.edit);
        Button btn = findViewById(R.id.button);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String search = prefs.getString("name", "");
        name.setText(search);
        Toast.makeText(getApplicationContext(),"Enter Value",Toast.LENGTH_LONG).show();

        btn.setOnClickListener( clk -> {
            builder.setMessage("Do you want to continue with the search");
            builder.setTitle("Question");
            builder.setNegativeButton("No", (dialog, cl) ->{});
            builder.setPositiveButton("Yes", (dialog, cl) ->{
                Snackbar.make(btn, "Search in progress", Snackbar.LENGTH_LONG).show();
            });
            builder.create().show();

            });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor = prefs.edit();
        editor.putString("name", name.getText().toString());
        editor.apply();

    }
}