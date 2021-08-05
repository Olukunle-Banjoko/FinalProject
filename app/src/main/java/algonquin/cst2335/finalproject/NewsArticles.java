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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

    NewsListFragment newsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.empty_layout);

        newsFragment = new NewsListFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.fragmentRoom, newsFragment);
        tx.commit();
    }

    public void userClickedNews(NewsListFragment.newsFeed newsFeed, int position) {
        NewsDetailsFragment ndFragment = new NewsDetailsFragment(newsFeed, position);

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentRoom, ndFragment).commit();
    }

    public void notifyAddNewsFavourite(NewsListFragment.newsFeed selectedNews, int selectedPosition) {
        newsFragment.AddToFavourite(selectedNews, selectedPosition);
    }

    public void deletedNewsFavourite(NewsListFragment.newsFeed selectedNews, int selectedPosition) {
        newsFragment.deleteFavourite(selectedNews, selectedPosition);
    }

}
