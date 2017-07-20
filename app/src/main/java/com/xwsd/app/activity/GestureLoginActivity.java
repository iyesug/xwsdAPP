package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternView;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GesturePassward;

import java.util.List;


/**
 * Created by Gx on 2016/8/30.
 * 手势登录
 */
public class GestureLoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GestureLoginActivity";

    @Bind(R.id.lockPatternView)
    LockPatternView lockPatternView;

    @Bind(R.id.messageTv)
    TextView messageTv;

//    private ACache aCache;
    private static final long DELAYTIME = 600l;
//    private byte[] gesturePassword;
    private String gesturePassword;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_gesture_login);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title="手势登录";
//        aCache = ACache.get(GestureLoginActivity.this);
        //得到当前用户的手势密码
//        gesturePassword = aCache.getAsBinary((String) getParam(UserParam.USER_ID, ""));
        gesturePassword = GesturePassward.getString((String)getParam(UserParam.USER_ID, ""),"");
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (pattern != null) {
                if (LockPatternUtil.checkPattern(pattern, Base64.decode(gesturePassword,Base64.DEFAULT))) {
                    updateStatus(Status.CORRECT);
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

    /**
     * 更新状态
     *
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
        if (AppManager.getActivity(MainActivity.class) == null) {
            Intent intent = new Intent(GestureLoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    /**
     * 屏蔽掉返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick({R.id.forgetGestureBtn, R.id.switchAccount})
    @Override
    public void onClick(View v) {
        AppManager.getAppManager().finishActivity(UserActivity.class);
        Intent intent = new Intent(GestureLoginActivity.this, UserActivity.class);
        intent.putExtra(UserParam.SETTING_GESTURE, true);
        switch (v.getId()) {
            case R.id.forgetGestureBtn:
                startActivity(intent);
                break;

            case R.id.switchAccount:
                startActivity(intent);
                break;
        }
    }

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }
}
