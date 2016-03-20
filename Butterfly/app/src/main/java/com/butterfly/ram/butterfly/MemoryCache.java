package com.butterfly.ram.butterfly;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by rostislawk on 20.3.16.
 */
public class MemoryCache implements BitmapCache {

    private LruCache<Integer, Bitmap> mMemoryCache;

    public MemoryCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public void setBitmap(Integer key, Bitmap bitmap) {
        if (getBitmap(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmap(Integer key) {
        return mMemoryCache.get(key);
    }

}
