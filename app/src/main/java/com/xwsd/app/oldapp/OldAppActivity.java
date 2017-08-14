package com.xwsd.app.oldapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;

public class OldAppActivity extends BaseActivity {

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_old_app);
    }

    public FragmentManager fragmentManager;
    @Override
    protected void init(Bundle savedInstanceState) {
        fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_content, new OldAccountFragment())
                .commit();
    }

}
