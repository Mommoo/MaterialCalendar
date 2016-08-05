package com.mommoo.materialpicker.toolkit;

/**
 * Created by mommoo on 2016-07-24.
 */
public class Color {
    public static int darker(int color){
        float[] hsv = new float[3];
        android.graphics.Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return android.graphics.Color.HSVToColor(hsv);
    }
    public static int lighter(int color){
        float[] hsv = new float[3];
        android.graphics.Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]); // value component
        return android.graphics.Color.HSVToColor(hsv);
    }
    public static int lighten(int color, double fraction) {
        int red = android.graphics.Color.red(color);
        int green = android.graphics.Color.green(color);
        int blue = android.graphics.Color.blue(color);
        red = lightenColor(red, fraction);
        green = lightenColor(green, fraction);
        blue = lightenColor(blue, fraction);
        int alpha = android.graphics.Color.alpha(color);
        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    public static int darken(int color, double fraction) {
        int red = android.graphics.Color.red(color);
        int green = android.graphics.Color.green(color);
        int blue = android.graphics.Color.blue(color);
        red = darkenColor(red, fraction);
        green = darkenColor(green, fraction);
        blue = darkenColor(blue, fraction);
        int alpha = android.graphics.Color.alpha(color);

        return android.graphics.Color.argb(alpha, red, green, blue);
    }

    private static int darkenColor(int color, double fraction) {
        return (int)Math.max(color - (color * fraction), 0);
    }

    private static int lightenColor(int color, double fraction) {
        return (int) Math.min(color + (color * fraction), 255);
    }
}
