package com.butterfly.ram.butterfly;

import android.graphics.Bitmap;

/**
 * Created by rostislawk on 21.3.16.
 */
public interface BitmapCache {

    public void setBitmap(Integer key, Bitmap bitmap);
    public Bitmap getBitmap(Integer key);

}
