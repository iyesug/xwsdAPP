package com.xwsd.app.guide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xwsd.app.R;


public class GuideFragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guide_2, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.guide_img_2);
        imageView.setImageResource(R.mipmap.img_guide_2);
        return view;
    }

}
