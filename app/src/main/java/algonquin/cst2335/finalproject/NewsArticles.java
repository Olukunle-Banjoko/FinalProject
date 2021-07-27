package algonquin.cst2335.finalproject;

import android.app.AlertDialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

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
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NewsArticles extends AppCompatActivity {
    ArrayList<newsFeed> news = new ArrayList<>();
    MyNewsAdapter adt = new MyNewsAdapter();

    private String stringURL;
    RecyclerView newsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stringURL ="http://www.goal.com/en/feeds/news?fmt=rss";

        setContentView(R.layout.newslayout);

        //--------------

        String newsTitle1="Olympic soccer standings 2021";
        String newsDate ="21-Jul-2021";
        ImageView newsImage = findViewById(R.id.newsimage);

        Button save = findViewById(R.id.savebutton);
        Button load = findViewById(R.id.loadbutton);
        RecyclerView newsList = findViewById(R.id.myrecycler);

        newsList.setAdapter(adt);


        newsList.setLayoutManager(new LinearLayoutManager(this));


        //newsFeed thisNews = new newsFeed(newsTitle1 ,newsDate,newsImage);
        newsFeed thisNews = new newsFeed(newsTitle1 ,newsDate,newsImage);
        news.add( thisNews );
        adt.notifyItemInserted(news.size()-1);

        //-------------

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

    //--------MyRowView

    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView newsTitle;
        TextView newsDate;
        Image newsImage;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);

            itemView.setOnClickListener( click -> {
                AlertDialog.Builder builder = new AlertDialog.Builder( NewsArticles.this );
                builder.setMessage("Do you want to save the news:" + newsTitle.getText())
                        .setTitle("Question")
                        .setNegativeButton("No",(dialog, cl) ->{ })
                        .setPositiveButton("Yes",(dialog, cl) ->{

                            position=getAbsoluteAdapterPosition();

                            newsFeed saveNews = news.get(position); //Keep the delted message text to undo if needed
                            //news.remove(position);
                            //adt.notifyItemRemoved(position);

                            Snackbar.make(newsTitle,"You saved news #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        //Undo the delete
                                        news.add(position,saveNews);
                                        adt.notifyItemInserted(position);
                                    })
                                    .show();
                        })
                        .create().show();
            });

            newsTitle = itemView.findViewById(R.id.message);
            newsDate  = itemView.findViewById(R.id.time);
        }

        public void setPosition(int p) { position=p; }
    }

    //-------------

    private class MyNewsAdapter extends RecyclerView.Adapter{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int layoutID;

            layoutID=R.layout.load_news;

            View loadedRow = inflater.inflate(layoutID, parent, false);
            MyRowViews initRow = new MyRowViews(loadedRow);

            return initRow;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MyRowViews thisView = (MyRowViews)holder;
            thisView.newsTitle.setText(news.get(position).getTitle());
            thisView.newsDate.setText(news.get(position).getNewsDate());
            thisView.setPosition(position);
        }

        @Override
        public int getItemCount() {
            //Numbwer of items in array
            return news.size();
        }



    }

    //------

    private class newsFeed
    {
        public String title;
        public String newsDate;
        public ImageView newsImage;

        public newsFeed(String title, String newsDate, ImageView newsImage){
            this.title = title;
            this.newsDate = newsDate;
            this.newsImage = newsImage;
        }

        public String getTitle(){return  title;}
        public String getNewsDate(){return newsDate;}
        public ImageView getNewsImage(){return newsImage;}
    }



}
