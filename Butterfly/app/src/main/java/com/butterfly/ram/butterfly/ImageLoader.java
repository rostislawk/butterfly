package com.butterfly.ram.butterfly;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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
    private MemoryCache mMemoryCache;

    public ImageLoader(Context context) {
        mContext = context;
        mMemoryCache = new MemoryCache();
        mPlaceholder = BitmapUtils.decodeSampleBitmapFromResource(mContext.getResources(),
                R.mipmap.placeholder, 360, 360);

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
            Bitmap cachedBitmap = mMemoryCache.getBitmap(data);
            if (cachedBitmap == null) {
                cachedBitmap = BitmapUtils.decodeSampleBitmapFromResource(mContext.getResources(), data, 360, 360);
                mMemoryCache.setBitmap(data, cachedBitmap);
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
                    Drawable[] layers = new Drawable[2];
                    layers[0] = new BitmapDrawable(getPlaceholder());
                    layers[1] = new BitmapDrawable(bitmap);
                    TransitionDrawable transitionDrawable = new TransitionDrawable(layers);
                    imageView.setImageDrawable(transitionDrawable);
                    transitionDrawable.startTransition(300);
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
