package com.kalule.rutoorohymns;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchView extends AppCompatActivity implements android.widget.SearchView.OnQueryTextListener {
    // Declare Variables
    ListView listView;
    ListViewAdapter searchAdapter;
    android.widget.SearchView searchView;
    ArrayList<SongName> arraySongList = new ArrayList<SongName>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // styles have to be applied before inflating views -> to initialise attribute values
        getTheme().applyStyle(new Preferences(this).getFontStyle().getResId(), true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchview);


        // Locate the ListView in listview_main.xml
        listView = (ListView) findViewById(R.id.listview);
        arraySongList = FileReader.loadSongList(this);

        // Pass results to ListViewAdapter Class
        searchAdapter = new ListViewAdapter(this, arraySongList);

        // Binds the Adapter to the ListView
        listView.setAdapter(searchAdapter);

        // Locate the searchView in listview_main.xml
        searchView = (android.widget.SearchView) findViewById(R.id.search);
        searchView.setOnQueryTextListener(this);

        // Set an item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                SongName selectedSong = (SongName) parent.getItemAtPosition(position);
                String selectedSongNumber = selectedSong.getSongNumber();

                searchView.setQuery(selectedSongNumber, true);
            }
        });

        // Associate searchable configuration with the SearchActivity
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
    }


    /**
     *
     * @param query
     * @return True if no valid search input, otherwise False
     * -> False submits then to SearchView
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        // true -> handeled commit, false -> submit to SearchView
        return (!arraySongList.contains(new SongName(query, "", "")));
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // check if results are available
        if (searchAdapter.filter(newText)) {
            listView.setVisibility(View.VISIBLE);
        }
        else {
            // no results -> turn list invisible
            listView.setVisibility(View.INVISIBLE);
        }
        return false;
    }
}
