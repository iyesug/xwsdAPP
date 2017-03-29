package com.xwsd.app.guide;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.activity.GestureLockSettingsActivity;
import com.xwsd.app.activity.GestureLoginActivity;
import com.xwsd.app.activity.MainActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.service.UserService;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GesturePassward;

import java.util.ArrayList;
import java.util.List;

import static com.xwsd.app.api.ApiHttpClient.userSecret;

/**
 * 启动页面
 */
public class GuideActivity extends BaseActivity {
    public SharedPreferences sp;
    private ViewPager mVPActivity;
    private GuideFragment1 mFragment1;
    private GuideFragment2 mFragment2;
    private GuideFragment3 mFragment3;
    private GuideFragment4 mFragment4;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();
    private PagerAdapter mPgAdapter;
    private ImageView iv_splash;

    @Override
    protected void onBeforeSetContentLayout() {

        setContentView(R.layout.activity_guide);

        BuriedPointUtil.getInstance();//在启动页实例化埋点单例，以后在整个程序运行周期内不会重复创建，提高效率

    }

    @Override
    protected void init(Bundle savedInstanceState) {
        sp = getApplicationContext().getSharedPreferences("UserParam", Activity.MODE_PRIVATE);
        userSecret = (String) getParam(UserParam.USER_SECRET, "");
        if (!TextUtils.isEmpty((String) getParam(UserParam.USER_ID, ""))) {
            //启动服务获取用户信息
            startService(new Intent(GuideActivity.this, UserService.class).putExtra(UserParam.USER_ID, (String) getParam(UserParam.USER_ID, "")));
        }

        iv_splash = (ImageView) findViewById(R.id.iv_splash);
        iv_splash.setBackgroundResource(R.mipmap.img_splash);
//        判断当前版本号是否和储存的相同
        try {
            if (sp.getString("versions", "").equals(getVersionName())) {//显示启动图
                splash();
            } else {//加载引导页
                initView();
                //将版本储存到本地
                sp.edit().putString("versions", getVersionName()).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            splash();
        }
    }

    Handler handler = new Handler();

    /**
     * 进入首页
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            //从本地取出用户信息
            Intent intent;
            //判断本地是否有登录的用户
            if (!TextUtils.isEmpty((String) getParam(UserParam.USER_ID, ""))) {
//                //判断用户是否设置了手势密码
//                if (ACache.get(GuideActivity.this).getAsBinary((String) getParam(UserParam.USER_ID, "")) == null) {
//                    //关闭登录页面，和自己，防止跳出重复页面
//                    AppManager.getAppManager().finishActivity(UserActivity.class);
//                    AppManager.getAppManager().finishActivity(GestureLockSettingsActivity.class);
//                    intent = new Intent(GuideActivity.this, GestureLockSettingsActivity.class);
//                    intent.putExtra("showBack", false);
//                }
                if ("".equals(GesturePassward.getString((String)getParam(UserParam.USER_ID, ""),""))) {
                    //关闭登录页面，和自己，防止跳出重复页面
                    AppManager.getAppManager().finishActivity(UserActivity.class);
                    AppManager.getAppManager().finishActivity(GestureLockSettingsActivity.class);
                    intent = new Intent(GuideActivity.this, GestureLockSettingsActivity.class);
                    intent.putExtra("showBack", false);
                } else {
                    //关闭登录页面，和自己，防止跳出重复页面
                    AppManager.getAppManager().finishActivity(UserActivity.class);
                    AppManager.getAppManager().finishActivity(GestureLoginActivity.class);
                    //启用手势锁
/*
                    AppContext.setNeedLock(true);
*/
                    intent = new Intent(GuideActivity.this, GestureLoginActivity.class);
                }
            } else {
                intent = new Intent(GuideActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }
    };

    /**
     * 启动页
     */
    private void splash() {
        /**
         * 延迟3秒
         */
        handler.postDelayed(runnable, 3000);
        iv_splash.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化引导页
     */

    private void initView() {
        mVPActivity = (ViewPager) findViewById(R.id.vp_activity);
        mFragment1 = new GuideFragment1();
        mFragment2 = new GuideFragment2();
        mFragment3 = new GuideFragment3();
        mFragment4 = new GuideFragment4();
        mListFragment.add(mFragment1);
        mListFragment.add(mFragment2);
        mListFragment.add(mFragment3);
        mListFragment.add(mFragment4);
        mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mListFragment);
        mVPActivity.setAdapter(mPgAdapter);
    }

    /**
     * 屏蔽返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取当前应用的版本号
     *
     * @return
     * @throws Exception
     */
    private String getVersionName() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = getApplicationContext()
                .getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(
                getApplicationContext().getPackageName(), 0);
        String version = String.valueOf(packInfo.versionCode);
        return version;
    }

}
