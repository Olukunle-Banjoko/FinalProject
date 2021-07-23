package algonquin.cst2335.finalproject;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NewsArticles extends AppCompatActivity {
    private String stringURL;
    RecyclerView newsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stringURL ="http://www.goal.com/en/feeds/news?fmt=rss";

        setContentView(R.layout.newslayout);




        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () ->{
            try{
                URL url = new URL(stringURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String text = (new BufferedReader(
                        new InputStreamReader(in, StandardCharsets.UTF_8)))
                        .lines()
                        .collect(Collectors.joining("\n"));

                JSONObject theDocument = new JSONObject( text );



            }catch (IOException | JSONException ioe){
                Log.e("Connection error:", ioe.getMessage());
            }


        });

        newsList = findViewById(R.id.myrecycler);
        newsList.setAdapter(new RecyclerView.Adapter() {



            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
    }


    private class newsFeed
    {
        String title;
        Date newsDate;
        Image newsImage;
    }

}
