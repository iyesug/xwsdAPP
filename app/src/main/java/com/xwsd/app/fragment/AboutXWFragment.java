package com.xwsd.app.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.WebDetailsActivity;
import com.xwsd.app.api.XWSDRequestAdresse;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.view.MADialog;
import com.xwsd.app.view.NavbarManage;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

/**
 * Created by Gx on 2016/8/29.
 * 关于小微
 */
public class AboutXWFragment extends BaseFragment implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.tv_versions)
    TextView tv_versions;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.activity_about_xw, null);
        return view;
    }

    @Override
    protected void init() {
        tv_versions.setText(getVersion());
    }





    @OnClick({R.id.ll_about_us, R.id.ll_help_center, R.id.ll_charging_standard, R.id.ll_update,R.id.call})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_about_us:
                intent = new Intent(this.getContext(), WebDetailsActivity.class);
                intent.putExtra(UserParam.TITLE, "关于我们");
                intent.putExtra(UserParam.URL, XWSDRequestAdresse.ABOUT_US);
                intent.putExtra(UserParam.TYPE, WebDetailsActivity.TYPE_NATIVE);
                startActivity(intent);
                break;
            case R.id.ll_help_center:
                intent = new Intent(this.getContext(), WebDetailsActivity.class);
                intent.putExtra(UserParam.TITLE, "帮助中心");
                intent.putExtra(UserParam.URL, XWSDRequestAdresse.QUESTION);
                intent.putExtra(UserParam.TYPE, WebDetailsActivity.TYPE_NETWORK);
                startActivity(intent);
                break;
            case R.id.ll_charging_standard:
                intent = new Intent(this.getContext(), WebDetailsActivity.class);
                intent.putExtra(UserParam.TITLE, "收费标准");
                intent.putExtra(UserParam.URL, XWSDRequestAdresse.FEES);
                intent.putExtra(UserParam.TYPE, WebDetailsActivity.TYPE_NETWORK);
                startActivity(intent);
                break;
            case R.id.ll_update:
                break;
            case R.id.call:
                // 先判断是否有权限。
                if(!AndPermission.hasPermission(getActivity(), Manifest.permission.CALL_PHONE)) {
                    // 有权限，直接do anything.

                    // 申请单个权限。
                    AndPermission.with(this)
                            .requestCode(100)
                            .permission(Manifest.permission.CALL_PHONE)
                            // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                            .rationale(new RationaleListener() {
                                @Override
                                public void showRequestPermissionRationale(int requestCode, Rationale rationale) {

                                    AndPermission.rationaleDialog(getActivity(), rationale).show();
                                }
                            })
                            .send();
                }
                final MADialog mMDialog = new MADialog(getContext());
                mMDialog.setMessage("确认拨打：400 8659 993");
                mMDialog.setBtnOK("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMDialog.miss();
                        Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4008659993"));
                        startActivity(intentPhone);
                    }
                });
                mMDialog.setBtnCancel("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMDialog.miss();
                    }
                });



        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = AppContext.context().getPackageManager();
            PackageInfo info = manager.getPackageInfo(AppContext.context().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }


}
