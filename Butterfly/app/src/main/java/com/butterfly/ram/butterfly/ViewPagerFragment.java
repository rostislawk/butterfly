package com.butterfly.ram.butterfly;

import android.os.Bundle;
import android.sax.TextElementListener;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by rostislawk on 12.3.16.
 */
public class ViewPagerFragment extends Fragment {

    public String title;

    public ViewPagerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_pager_fragment, container, false);
        TextView textView = (TextView) view.findViewById(R.id.view_pager_fragment_title);
        textView.setText(title);

        return view;
    }
}
