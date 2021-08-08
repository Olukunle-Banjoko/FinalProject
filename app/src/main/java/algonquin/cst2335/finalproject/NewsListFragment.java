package algonquin.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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

public class NewsListFragment extends Fragment {
    ArrayList<newsFeed> news = new ArrayList<>();
    MyNewsAdapter adt = new MyNewsAdapter();

    private String stringURL;
    RecyclerView newsList;
    SQLiteDatabase db;
    Button save;

    Executor newThread = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View newsLayout = inflater.inflate(R.layout.newslayout, container, false);
        save = newsLayout.findViewById(R.id.savebutton);
    //-----------------

        Button save = newsLayout.findViewById(R.id.savebutton);
        Button load = newsLayout.findViewById(R.id.loadbutton);
        RecyclerView newsList = newsLayout.findViewById(R.id.myrecycler);
        EditText currentRating = newsLayout.findViewById(R.id.newsRating);
        this.newsList = newsList;

        //----Toolbar
        Toolbar myToolbar = newsLayout.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        setHasOptionsMenu(true);

        /*
        DrawerLayout drawer = newsLayout.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = newsLayout.findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener((item) -> {

            onOptionsItemSelected(item);  //Call the function for the other Toolbar
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });
        */


        //----Open Database in writable mode
        MyOpenHelper opener = new MyOpenHelper(getContext());
        db = opener.getWritableDatabase();
        //----

        //-----App Rating--------
        SharedPreferences prefs = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String rating = prefs.getString("Rating", "");

        if (rating == "" || rating ==null){

            final String[] m_Text = {""};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Please enter your rating (1-5)");

            // Set up the input
                final EditText input = new EditText(getContext());
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //m_Text[0] = input.getText().toString();
                    //--Save rating to shared preference
                    SharedPreferences.Editor  editor = prefs.edit();
                    editor.putString("Rating",input.getText().toString());
                    editor.apply();
                    currentRating.setText("App Rating : " + input.getText().toString() + "/5");
                    currentRating.setInputType(InputType.TYPE_NULL);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }else{
             currentRating.setText("App Rating : " + rating + "/5");
             currentRating.setInputType(InputType.TYPE_NULL);
        }
        //----End of Rating

        //----Code for loading button
        load.setOnClickListener( (click) ->{
            loadSoccerNews(newsList);

       }); //--- End of loading news (Click button)


        //-----Code for Favourites button
        save.setOnClickListener( (click) ->{
            loadFavourites(newsList);

        }); //---End of loading favourites (Favourites button)

