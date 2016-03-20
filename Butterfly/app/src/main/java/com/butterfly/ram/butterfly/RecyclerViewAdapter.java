package com.butterfly.ram.butterfly;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rostislawk on 13.3.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemNumberLabel;
        public SquareImageView thumbnailView;

        public ViewHolder(View view) {
            super(view);

            itemNumberLabel = (TextView) view.findViewById(R.id.image_number);
            thumbnailView = (SquareImageView) view.findViewById(R.id.thumbnail_view);
        }
    }

    private List<Integer> mImagesIDs;
    private Context mContext;
    private ImageLoader mImageLoader;

    public RecyclerViewAdapter(Context context) {
        mContext = context;
        mImageLoader = new ImageLoader(context);
        mImagesIDs = new ArrayList<Integer>();
        mImagesIDs.add(R.mipmap.pic1);
        mImagesIDs.add(R.mipmap.pic2);
        mImagesIDs.add(R.mipmap.pic3);
        mImagesIDs.add(R.mipmap.pic4);
        mImagesIDs.add(R.mipmap.pic5);
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View item_view = inflater.inflate(R.layout.thumbnail_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(item_view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView = holder.itemNumberLabel;
        textView.setText(String.valueOf(position + 1));

        SquareImageView imageView = holder.thumbnailView;
        int resID = mImagesIDs.get(position % mImagesIDs.size());
        mImageLoader.loadBitmap(resID, imageView);
    }

    @Override
    public int getItemCount() {
        return 100;
    }

}
