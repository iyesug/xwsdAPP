package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import butterknife.Bind;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternIndicator;
import com.star.lockpattern.widget.LockPatternView;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.tools.GesturePassward;
import com.xwsd.app.view.NavbarManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gx on 2016/8/30.
 * 手势锁-设置
 */
public class GestureLockSettingsActivity extends BaseActivity {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.lockPatterIndicator)
    LockPatternIndicator lockPatternIndicator;

    @Bind(R.id.lockPatternView)
    LockPatternView lockPatternView;

    @Bind(R.id.messageTv)
    TextView messageTv;

    private List<LockPatternView.Cell> mChosenPattern = null;

//    private ACache aCache;

    private static final long DELAYTIME = 600L;

    private boolean canBack;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_gesture_lock_settings);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.gesture_lock_settings));
        navbarManage.showRight(false);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        canBack = getIntent().getBooleanExtra("showBack", true);
        navbarManage.showLeft(canBack);
//        aCache = ACache.get(GestureLockSettingsActivity.this);
        lockPatternView.setOnPatternListener(patternListener);
    }

    @Override
    public void onBackPressed() {
        if (canBack) {
            super.onBackPressed();
        }
    }

    /**
     * 手势监听
     */
    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
            lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (mChosenPattern == null && pattern.size() >= 4) {
                mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
                updateStatus(Status.CORRECT, pattern);
            } else if (mChosenPattern == null && pattern.size() < 4) {
                updateStatus(Status.LESSERROR, pattern);
            } else if (mChosenPattern != null) {
                if (mChosenPattern.equals(pattern)) {
                    updateStatus(Status.CONFIRMCORRECT, pattern);
                } else {
                    updateStatus(Status.CONFIRMERROR, pattern);
                }
            }
        }
    };

    /**
     * 更新状态
     *
     * @param status
     * @param pattern
     */
    private void updateStatus(Status status, List<LockPatternView.Cell> pattern) {
        messageTv.setTextColor(getResources().getColor(status.colorId));
        messageTv.setText(status.strId);
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CORRECT:
                updateLockPatternIndicator();
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case LESSERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CONFIRMERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CONFIRMCORRECT:
                saveChosenPattern(pattern);
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                setLockPatternSuccess();
                break;
        }
    }

    /**
     * 更新 Indicator
     */
    private void updateLockPatternIndicator() {
        if (mChosenPattern == null)
            return;
        lockPatternIndicator.setIndicator(mChosenPattern);
    }

    /**
     * 重新设置手势
     */
    private void resetLockPattern() {
        mChosenPattern = null;
        lockPatternIndicator.setDefaultIndicator();
        updateStatus(Status.DEFAULT, null);
        lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
    }

    /**
     * 成功设置了手势密码
     */
    private void setLockPatternSuccess() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
           /*         AppContext.setNeedLock(true);*/
                    if (AppManager.getActivity(MainActivity.class) == null) {
                        Intent intent = new Intent(GestureLockSettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 保存手势密码
     */
    private void saveChosenPattern(List<LockPatternView.Cell> cells) {
        byte[] bytes = LockPatternUtil.patternToHash(cells);

        //注意字节数组和字符串之间的直接转换不行，要转换成Base64编码。
        String gesturePassWard = Base64.encodeToString(bytes,Base64.DEFAULT);

        GesturePassward.putString(AppContext.getUserBean().data.userId,gesturePassWard);

//        aCache.put(AppContext.getUserBean().data.userId, bytes);
    }

    private enum Status {
        //默认的状态，刚开始的时候（初始化状态）
        DEFAULT(R.string.create_gesture_default, R.color.grey_a5a5a5),
        //第一次记录成功
        CORRECT(R.string.create_gesture_correct, R.color.grey_a5a5a5),
        //连接的点数小于4（二次确认的时候就不再提示连接的点数小于4，而是提示确认错误）
        LESSERROR(R.string.create_gesture_less_error, R.color.red_f4333c),
        //二次确认错误
        CONFIRMERROR(R.string.create_gesture_confirm_error, R.color.red_f4333c),
        //二次确认正确
        CONFIRMCORRECT(R.string.create_gesture_confirm_correct, R.color.grey_a5a5a5);

        Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;

    }
}
