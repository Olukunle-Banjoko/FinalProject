package algonquin.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button finder = findViewById(R.id.finderbtn);
        finder.setOnClickListener( clk -> {
            Intent nextPage = new Intent(MainActivity.this, ChargingStationFinder.class);
            startActivity( nextPage );
        });
    }
}