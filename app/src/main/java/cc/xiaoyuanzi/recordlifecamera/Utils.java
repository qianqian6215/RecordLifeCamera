package cc.xiaoyuanzi.recordlifecamera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

/**
 * Created by kotoshishang on 16/8/7.
 */
public class Utils {

    private static final String MAIN_PREFERENCE = "main_preference";
    private static final String KEY_FREQUENCY = "frequency";
    private static final String KEY_USE_FONT_CAMERA = "use_font_camera";
    private static final int DEFAULT_FREQUENCY = 5;

    public static int getFrequency(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(KEY_FREQUENCY, DEFAULT_FREQUENCY);
    }

    public static void setFrequency(Context context, int value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit().
                putInt(KEY_FREQUENCY, value);
        editor.apply();
    }

    public static Boolean getUseFontCamera(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(KEY_USE_FONT_CAMERA, false);
    }

    public static void setUseFontCamera(Context context, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit().
                putBoolean(KEY_USE_FONT_CAMERA, value);
        editor.apply();
    }
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.
                    getSharedPreferences(MAIN_PREFERENCE, Activity.MODE_PRIVATE);
    }
}
