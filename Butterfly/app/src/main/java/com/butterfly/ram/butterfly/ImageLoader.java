package com.butterfly.ram.butterfly;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by rostislawk on 20.3.16.
 */
public class ImageLoader {

    private Context mContext;
    private Bitmap mPlaceholder;
    private LruCache<Integer, Bitmap> mMemoryCache;

    public ImageLoader(Context context) {
        mContext = context;
        mPlaceholder = BitmapUtils.decodeSampleBitmapFromResource(mContext.getResources(),
                R.mipmap.placeholder, 200, 200);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public void loadBitmap(int resId, ImageView imageView) {
        if (cancelPotentialWork(resId, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(mContext.getResources(), getPlaceholder(), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }

    private Bitmap getPlaceholder() {
        return mPlaceholder;
    }

    private void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemoryCache(Integer key) {
        return mMemoryCache.get(key);
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitMapData = bitmapWorkerTask.data;
            if (bitMapData == 0 || bitMapData != data) {
                bitmapWorkerTask.cancel(true);
            }
            else {
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            Bitmap cachedBitmap = getBitmapFromMemoryCache(data);
            if (cachedBitmap == null) {
                cachedBitmap = BitmapUtils.decodeSampleBitmapFromResource(mContext.getResources(), data, 200, 200);
                addBitmapToMemoryCache(data, cachedBitmap);
            }
            return cachedBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}
