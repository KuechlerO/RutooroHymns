package com.kalule.rutoorohymns;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// for rating-dialog needed
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.content.DialogInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static final String EMPTY_INPUT = "Empty";
    static final String ZERO_INPUT = "Zero";
    static final String FILE_NOT_EXISTING = "NotExisting";
    static final String VALID_INPUT = "ValidInput";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // styles have to be applied before inflating views -> to initialise attribute values
        getTheme().applyStyle(new Preferences(this).getFontStyle().getResId(), true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGoForSong = findViewById(R.id.goForSong);
        btnGoForSong.setOnClickListener(this);
    }

    /**
     * Listener for Go-Button
     * @param view
     */
    public void onClick(View view) {
        Intent secAct = new Intent(this, SongDisplay.class);
        TextView textField = findViewById(R.id.enterNumber);

        String numberString = textField.getText().toString();       // get input
        numberString = numberString.replaceFirst("^0+(?!$)", "");   // delete leading 0s

        String alertDialog;

        switch (checkChosenNumber(numberString)) {
            case EMPTY_INPUT:
                alertDialog = "Please insert a song number!";
                break;
            case ZERO_INPUT:
                alertDialog = "Input must be greater zero!";
                break;
            case FILE_NOT_EXISTING:
                alertDialog = "Your song-number is not yet registered. The maximum number for " +
                        "this version is 225.";
                break;
            default:
                alertDialog = "";
        }

        if (!alertDialog.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(alertDialog);
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        // pass information to next activity (name, value)
        secAct.putExtra(getString(R.string.key_songNumber), numberString);
        startActivity(secAct);
    }


    /**
     * Create the main-menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Setting options for the main-menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent searchIntent = new Intent (this, SearchView.class);
                startActivity(searchIntent);
                return true;
            case R.id.settings:
                Intent settIntent = new Intent(this, SettingsActivity.class);
                startActivity(settIntent);
                return true;
            case R.id.about:
                //Toast.makeText(this, "About (not yet implemented)", Toast.LENGTH_SHORT).show();
                showAbout();
                return true;
            case R.id.thanks:
                showThanks();
                return true;
            case R.id.rating:
                showRating();
                return true;
            case R.id.shareApp:
                shareApp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Preferences.updateSettings(this);
    }

    /**
     * Checks the input-number -> Number valid? && File exists?
     * @param numberString
     * @return  boolean
     */
    private String checkChosenNumber(String numberString) {
        if (numberString.isEmpty()) {
            return EMPTY_INPUT;
        }
        else {
            int chosenNumber = Integer.parseInt(numberString);
            if (chosenNumber == 0) {
                return ZERO_INPUT;
            }
            else {
                String filepath = getString(R.string.song_file_rutooro_pre) + numberString;
                int checkExistence = getResources().getIdentifier(filepath, "raw", getPackageName());

                // check if file exists
                if(checkExistence == 0) {
                    return FILE_NOT_EXISTING;
                }
                else {
                    return VALID_INPUT;
                }
            }
        }
    }

    /**
     * Presents the About-Dialog
     */
    private void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

        //builder.setIcon(R.drawable.music_icon1);
        builder.setTitle("About " + getString(R.string.app_name));
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    /**
     * Presents the Thanks-Dialog
     */
    private void showThanks() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.thanks, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

        builder.setTitle("Thanks");
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    /**
     * Presents the Rating-Dialog
     */
    private void showRating() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.rating, null, false);


        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Rate our app!");
        builder.setView(messageView);
        //builder.setMessage("Make us happy and rate our app on the Google-Playstore!");

        // add the buttons
        builder.setNegativeButton("Later...", null);
        builder.setPositiveButton("Rate it now!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {        // do something like...
                rateApp();
            }
        });

        final AlertDialog ratingDialog = builder.create();
        ratingDialog.show();
    }



    /*
     * Opens new intent for rating the app
     * -> checks also if playstore app is installed -> if not then browser is used
     * */
    private void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");   // for playstore app
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e) {           // no playstore app installed
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        // create actual url leading to app
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url,
                getPackageName())));
        // set flags for intent
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;    // get back to app if "return" is chosen
        }
        intent.addFlags(flags);
        return intent;
    }


    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SDA - RutooroHymns");

            String shareMessage= "\nLet me recommend you the app \"SDA - Rutooro Hymns\"!\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Choose one"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
