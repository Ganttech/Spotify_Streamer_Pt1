package com.ganttech.udacity.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Code based on tutorial from theopentutorials.com
 * http://theopentutorials.com/post/uncategorized/android-custom-listview-with-image-and-text-using-baseadapter/
 * this Android 4 example, we will create custom ListView where each row item consists of one
 * ImageView and two TextView (one for displaying image title and other for displaying image
 * description) and populate its items using custom BaseAdapter.
 */
public class TopTracksAdapter extends BaseAdapter {
    Context context;
    List<TopTracksRowItem> rowItems;

    public TopTracksAdapter(Context context, List<TopTracksRowItem> items) {
        this.context = context;
        this.rowItems = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView trackName;
        TextView albumName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_top_tracks, null);
            holder = new ViewHolder();
            holder.albumName = (TextView) convertView.findViewById(
                    R.id.list_item_top_tracks_album_textview);
            holder.trackName = (TextView) convertView.findViewById(
                    R.id.list_item_top_tracks_song_textview);
            holder.imageView = (ImageView) convertView.findViewById(
                    R.id.list_item_top_tracks_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TopTracksRowItem rowItem = (TopTracksRowItem) getItem(position);

        holder.albumName.setText(rowItem.getAlbumName());
        holder.trackName.setText(rowItem.getTrackName());

        // Load image using Picasso
        // Picasso.with(context).load(url).into(view);
        com.squareup.picasso.Picasso.with(context).load(rowItem.gettrackImg()).into(holder.imageView);
        //holder.imageView.setImageResource(rowItem.gettrackImg());

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

}
