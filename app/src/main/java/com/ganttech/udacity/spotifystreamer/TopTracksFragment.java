package com.ganttech.udacity.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment {
    private final String LOG_TAG = TopTracksFragment.class.getSimpleName();
    private Toast mAppToast;
    List<TopTracksRowItem> topTracksRowItems;
    public TopTracksAdapter mTopTracksAdapter;
    public ListView listView;

    public TopTracksFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top10_tracks, container, false);

        // Get the artist name from the intent Extra
        Intent intent = getActivity().getIntent();
        String spotifyID = intent.getStringArrayExtra("artistInfo")[1];    // Get spotifyID
        searchTopStracks(spotifyID);                                // Get top Tracks

        // Create a new array of Top Tracks Row items
        topTracksRowItems = new ArrayList<TopTracksRowItem>();

        // Get List view of artists
        listView = (ListView) rootView.findViewById(R.id.listview_top_tracks);
        // Set up adapter
        mTopTracksAdapter = new TopTracksAdapter(getActivity(), topTracksRowItems);
        listView.setAdapter(mTopTracksAdapter);   // // Add mArtistsAdapter to listView;



        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


    }

    public void noTracksMsg() {
        if (mAppToast != null) {  // if current Toast present, cancel for better User Experience
            mAppToast.cancel();
        }
        //String data = parent.getItemAtPosition(position).toString();
        String data = getString(R.string.toast_no_tracks);
        mAppToast = Toast.makeText(getActivity(), // get current activity
                data,                               // String to pass info
                Toast.LENGTH_LONG);                 // Length
        mAppToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);    // Move to center
        mAppToast.show();
    }

    private void searchTopStracks(String spotifyID) {
        FetchTopTrackTask artist = new FetchTopTrackTask();
        artist.execute(spotifyID);
    }

    /* Class to Fetch Artists Top 10 Tracks using Spotify API Adapter
    * Does this using an AsyncTask (worker thread)
    * ASync work done in doInBackground, result back on UI Thread onPostExecute  */
    public class FetchTopTrackTask extends AsyncTask<String, Void, List<Track>> {

        private final String LOG_TAG = FetchTopTrackTask.class.getSimpleName();

        @Override
        protected List<Track> doInBackground(String... params) {
        /*  For task 2, you will need to request track data via the Get an Artist’s Top Tracks web
         *  endpoint. Specify a country code for the search (the API requires this). You can either
         *  set a hardcoded String in the query call, or make a preference screen to make the
         *  country code user-modifiable. For each track result you should extract the following
         *  data: track name, album name, Album art thumbnail (large (640px for Now Playing screen)
         *  and small (200px for list items)). If the image size does not exist in the API response,
         *  you are free to choose whatever size is available.) preview url* - This is an HTTP url
         *  that you use to stream audio. You won’t need to use this until Stage 2.    */
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            List<Track> topTrackList;

            // https://api.spotify.com/v1/artists/6vWDO969PvNqNYHIOW5v0m/top-tracks?country=US
            // For list of supported parameters see
            // https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
            Map queryParams = new HashMap<String, String>();
            queryParams.put("country", "US");

            // Get Top Tracks results, put into a list
            topTrackList = spotify.getArtistTopTrack(params[0], queryParams).tracks;

            return topTrackList;
        }

        @Override
        protected void onPostExecute(List<Track> tracks) {
            List<Image> imageList;
            String trackName, albumName, albumArtThumbnail = null, albumArtSmall, previewUrl;
            AlbumSimple albumInfo;

            if (!tracks.isEmpty()) {

                mTopTracksAdapter.rowItems.clear();

                // Get Track Name, Album Name, Preview URL
                for (int i = 0; i < tracks.size(); i++) {
                    trackName = tracks.get(i).name;
                    albumInfo = tracks.get(i).album;
                    albumName = albumInfo.name;

                    // This is an HTTP url that you use to stream audio
                    previewUrl = tracks.get(i).preview_url;

                    // Album art thumbnail (large (640px for Now Playing screen) and small
                    // (200px for list items)). If the image size does not exist in the API
                    // response, you are free to choose whatever size is available.)
                    imageList = albumInfo.images;

                    // Image List [0] 640x640, [1]300x300 [2] 64x64
                    albumArtThumbnail = getTrackImg(imageList);
//                //Add all data to ArtistRowItems
                    TopTracksRowItem item = new TopTracksRowItem(albumArtThumbnail, trackName,
                            albumName, previewUrl);
                    topTracksRowItems.add(item);

                    listView.setAdapter(mTopTracksAdapter);   // reset the listview to new adapter
                }
            } else {
                noTracksMsg();
            }
        }

        private String getTrackImg(List<Image> imageList) {
            for (int j = 0; j < imageList.size(); j++) {
                switch (j) {
                    case 0:
                        return imageList.get(j).url;
                    case 1:
                        return imageList.get(j).url;
                    case 2:
                        return imageList.get(j).url;
                }

            }
            return getString(R.string.default_album_img); // If no image return default image
        }
    }
}


