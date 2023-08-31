package com.kalule.rutoorohymns;

import org.junit.Test;
import java.io.File;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;


public class TestSongFiles {
    private final String rawDirectoryPath = "/Users/oliverkuchler/AndroidStudioProjects/RutooroHymns/app/src/main/res/raw";
    private final int number_of_songs = 300;


    /**
     * Checks whether the song-number in the song-file is set correctly
     */
    @Test
    public void header_isCorrect() {
        for (int i = 0; i <= number_of_songs; i++) {
            File dir = new File(rawDirectoryPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String fileName = child.getName();

                    if (fileName.contains("rutooro_")) {
                        String number = fileName.replace("rutooro_", "");
                        number = number.replace(".txt", "");

                        try {
                            BufferedReader reader = new BufferedReader(new java.io.FileReader(child));
                            String firstLine = reader.readLine().replace(" ", "");
                            assertEquals(number, firstLine);
                            reader.close();
                        }
                        catch (Exception e) {
                            System.out.println("Something went wrong.");
                            e.printStackTrace();
                        }

                    }
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        }
    }


    /**
     * Checks if keywords "Original", "Composer", "Key/Doh", "Luganda" are listed only
     * at the beginning of a song file.
     */
    @Test
    public void check_SongStructure() {
        for (int i = 0; i <= number_of_songs; i++) {
            File dir = new File(rawDirectoryPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String fileName = child.getName();

                    if (fileName.contains("rutooro_")) {
                        String number = fileName.replace("rutooro_", "");
                        number = number.replace(".txt", "");

                        try {
                            BufferedReader reader = new BufferedReader(new java.io.FileReader(child));
                            boolean passed_information = false;
                            String line;
                            String [] informations = new String [] {"Original", "Composer",
                                    "Key/Doh", "Luganda"};
                            reader.readLine();              // skip first line
                            reader.readLine();              // skip second line
                            while ((line = reader.readLine()) != null) {
                                line = line.replace(" ", "");
                                if (line.matches("1\\.")) {  // any digit
                                    passed_information = true;
                                    continue;
                                }
                                else {
                                    if (!passed_information) {
                                        assertTrue("Missing prefix {\"Original\", " +
                                                        "\"Composer\",\n" +
                                                        " \"Key/Doh\", \"Luganda\"} . Number: " +
                                                        number + ", found: " + line,
                                                checkIfLineContains(informations, line));
                                    }
                                    else {
                                        assertFalse("Number: " + number + ", found: " + line,
                                                checkIfLineContains(informations, line));
                                    }
                                }
                            }
                            reader.close();
                        }
                        catch (Exception e) {
                            System.out.println("Something went wrong.");
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        }
    }


    /**
     * Checks the correct placement of whitespaces in the files
     */
    @Test
    public void check_Whitespaces() {
        for (int i = 0; i <= number_of_songs; i++) {
            File dir = new File(rawDirectoryPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String fileName = child.getName();

                    if (fileName.contains("rutooro_")) {
                        String number = fileName.replace("rutooro_", "");
                        number = number.replace(".txt", "");

                        try {
                            BufferedReader reader = new BufferedReader(new java.io.FileReader(child));
                            String line;

                            while ((line = reader.readLine()) != null) {
                                if (line.contains(",")) {
                                    assertTrue("Number: " + number + ", found: " + line,
                                            line.contains(", ") || line.endsWith(","));
                                }
                                if (line.contains(";")) {
                                    assertTrue("Number: " + number + ", found: " + line,
                                            line.contains("; ") || line.endsWith(";"));
                                }
                                if (line.contains(":")) {
                                    assertTrue("Number: " + number + ", found: " + line,
                                            line.contains(": ") || line.endsWith(":"));
                                }
                                if (line.contains("!")) {
                                    assertTrue("Number: " + number + ", found: " + line,
                                            line.contains("! ") || line.endsWith("!"));
                                }
                                if (line.contains("?")) {
                                    assertTrue("Number: " + number + ", found: " + line,
                                            line.contains("? ") || line.endsWith("?"));
                                }

                                assertFalse(line.contains("Chorus:"));
                                assertFalse(line.startsWith(":"));
                            }
                            reader.close();
                        }
                        catch (Exception e) {
                            System.out.println("Something went wrong.");
                            e.printStackTrace();
                        }

                    }
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        }
    }


    /**
     * Checks the correct appearance of numbers in the songs. E.g. A1l (1 instead of l) is not
     * allowed
     */
    @Test
    public void check_numbers_in_songs() {
        for (int i = 0; i <= number_of_songs; i++) {
            File dir = new File(rawDirectoryPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    String fileName = child.getName();

                    if (fileName.contains("rutooro_")) {
                        String number = fileName.replace("rutooro_", "");
                        number = number.replace(".txt", "");

                        try {
                            BufferedReader reader = new BufferedReader(new java.io.FileReader(child));
                            String line;

                            while ((line = reader.readLine()) != null) {
                                assertTrue("Number: " + number + ", found: " + line,
                                        line.matches("^\\d+\\.? *$") // Only numbers in line
                                                || line.matches("^\\D+$") // Only non-numbers in line
                                                || line.matches("^Luganda: \\d+$") // Luganda number
                                                || line.matches("^ *$") // empty lines
                                );
                            }
                            reader.close();
                        } catch (Exception e) {
                            System.out.println("Something went wrong.");
                            e.printStackTrace();
                        }

                    }
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        }
    }

    /**
     * Checks whether all entries in the song index list are unique
     */
    @Test
    public void check_duplicates_song_index() {
        File song_index_file = new File(rawDirectoryPath + "/songs_index.txt");

        try {
            Scanner s = new Scanner(song_index_file);
            ArrayList<String> song_list = new ArrayList<String>();
            while (s.hasNextLine()){
                song_list.add(s.nextLine());
            }
            s.close();

            for (int i=0; i<song_list.size(); ++i) {
                for (int j=i+1; j<song_list.size(); ++j) {
                    String [] x_song = song_list.get(i).split(";");
                    String [] y_song = song_list.get(j).split(";");

                    assertTrue("Line " + i + " has less then 2 entries (number + " +
                                    "title at least needed", x_song.length >= 2);
                    for (String song_info_x: x_song) {
                        for (String song_info_y : y_song) {
                            assertFalse("Lines " + (i + 1) + " and " + (j + 1) + " have an value " +
                                    "overlap: " + song_info_x, song_info_x.equals(song_info_y));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong.");
            e.printStackTrace();
        }


    }


    /**
     * Helper function to check if single element of items is contained in line
     * @param items Array of String elements, where at least one shall be included in line
     * @param line A given string
     * @return Boolean: True if line contains one item of items, else False
     */
    private boolean checkIfLineContains(String[] items, String line) {
        for (String item: items) {
            if (line.contains(item)) {
                return true;
            }
        }
        return false;
    }
}
