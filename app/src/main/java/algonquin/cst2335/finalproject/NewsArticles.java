package algonquin.cst2335.finalproject;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class NewsArticles extends AppCompatActivity {
    ArrayList<newsFeed> news = new ArrayList<>();
    MyNewsAdapter adt = new MyNewsAdapter();

    private String stringURL;
    RecyclerView newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newslayout);

        Button save = findViewById(R.id.savebutton);
        Button load = findViewById(R.id.loadbutton);
        RecyclerView newsList = findViewById(R.id.myrecycler);

        load.setOnClickListener( (click) ->{

        //---Getting data from news feed
        Executor newThread = Executors.newSingleThreadExecutor();

        newThread.execute( () -> {
            try{
                stringURL ="https://www.goal.com/en/feeds/news?fmt=rss&mode=xml";

                URL url = new URL(stringURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( in  , "UTF-8");

                String description = null;
                String newsTitle1 = null;
                String newsDate =null;
                String urlLink =null;
                String imageLink = null;

                Bitmap newsImage = null;

                while (xpp.next() !=XmlPullParser.END_DOCUMENT)
                {
                    switch (xpp.getEventType())
                    {
                        case XmlPullParser.START_TAG:
                            if (xpp.getName().equals("title"))
                            {
                               newsTitle1 = xpp.nextText() ;  //this gets the news title


                            }
                            else if (xpp.getName().equals("pubDate"))
                            {
                                newsDate = xpp.nextText();
                               // newsDate = xpp.getAttributeValue(null, "pubDate"); //this gets the publication date
                               // newsImage = xpp.getAttributeValue(null, "icon"); //this gets the icon name
                            }
                            else if (xpp.getName().equals("link"))
                            {
                                urlLink = xpp.nextText() ; //this gets the news link

                            }
                            else if (xpp.getName().equals("description"))
                            {
                                description = xpp.nextText() ; //this gets the news description

                            }
                            else if (xpp.getName ().equals("media:thumbnail"))
                            {
                                imageLink = xpp.getAttributeValue(null,"url");
                                imageLink = imageLink.replace("http:","https:");
                            }
                            break;
                        case XmlPullParser.END_TAG:

                            break;
                        case XmlPullParser.TEXT:
                            break;
                    }

                    if (newsTitle1 !=null && newsDate !=null && description !=null && urlLink !=null && imageLink != null){

                        //----Get image from URL

                        URL imgUrl = new URL(imageLink);
                        HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            newsImage = BitmapFactory.decodeStream(connection.getInputStream());

                        }
                        //--- End of gettign the image

                        newsFeed thisNews = new newsFeed(newsTitle1 ,newsDate,newsImage);
                        news.add( thisNews );
                        newsTitle1 = null;
                        newsDate = null;
                        description = null;
                        urlLink = null;
                        imageLink = null;
                    }
                    //code
                }

                runOnUiThread(( ) -> {
                    newsList.setAdapter(adt);
                    newsList.setLayoutManager(new LinearLayoutManager(this));
                   /*
                    for (int x=1; x<news.size() ;x++) {
                        adt.notifyItemInserted(news.size() - 1);
                    }*/
                });

            }
            catch(IOException | XmlPullParserException ioe){
                Log.e("Connection error:", ioe.getMessage());
            }

        } );

        //----End of getting news feed

        });
    }

    //--------MyRowView

    private class MyRowViews extends RecyclerView.ViewHolder{
        TextView newsTitle;
        TextView newsDate;
        ImageView newsImage;

        //Image newsImage;
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

                            newsFeed saveNews = news.get(position); //Keep the deleted message text to undo if needed
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

            newsTitle = itemView.findViewById(R.id.newstitle);
            newsDate  = itemView.findViewById(R.id.newsdate);
            newsImage = itemView.findViewById(R.id.imageTumb);
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
            thisView.newsImage.setImageBitmap(news.get(position).getNewsImage());
            thisView.setPosition(position);
        }

        @Override
        public int getItemCount() {
            //Number of items in array
            return news.size();
        }

        /*
        @Override
        public int getItemViewType(int position) {
            newsFeed thisRow = news.get(position);
            return thisRow.sendOrReceive;
        }*/


    }




    //------

    private class newsFeed
    {
        public String title;
        public String newsDate;
        public Bitmap newsImage;

        public newsFeed(String title, String newsDate, Bitmap newsImage){
            this.title = title;
            this.newsDate = newsDate;
            this.newsImage = newsImage;
        }

        public String getTitle(){return  title;}
        public String getNewsDate(){return newsDate;}
        public Bitmap getNewsImage(){return newsImage;}
    }
    /*
    private class newsFeed
    {
        public String title;
        public String newsDate;
        public Bitmap newsImage;

        public newsFeed(String title, String newsDate, Bitmap newsImage){
            this.title = title;
            this.newsDate = newsDate;
            this.newsImage = newsImage;
        }

        public String getTitle(){return  title;}
        public String getNewsDate(){return newsDate;}
        public Bitmap getNewsImage(){return newsImage;}
    }
    */

}
