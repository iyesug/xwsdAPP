package com.xwsd.app.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.xwsd.app.R;
import com.xwsd.app.activity.GestureLockSettingsActivity;
import com.xwsd.app.activity.GestureLoginActivity;
import com.xwsd.app.activity.MainActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GesturePassward;


public class GuideFragment4 extends Fragment {

    private GuideActivity guideActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guide_4, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.guide_img_4_bt);
        imageView.setImageResource(R.mipmap.img_guide_4);
        imageView.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //从本地取出用户信息
                        Intent intent;
                        //判断本地是否有登录的用户
                        if (!TextUtils.isEmpty((String) ((GuideActivity) getActivity()).getParam(UserParam.USER_ID, ""))) {
                            //判断用户是否设置了手势密码
                            //ACache.get(getActivity()).getAsBinary((String) ((GuideActivity) getActivity()).getParam(UserParam.USER_ID, "")) == null
                            if (guideActivity.getParam(UserParam.USER_ID, "") == null &&"".equals(GesturePassward.getString((String) guideActivity.getParam(UserParam.USER_ID, ""), ""))) {
                                intent = new Intent(getActivity(), GestureLockSettingsActivity.class);
                                intent.putExtra("showBack", false);
                            } else {
                                //启用手势锁
                           /*     AppContext.setNeedLock(true);*/
                                intent = new Intent(getActivity(), GestureLoginActivity.class);
                            }
                        } else {
                            intent = new Intent(getActivity(), MainActivity.class);
                        }
                        startActivity(intent);
                        getActivity().finish();
//                        startActivity(new Intent(getActivity(),
//                                MainActivity.class));
//                        getActivity().finish();
                    }
                });

        return view;
    }

}
