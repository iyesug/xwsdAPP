package com.xwsd.app.guide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xwsd.app.R;


public class GuideFragment1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guide_1, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.guide_img_1);
        imageView.setBackgroundResource(R.mipmap.img_guide_1);
        return view;
    }

}
