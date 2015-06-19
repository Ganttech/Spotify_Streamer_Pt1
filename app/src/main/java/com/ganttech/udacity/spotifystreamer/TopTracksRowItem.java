package com.ganttech.udacity.spotifystreamer;

/**
 * Created by JMan13 on 6/18/15.
 */
public class TopTracksRowItem {
    private String trackImg;
    private String trackName;
    private String albumName;
    private String previewUrl;

    public TopTracksRowItem(String trackImg, String trackName, String albumName, String previewUrl) {
        this.trackImg = trackImg;
        this.trackName = trackName;
        this.albumName = albumName;
        this.previewUrl = previewUrl;
    }

    public String gettrackImg() {
        return trackImg;
    }

    public void settrackImg(String trackImg) {
        this.trackImg = trackImg;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    @Override
    public String toString() {
        return trackName + "\n" + albumName;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}

