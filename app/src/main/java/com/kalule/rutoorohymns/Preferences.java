package com.kalule.rutoorohymns;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

public class Preferences {
    private final Context context;


    public Preferences(Context context) {
        this.context = context;
    }

    protected SharedPreferences open() {
        // open default preference file
        return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
        // open "prefs" - file
        //return context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    protected SharedPreferences.Editor edit() {
        return open().edit();
    }

    public FontStyle getFontStyle() {
        // 2nd argument as dummy
        return FontStyle.valueOf(open().getString(context.getString(R.string.pref_key_fontStyle),
                FontStyle.Medium.name()));
    }

    // This function is used for the Zoom-Shortcuts (on Song-View)
    public void setFontStyle(FontStyle style) {
        edit().putString(context.getString(R.string.pref_key_fontStyle), style.name()).commit();
    }


    /**
     * Updates the font-style and -size and also sets the background
     * @param activity  The activity which is to address
     */
    protected static void updateSettings(Activity activity) {
        // style application
        activity.getTheme().applyStyle(new Preferences(activity).getFontStyle().getResId(), true);
        System.out.println(new Preferences(activity).getFontStyle().toString());

        SharedPreferences spDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(activity);

        View root = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);

        // get saved color-string (2nd arg: Dummy if preference does not exist)
        String colorString = spDefaultPrefs.getString(activity.getString(R.string.pref_key_bgColor),
                "#FFFFFF");
        root.setBackgroundColor(Color.parseColor(colorString));
    }
}
