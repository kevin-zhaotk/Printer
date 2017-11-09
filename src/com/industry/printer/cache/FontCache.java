package com.industry.printer.cache;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Customized Font library loading cache for the purpose of avoiding memory leak
 * Bacause Typeface will allocate new memory block everytime when it create
 * a new instance of Typeface by invoking Typeface.createFromAsset method, this is a
 * known bug from google;
 * We improve this issue by managing the Typefaces using this FontCache Class;
 * Created by kevin on 2017/11/7.
 */

public class FontCache {

    public static final HashMap FONT_CACHE = new HashMap<String, Typeface>();

    public static Typeface get(Context ctx, String font) {

        synchronized (FONT_CACHE) {
            Typeface tf = (Typeface) FONT_CACHE.get(font);
            if (tf == null) {
                tf = Typeface.createFromAsset(ctx.getAssets(), font);
                FONT_CACHE.put(font, tf);
            }
            return tf;
        }
    }
}
