package com.ganttech.udacity.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistSearchFragment extends Fragment {
    private Toast mAppToast;
    private final String LOG_TAG = ArtistSearchFragment.class.getSimpleName();
    List<ArtistsRowItem> artistsRowItems;
    public ArtistsAdapter mArtistsAdapter;
    public String artistSearchName;
    ListView listView;

    public ArtistSearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get the Artist from editText and hide keyboard
        EditText editText = (EditText) rootView.findViewById(R.id.search_artist_editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // get artist & replace spaces for Spotify API
                    artistSearchName = v.getText().toString();
                    searchArtist(artistSearchName.replaceAll(" ", "+"));

                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    handled = true;
                }
                return handled;
            }
        });

        // Create a new array of Artist Row items
        artistsRowItems = new ArrayList<ArtistsRowItem>();

        // Get List view of artists
        listView = (ListView) rootView.findViewById(R.id.listview_artist);
        // Set up adapter
        mArtistsAdapter = new ArtistsAdapter(getActivity(), artistsRowItems);
        listView.setAdapter(mArtistsAdapter);   // // Add mArtistsAdapter to listView;

        // Set up on Click to pass artist to Top Tracks
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(parent.getContext(), TopTracksActivity.class);
                Log.v(LOG_TAG,"before" );

                // Create arraylist to hold Name for now, get ID later
                String[] artistNameId = {
                        artistsRowItems.get(position).getArtistName(), // add Name
                        artistsRowItems.get(position).getSpotifyID()  // add spotify Id
                };
                intent.setAction(Intent.ACTION_SEND);                   // send multiple

                // Get the spotify ID from rowItem to send to TopTracks
                intent.putExtra ("artistInfo", artistNameId);
                //Log.v(LOG_TAG, "ArrayList:" + artistNameId.toString() +"/"+artistNameId);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void searchArtist(String name) {
        FetchArtistTask artist = new FetchArtistTask();
        artist.execute(name);
    }

    public void noArtistssMsg() {

        if (mAppToast != null) {  // if current Toast present, cancel for better User Experience
            mAppToast.cancel();
        }
        //String data = parent.getItemAtPosition(position).toString();
        String data = artistSearchName + " " + getString(R.string.toast_no_artists);
        mAppToast = Toast.makeText(getActivity(), // get current activity
                data,                               // String to pass info
                Toast.LENGTH_LONG);                 // Length
        mAppToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);    // Move to center
        mAppToast.show();
    }

    @Override
    public void onStart() {
        // Setup artists list of people named Kelly so not a blank screen
        EditText editText = (EditText) getActivity().findViewById(R.id.search_artist_editText);
        String artistSearchedFor = editText.getText().toString();

        // Setup artists list of people named Kelly so not a blank screen
        if (artistSearchedFor.isEmpty()){
            searchArtist(getString(R.string.default_search_artist_name));
        }
        // Setup artists list to user searched artist
        else{
            searchArtist(artistSearchedFor);
        }

        super.onStart();
    }


    /* Class to Fetch Artist using Spotify API Adapter
    * Does this using an AsyncTask (worker thread)
    * ASync work done in doInBackground, result back on UI Thread onPostExecute  */

    public class FetchArtistTask extends AsyncTask<String, Void, List<Artist>> {

        private final String LOG_TAG = FetchArtistTask.class.getSimpleName();
        private String[] spotifyResults;

        @Override
        protected List<Artist> doInBackground(String... params) {
         /* For task 1, you will need to request artist data via the Search for an Item web endpoint.
        *  Be sure to restrict the search to artists only by including the item type=”artist”.
        *  For each artist result you should extract the following data:
        *  artist name
        *  SpotifyId* - This is required by the Get an Artist’s Top Tracks query which will use
        *  afterwards. artist thumbnail image */
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            List<Artist> artistsList;

            // Setup type=artist using Map
            // For list of supported parameters see https://developer.spotify.com/web-api/search-item/
            Map queryParams = new HashMap<String, String>();
            queryParams.put("type", "artist");

            // Get Artists results, put into a list
            artistsList = spotify.searchArtists(params[0], queryParams).artists.items;

            return artistsList;

        }

        @Override
        protected void onPostExecute(List<Artist> artistsList) {

            if (!artistsList.isEmpty()) {
                List<Image> imageList;
                String name, id, artistImg = null;

                // Clear current tracks
                mArtistsAdapter.rowItems.clear();

                // Get nqme, Spotfy ID, image
                for (int i = 0; i < artistsList.size(); i++) {
                    name = artistsList.get(i).name;
                    id = artistsList.get(i).id;

                    // Get Image results, for more info
                    // https://developer.spotify.com/web-api/object-model/#image-object
                    imageList = artistsList.get(i).images;
                    artistImg = getArtistImg(imageList);


                    //Add all data to ArtistRowItems
                    ArtistsRowItem item = new ArtistsRowItem(artistImg, name, id);
                    artistsRowItems.add(item);

                }
//                Log.v(LOG_TAG, "count(" + mArtistsAdapter.getCount() + ")");
//                mArtistsAdapter.rowItems.addAll(artistsRowItems);
//                Log.v(LOG_TAG, "After count(" + mArtistsAdapter.getCount() + ")");

                listView.setAdapter(mArtistsAdapter);   // reset the listview to new adapter
            }
            else
                noArtistssMsg();

        }
    }

    private String getArtistImg(List<Image> imageList) {
        // Image List [0] Large, [1]Medium [2] Small
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

