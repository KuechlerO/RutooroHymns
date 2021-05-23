package com.kalule.rutoorohymns;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    private LayoutInflater inflater;
    private List<SongName> songNamesList;
    private ArrayList<SongName> arrayList;

    protected ListViewAdapter(Context context, List<SongName> songNamesList) {
        this.songNamesList = songNamesList;
        inflater = LayoutInflater.from(context);
        this.arrayList = new ArrayList<SongName>();
        this.arrayList.addAll(songNamesList);
    }

    public class ViewHolder {
        TextView songNumber;
        TextView songName;
    }

    @Override
    public int getCount() {
        return songNamesList.size();
    }

    @Override
    public SongName getItem(int position) {
        return songNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);

            // Get textView from listview_item.xml
            holder.songNumber = (TextView) view.findViewById(R.id.numberLabel);
            holder.songName = (TextView) view.findViewById(R.id.titleLabel);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.songNumber.setText(songNamesList.get(position).getSongNumber());
        holder.songName.setText(songNamesList.get(position).getCombinedSongName());

        return view;
    }

    /**
     * Filters and collects the search results
     * @param charText  The search-text
     * @return  True if filtered listView is not empty
     */
    public boolean filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        songNamesList.clear();
        if (charText.length() == 0) {
            songNamesList.addAll(arrayList);
        } else {
            for (SongName songName : arrayList) {
                // check if query exists as original or rutooro title
                if (songName.getRutooroSongName().toLowerCase(Locale.getDefault()).contains(charText)
                || (songName.getOriginalSongName().toLowerCase(Locale.getDefault()).contains(charText))) {
                    songNamesList.add(songName);
                }
                // check number
                else if (songName.getSongNumber().contains(charText)) {
                    songNamesList.add(songName);
                }
            }
        }
        // update changes
        notifyDataSetChanged();

        // returns true if not empty
        return (!songNamesList.isEmpty());
    }
}
