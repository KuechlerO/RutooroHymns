package com.kalule.rutoorohymns;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;

public class FileReader {
    /**
     * Loads the song-index-entries
     * @param context
     * @return  ArrayList containing all songs as SongName-Objects
     */
    protected static ArrayList<SongName> loadSongList(Context context) {
        // song-Index
        String filename = context.getString(R.string.song_file_index);

        int fileId = context.getResources().getIdentifier(filename, "raw", context.getPackageName());
        InputStream inputStream = context.getResources().openRawResource(fileId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // add entries as SongName-objects
        ArrayList<SongName> songNameList = new ArrayList<SongName>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String [] seperatedString = line.split(";");

                if (seperatedString.length > 1) {           // check length
                    if (seperatedString.length == 3) {      // all data is given
                        songNameList.add(new SongName(seperatedString[0], seperatedString[1], seperatedString[2]));
                    }
                    else {                                  // only rutooro title is given
                        // add as SongName-object
                        songNameList.add(new SongName(seperatedString[0], seperatedString[1], ""));
                    }
                }
            }
            reader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return songNameList;
    }


    /**
     * Returns an String which holds the text of the text-file representing the chosenNumber
     * @param context
     * @param chosenNumber  The chosen song-number
     * @param rutooro   Boolean -> If true, then get Rutooro text, else English text
     * @return  The string (each line seperated by %)
     */
    protected static String getSongText(Context context, String chosenNumber, boolean rutooro) {
        String fileName;
        if (rutooro) {
            fileName = context.getString(R.string.song_file_rutooro_pre) + chosenNumber;
        }
        else {
            fileName = context.getString(R.string.song_file_english_pre) + chosenNumber;
        }

        int fileId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());

        InputStream inputStream = context.getResources().openRawResource(fileId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String complete_text = "";
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                // every line is seperated by %
                if (complete_text.isEmpty()) {
                    complete_text = line;
                }
                else {
                    complete_text = complete_text + "%" + line;
                }
            }
            reader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return complete_text;
    }


    /**
     * Gets the songs content in an ordered manner
     * @param context
     * @param chosenNumber  The chosen song-number
     * @param rutooro   Boolean -> If true, then get Rutooro text, else English text
     * @return  ArrayList: 1. Number, Title; 2. Additional Info; 3. Verses and Chorus (each one in 1 string)
     */
    protected static ArrayList<String []> getSongContentArrayList(Context context,
                                                                  String chosenNumber, boolean rutooro) {
        String completeText = getSongText(context, chosenNumber, rutooro);
        String [] completeSongSplitted = completeText.split("%");

        String [] numberAndTitle = {completeSongSplitted[0], completeSongSplitted[1]};
        // String [] originalComposerKeyLuganda = {"", "", "", ""};

        ArrayList<String> originalComposerKeyLuganda = new ArrayList<String>();

        ArrayList<String> versesList = new ArrayList<String>();
        String previousStrings = "";

        // iterate over remaining lines
        boolean completedInfo = false;
        for (int i = 2; i < completeSongSplitted.length; i++) {
            char firstChar = completeSongSplitted[i].charAt(0);
            // info
            if (!completedInfo) {
                if (Character.isDigit(firstChar)) {     // check if digit -> first verse is reached
                    completedInfo = true;
                }
                else {
                    originalComposerKeyLuganda.add(completeSongSplitted[i]);  // add info
                }
            }

            // stanzas & chorus
            if (completedInfo) {
                if (Character.isDigit(firstChar) ||
                        completeSongSplitted[i].equals("Chorus")) {     // reached new stanza/chorus
                    if (!previousStrings.equals("")) {
                        versesList.add(previousStrings);    // add previously seen stanza
                    }
                    previousStrings = completeSongSplitted[i];  // new stanza
                }
                else {
                    previousStrings = previousStrings + "\n" + completeSongSplitted[i];    // continue stanza
                }
            }
        }
        versesList.add(previousStrings);    // add last verse

        // transfer ListArrays to normal arrays
        String [] infoContentArray = transferListToArray(originalComposerKeyLuganda);   // array of info
        String [] versesAndChorusArray = transferListToArray(versesList);    // array of verse strings


        // create final Array-List
        ArrayList<String []> songContents = new ArrayList<String []>();
        songContents.add(numberAndTitle);
        songContents.add(infoContentArray);
        songContents.add(versesAndChorusArray);

        return songContents;
    }


    /**
     * Transfers arrayLists to normal string arrays
     * @param arrayList
     * @return
     */
    private static String[] transferListToArray(ArrayList<String> arrayList) {
        String [] array = new String[arrayList.size()];
        for (int i=0; i<arrayList.size(); ++i) {
            array[i] = arrayList.get(i);
        }
        return array;
    }
}
