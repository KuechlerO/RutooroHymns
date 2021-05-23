package com.kalule.rutoorohymns;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FileReaderTest {
    @Test
    /**
     * Test runs through all songs and checks if the app is able to read everything
     */
    public void testAllFileReadInputs() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kalule.rutoorohymns", appContext.getPackageName());

        for (int i = 1; i <= 225; ++i) {
            System.out.println("i: " + i);
            ArrayList<String []> songContentList =
                    FileReader.getSongContentArrayList(appContext, Integer.toString(i), true);
            assertTrue("Error for song-number: : " + i,
                    (songContentList.size() == 3));
        }
    }
}