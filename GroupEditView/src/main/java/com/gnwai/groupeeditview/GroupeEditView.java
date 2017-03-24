package com.gnwai.groupeeditview;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * 用户登录，找回密码等界面的组合输入框
 */
public class GroupeEditView extends LinearLayout {
    /**
     * 图标
     */
    private ImageView group_edeit_image;

    /**
     * 输入框
     */
    private EditText group_edeit_edit;

    /**
     * 清除按钮
     */
    private ImageView group_edeit_clear;

    /**
     * 验证码按钮
     */
    private Button group_edeit_button;


    /**
     * 验证码倒计时内部类的实例
     */
    private TimeCount time;


    /**
     * 第一个构造方法
     *
     * @param context
     */
    public GroupeEditView(Context context) {
        super(context, null);
    }


    /**
     * 第二个构造方法
     *
     * @param context
     * @param attrs
     */
    public GroupeEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }


    /**
     * 第三个构造方法
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public GroupeEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }


    /**
     * 初始化布局
     *
     * @param context
     */

    private void initView(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_edit_view, this);
        group_edeit_image = (ImageView) findViewById(R.id.group_edeit_image);
        group_edeit_edit = (EditText) findViewById(R.id.group_edeit_edit);
        group_edeit_clear = (ImageView) findViewById(R.id.group_edeit_clear);
        group_edeit_button = (Button) findViewById(R.id.group_edeit_button);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LoginEditViewEdit);

            if (a.getDrawable(R.styleable.LoginEditViewEdit_imager) != null) {
                group_edeit_image.setImageDrawable(a.getDrawable(R.styleable.LoginEditViewEdit_imager));
            }
            if (!TextUtils.isEmpty(a.getString(R.styleable.LoginEditViewEdit_hint))) {
                group_edeit_edit.setHint(a.getString(R.styleable.LoginEditViewEdit_hint));
            }
            if (a.getInt(R.styleable.LoginEditViewEdit_editType, 0) != 0) {
                group_edeit_edit.setInputType(a.getInt(R.styleable.LoginEditViewEdit_editType, 0));
            }
            a.recycle();
            setClearButton();
            setFocusChange();
        }

        group_edeit_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onVerifyClickListen != null) {
                    if (onVerifyClickListen.onClick()) {
                        openTimeCount();
                    }
                }
            }
        });
    }

    /**
     * 设置内容
     */
    public void setTextStr(String str) {
        group_edeit_edit.setText(str);
    }

    /**
     * 得到输入框的内容
     */
    public String getEditTextInfo() {
        return group_edeit_edit.getText().toString().trim();
    }

    /**
     * 设置输入类型
     */
    public void setInputType(int type) {
        group_edeit_edit.setInputType(type);
    }

    /**
     * 设置清除按钮显示
     */
    private void showClear() {
        group_edeit_clear.setVisibility(View.VISIBLE);

        //设置点击事件，清除输入，并且影藏自己
        group_edeit_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearEdit();

            }
        });
    }

    /**
     * 设置清除按钮影藏
     */
    private void hideClear() {
        group_edeit_clear.setVisibility(View.GONE);
    }

    /**
     * 清除编辑框内容
     */
    private void ClearEdit() {
        group_edeit_edit.setText("");
        hideClear();
    }

    /**
     * 设置清除按钮
     */
    private void setClearButton() {

        //设置文本监听器
        group_edeit_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //如果输入字符大于0就显示清除按钮
                if (s.length() > 0 && group_edeit_button.getVisibility() != View.VISIBLE) {
                    showClear();
                } else {
                    hideClear();
                }
            }
        });
    }

    /**
     * 设置焦点监听
     */
    private void setFocusChange() {
        group_edeit_edit.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(group_edeit_edit.getText().toString())) {
                    if (hasFocus) {//获得焦点
                        showClear();
                    } else {//失去焦点
                        hideClear();
                    }
                }
            }
        });

    }


    /**
     * 显示验证码按钮
     */
    public void showAuthButton() {
        time = new TimeCount(60000, 1000);
        group_edeit_button.setVisibility(View.VISIBLE);
    }

    /**
     * 开启验证码按钮倒计时
     */
    private void openTimeCount() {
        time.start();
    }

    /**
     * 设置验证码按钮的背景颜色
     *
     * @param colourType 0 表示蓝色;1表示灰色
     */
    public void setByackdropColour(int colourType) {

        Resources res = getResources();
        switch (colourType) {
            case 0:
                Drawable text_blue = res.getDrawable(R.color.bg_blue_s);
                group_edeit_button.setBackgroundDrawable(text_blue);
                break;
            case 1:
                Drawable light_gray = res.getDrawable(R.color.tv_gray);
                group_edeit_button.setBackgroundDrawable(light_gray);
                break;
        }
    }

    /**
     * 定义一个倒计时的内部类
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            setByackdropColour(0);
            group_edeit_button.setText(getContext().getString(R.string.anew_get));
            group_edeit_button.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            setByackdropColour(1);
            group_edeit_button.setClickable(false);
            group_edeit_button.setText(millisUntilFinished / 1000 + getContext().getString(R.string.second));
        }
    }

    /**
     * 验证码按钮的监听器
     */
    public interface OnVerifyClickListen {
        /**
         * @return 是否开始计时
         */
        boolean onClick();
    }

    private OnVerifyClickListen onVerifyClickListen;

    public void setOnVerifyClickListen(OnVerifyClickListen onVerifyClickListen) {
        this.onVerifyClickListen = onVerifyClickListen;
    }
}