        return  newsLayout;

    }






    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       // inflater.inflate(R.menu.main_activity_actions_news, menu);
        //super.onCreateOptionsMenu(menu,inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions_news, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.id_loadnews:
                loadSoccerNews(newsList);
                break;
            case R.id.id_favourites:
                loadFavourites(newsList);
                break;
            case R.id.id_help:
                viewHelp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
        builder.setMessage("Click on load news to get the live news updates \n " +
                "Click on Favourites to view the saved news \n Click on any news to view more details ")
                .setTitle("Help")
                .setPositiveButton("Ok",(dialog, cl) ->{

                })
                .create().show();

    }

    private void loadFavourites(RecyclerView newsList) {

        news.clear(); //Clear the array to load favourites

        newThread.execute( () -> {
            try{
                Cursor results = db.rawQuery("Select * from " + MyOpenHelper.TABLE_NAME + ";", null);

                int _idCol = results.getColumnIndex("_id");
                int titleCol = results.getColumnIndex(MyOpenHelper.col_title);
                int dateCol = results.getColumnIndex(MyOpenHelper.col_date);
                int descCol = results.getColumnIndex(MyOpenHelper.col_desc);
                int newsLinkCol = results.getColumnIndex(MyOpenHelper.col_newsURL);
                int imgLinkCol = results.getColumnIndex(MyOpenHelper.col_imageURL);
                Bitmap newsImage = null;

                //load previous messages from the DB
                while (results.moveToNext()) {
                    long id = results.getInt(_idCol);
                    String title = results.getString(titleCol);
                    String newsDate = results.getString(dateCol);
                    String newsDesc = results.getString(descCol);
                    String newsLink = results.getString(newsLinkCol);
                    String imgLink = results.getString(imgLinkCol);


                    //------Convert image URL to Bitmap

                    URL imgUrl = new URL(imgLink);
                    HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        newsImage = BitmapFactory.decodeStream(connection.getInputStream());

                    }

                    //---End of converting Bitmap

                    //---Add news to the array
                    news.add(new newsFeed(title, newsDate, newsImage, newsDesc, newsLink,id,imgLink,true ));
                }

                getActivity().runOnUiThread((  ) -> {
                    newsList.setAdapter(adt);
                    newsList.setLayoutManager(new LinearLayoutManager(getContext()));

                    Toast toast = Toast.makeText(getContext(), "Click on news to view details",Toast.LENGTH_LONG);
                    toast.show();

                });



            }
            catch(IOException ioe){
                Log.e("Connection error:", ioe.getMessage());
            }

        } );

    }

    private void loadSoccerNews(RecyclerView newsList) {
        //-----View Progress Bar
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Getting News Data")
                .setMessage("Please wait while loading news....")
                .setView(new ProgressBar(getContext()))
                .show();

        //---End of loading progress bar

        news.clear(); //Clear the array to load news

        //---Getting data from news feed

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

                        newsFeed thisNews = new newsFeed(newsTitle1 ,newsDate,newsImage,description,urlLink,imageLink,false);
                        news.add( thisNews );
                        newsTitle1 = null;
                        newsDate = null;
                        description = null;
                        urlLink = null;
                        imageLink = null;
                    }

                }

                getActivity().runOnUiThread((  ) -> {
                    newsList.setAdapter(adt);
                    newsList.setLayoutManager(new LinearLayoutManager(getContext()));

                    Toast toast = Toast.makeText(getContext(), "Click on news to view details",Toast.LENGTH_LONG);
                    toast.show();
                    dialog.hide();
                });
            }
            catch(IOException | XmlPullParserException ioe){
                Log.e("Connection error:", ioe.getMessage());
            }

        } );

        //----End of getting news feed


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
               NewsArticles parentActivity = (NewsArticles)getContext();
               int position= getAbsoluteAdapterPosition();
               parentActivity.userClickedNews(news.get(position),position);

            });

            newsTitle = itemView.findViewById(R.id.newstitle);
            newsDate  = itemView.findViewById(R.id.newsdate);
            newsImage = itemView.findViewById(R.id.imageTumb);
        }

        public void setPosition(int p) { position=p; }
    }

    //-------------

    public class MyNewsAdapter extends RecyclerView.Adapter{
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
    }

    //------

    public class newsFeed
    {
        public String title;
        public String newsDate;
        public Bitmap newsImage;
        public String newsDesc;
        public String newsUrl;
        public long id;
        public String imageLink;
        public Boolean isFavourites;


        public newsFeed(String title, String newsDate, Bitmap newsImage, String newsDescription, String newsLink, String imageLink,boolean isFavourites){
            this.title = title;
            this.newsDate = newsDate;
            this.newsImage = newsImage;
            this.newsDesc = newsDescription;
            this.newsUrl = newsLink;
            this.imageLink = imageLink;
            this.setIsFavourites(isFavourites);
        }

        public newsFeed(String title, String newsDate, Bitmap newsImage, String newsDescription, String newsLink, long id, String imageLink, boolean isFavourites) {
            this.title = title;
            this.newsDate = newsDate;
            this.newsImage = newsImage;
            this.newsDesc = newsDescription;
            this.newsUrl = newsLink;
            this.imageLink = imageLink;
            this.setIsFavourites(isFavourites);
            setId(id);
        }

        public String getTitle(){return  title;}
        public String getNewsDate(){return newsDate;}
        public String getNewsDesc(){return newsDesc;}
        public String getNewsUrl() {return newsUrl;}
        public Bitmap getNewsImage(){return newsImage;}
        public String getImageLink() {return imageLink;}
        public void setId(long l) {
            this.id = l;
        }
        public long getId() {
            return id;
        }

        public void setIsFavourites(boolean isFavou){
            isFavourites = isFavou;
        }

        public  boolean getFavourite(){
            return  isFavourites;
        }
    }

    public void AddToFavourite(newsFeed chosenNews, int chosenPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
        builder.setMessage("Are you sure you want to add to favourite:" + chosenNews.getTitle())
                .setTitle("Favourite News")
                .setNegativeButton("No",(dialog, cl) ->{ })
                .setPositiveButton("Yes",(dialog, cl) ->{

                    newsFeed savedNews = news.get(chosenPosition); //Keep the deleted news to undo if needed
                    //news.remove(chosenPosition);
                   // adt.notifyItemRemoved(chosenPosition);


                    ContentValues newRow = new ContentValues();
                    newRow.put(MyOpenHelper.col_title, chosenNews.getTitle());
                    newRow.put(MyOpenHelper.col_date, chosenNews.getNewsDate());
                    newRow.put(MyOpenHelper.col_desc, chosenNews.getNewsDesc());
                    newRow.put(MyOpenHelper.col_newsURL, chosenNews.getNewsUrl());
                    newRow.put(MyOpenHelper.col_imageURL, chosenNews.getImageLink());
                    long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_title, newRow);



                    Snackbar.make(save,"You added a news to favourites", Snackbar.LENGTH_LONG)
                            .setAction("Undo", clk -> {
                                //Undo the save news
                                news.remove(chosenPosition);
                                db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[]{
                                        Long.toString(newId)
                                });

                            })
                    .show();

                })
                .create().show();

    }

    public void deleteFavourite(newsFeed chosenNews, int chosenPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
        builder.setMessage("Are you sure you want to delete this news from the favourites?" )
                .setTitle("Delete News")
                .setNegativeButton("No",(dialog, cl) ->{ })
                .setPositiveButton("Yes",(dialog, cl) ->{

                    newsFeed deletedNews = news.get(chosenPosition); //Keep the deleted news to undo if needed
                    news.remove(chosenPosition);
                    adt.notifyItemRemoved(chosenPosition);

                    db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[]{
                            Long.toString(deletedNews.getId())
                    });

                    Toast toast = Toast.makeText(getContext(), "This news is deleted from the favourites list",Toast.LENGTH_LONG);
                    toast.show();

                })
                .create().show();

    }



}
