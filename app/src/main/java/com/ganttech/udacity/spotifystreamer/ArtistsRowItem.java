package com.ganttech.udacity.spotifystreamer;

/**
 * Artist Row
 */
public class ArtistsRowItem {
    private String imageURL;
    private String artistName;
    private String spotifyID;

    public ArtistsRowItem(String imageURL, String artistName, String spotifyID) {
        this.imageURL = imageURL;
        this.artistName = artistName;
        this.spotifyID= spotifyID;
    }

    public String getImageURL() {return imageURL;}

    public void setImageURL(String imgURL) {
        this.imageURL = imgURL;
    }

    public String getArtistName() {return artistName;}

    public void setArtistName(String artistName) {

        this.artistName = artistName;
    }

    public String getSpotifyID() {return spotifyID;}

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    @Override
    public String toString() {
        return artistName +" " + spotifyID + " " + imageURL;
    }

}

