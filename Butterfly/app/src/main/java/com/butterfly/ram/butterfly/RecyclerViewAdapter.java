package com.butterfly.ram.butterfly;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    private List<Image> mImages;

    public RecyclerViewAdapter(List<Image> images) {
        mImages = images;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View item_view = inflater.inflate(R.layout.thumbnail_cell, parent, false);
        ViewHolder viewHolder = new ViewHolder(item_view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Image image = mImages.get(position);

        TextView textView = holder.itemNumberLabel;
        textView.setText(String.valueOf(position + 1));

    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
