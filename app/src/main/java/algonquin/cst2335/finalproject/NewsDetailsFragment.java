package algonquin.cst2335.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class NewsDetailsFragment extends Fragment {

    NewsListFragment.newsFeed selectedNews;
    int selectedPosition;
    boolean isFavourite;

    public NewsDetailsFragment(NewsListFragment.newsFeed news, int position){
        selectedNews = news;
        selectedPosition = position;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsView = inflater.inflate(R.layout.details_layout, container,false);

        ImageView newsImage = detailsView.findViewById(R.id.newsImage);
        TextView newsDate = detailsView.findViewById(R.id.newsDate);
        TextView newsDesc = detailsView.findViewById(R.id.newsDescription);
        TextView newsLink = detailsView.findViewById(R.id.newsUrl);


        newsImage.setImageBitmap(selectedNews.getNewsImage());
        newsDesc.setText(selectedNews.getNewsDesc());
        newsLink.setText(selectedNews.getNewsUrl());
        newsDate.setText(selectedNews.getNewsDate());

        Button closeButton = detailsView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(closeClicked -> {
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });


        Button addFavourite = detailsView.findViewById(R.id.addFavourites);
        Button deleteFavourite = detailsView.findViewById(R.id.deleteFavourites);

        //--Set favourites button type based on the news type
        if (selectedNews.getFavourite()==false)
         {
            deleteFavourite.setVisibility(View.INVISIBLE);
            addFavourite.setVisibility(View.VISIBLE);
        }else{
            addFavourite.setVisibility(View.INVISIBLE);
            deleteFavourite.setVisibility(View.VISIBLE);
        }

        addFavourite.setOnClickListener(addClicked -> {
            NewsArticles parentActivity = (NewsArticles)getContext();
            parentActivity.notifyAddNewsFavourite(selectedNews,selectedPosition);

        });

        //----Delete from favourites
        deleteFavourite.setOnClickListener(deleteClicked -> {
            NewsArticles parentActivity = (NewsArticles)getContext();
            parentActivity.deletedNewsFavourite(selectedNews,selectedPosition);
        });

        //---View URL in browser
        Button viewBrowser = detailsView.findViewById(R.id.viewBrowser);

        viewBrowser.setOnClickListener(viewClicked -> {

            String url = newsLink.getText().toString();
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        return detailsView;

    }
}
