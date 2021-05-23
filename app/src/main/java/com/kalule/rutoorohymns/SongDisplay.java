package com.kalule.rutoorohymns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SongDisplay extends AppCompatActivity {
    private LinearLayout songPageLayout;
    private String songText = "Test";
    // get song content
    // ArrayList: 1. Number, Title; 2. Additional Info; 3. Verses and Chorus (each one in 1 string)
    private ArrayList<String []> completeSongContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // styles have to be applied before inflating views -> to initialise attribute values
        getTheme().applyStyle(new Preferences(this).getFontStyle().getResId(), true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_display);

        // get extras -> request for song-String
        Bundle extras = getIntent().getExtras();
        String songNumber = extras.getString(getString(R.string.key_songNumber));

        // set song content
        completeSongContent = FileReader.getSongContentArrayList(this, songNumber, true);
        setSongBody(songNumber);    // set song body
    }

    /**
     * Sets the view-content for the given song-number
     * @param songNumber
     */
    private void setSongBody(String songNumber) {
        songPageLayout = findViewById(R.id.songPageLayout);

        // height: wrap-content | width: wrap-content
        LinearLayout.LayoutParams infoContent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams paramsStanzaHead = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams paramsStanzaContent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int margin_left = getResources().getDimensionPixelSize(R.dimen.song_margin_left);
        int margin_right = getResources().getDimensionPixelSize(R.dimen.song_margin_right);
        int margin_bottom = getResources().getDimensionPixelSize(R.dimen.song_margin_bottom);
        int margin_top = getResources().getDimensionPixelSize(R.dimen.song_margin_top);

        // set margin
        infoContent.setMargins(margin_left, 0, margin_right, 0);
        paramsStanzaHead.setMargins(margin_left, margin_top, margin_right, 0);
        paramsStanzaContent.setMargins(margin_left, 0, margin_right, margin_bottom);

        // get song content
        //ArrayList<String []> completeSongContent = FileReader.getSongContentArrayList(this, songNumber, true);

        setTitle(completeSongContent.get(0)[0]);    // set number to bar

        // ------ songHeader ---------
        TextView songHeader = findViewById(R.id.songHeader);
        String numberAndTitle = completeSongContent.get(0)[0] + " - " + completeSongContent.get(0)[1];
        songHeader.setText(numberAndTitle);

        // ------ additional info ----------
        for (String infoString : completeSongContent.get(1)) {
            TextView textView = createTextView(infoString);
            textView.setTextColor(Color.YELLOW);
            textView.setTextColor(this.getResources().getColor(R.color.colorInfo));
            textView.setLayoutParams(infoContent);
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
            songPageLayout.addView(textView);
        }

        // --------- Stanza and chorus ---------
        for (String verse : completeSongContent.get(2)) {
            String[] splittedString = verse.split("\\R", 2);    // split at newlines

            TextView verseHead = createTextView(verse);
            TextView verseBody = createTextView(verse);

            verseHead.setText(splittedString[0]);   // set verse-nr / chorus
            verseBody.setText(splittedString[1]);   // set verse-content

            // Layouts
            verseHead.setLayoutParams(paramsStanzaHead);    // head-layout
            verseHead.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);


            verseBody.setLayoutParams(paramsStanzaContent); // body-layout
            if (splittedString[0].equals("Chorus")) {       // for chorus -> italic
                verseBody.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
            }

            songPageLayout.addView(verseHead);
            songPageLayout.addView(verseBody);
        }
    }


    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setMaxLines(10);

        // get dynamic text-size
        int [] textSizeAttr = new int[] {R.attr.font_medium};
        int indexOfAttrTextSize = 0;
        TypedValue typedValue = new TypedValue();
        TypedArray a = this.obtainStyledAttributes(typedValue.data, textSizeAttr);
        // returns scaled value or -1 if not set
        int textSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1); //-> 20sp -> 50 (pixel)
        a.recycle();
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        textView.setText(text);
        return textView;
    }


    /**
     * Create bar-menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_menu, menu);

        // get search view -> WARNING: find by ID returns null
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // load entries from index-file
        final ArrayList<SongName> arraySongList = FileReader.loadSongList(this);


        final ListView listView = (ListView) findViewById(R.id.listview);
        final ListViewAdapter searchAdapter = new ListViewAdapter(this, arraySongList);
        listView.setAdapter(searchAdapter);

        // add listener to searchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * When query-text changes -> manages result appearance
             * @param newText
             * @return
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                // empty search
                if (newText.isEmpty()) {
                   listView.setVisibility(View.INVISIBLE);
                }
                else {
                    // check if results are available
                    if (searchAdapter.filter(newText)) {
                        listView.setVisibility(View.VISIBLE);
                    }
                }
                return false;
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
            }
        );

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

        return true;
    }

    /**
     * Setting options for bar-menu
     * NOTICE: For app-bar-search already a listener is installed
     * @param item  The selected item
     * @return  Returns true if successfully run
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.zoomIn:
                changeFonstStyle(true);
                Toast.makeText(this, "Zoom in", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.ZoomOut:
                changeFonstStyle(false);
                Toast.makeText(this, "Zoom out", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                Intent settIntent = new Intent(this, SettingsActivity.class);
                startActivity(settIntent);
                return true;
            case R.id.shareText:
                String numberAndTitle = completeSongContent.get(0)[0] + " - "
                        + completeSongContent.get(0)[1];
                String songInfo = TextUtils.join("\n", this.completeSongContent.get(1));
                String songBody = TextUtils.join("\n\n", this.completeSongContent.get(2));
                String textToShare = "-*- Shared with SDA-RutooroHymns -*-\n\n" +
                        numberAndTitle + "\n" + songInfo + "\n\n" + songBody;


                Intent shareTextIntent = new Intent();
                shareTextIntent.setAction(Intent.ACTION_SEND);
                shareTextIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                shareTextIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(shareTextIntent, null);
                startActivity(shareIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This function increases/decreases to a new fontStyle and recreates the activity
     * @param increase  True -> increase | False -> decrease
     */
    private void changeFonstStyle(boolean increase) {
        Preferences preferences = new Preferences(this);
        FontStyle currentFontStyle = preferences.getFontStyle();

        FontStyle [] _fontstyles = {FontStyle.Small, FontStyle.Medium, FontStyle.Large, FontStyle.XLarge};
        int index = Arrays.asList(_fontstyles).indexOf(currentFontStyle);

        if (increase && (index < 3)) {
            preferences.setFontStyle(_fontstyles[index +1]);
        }
        else if (!increase && (index > 0)) {
            preferences.setFontStyle(_fontstyles[index -1]);
        }
        // recreate to make settings take effect immediately
        this.recreate();
    }



    @Override
    protected void onResume() {
        super.onResume();
        Preferences.updateSettings(this);
    }
}
