package com.xwsd.app.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.xwsd.app.R;
import com.xwsd.app.view.MADialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.util.Calendar;
import java.util.List;

/**
 * Created by iyesug on 2017/6/5.
 */

public class ContactUtil {
    static boolean isCheck;
    /**
     * 判断微信是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    System.out.println("已安装qq");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isRightTime(Context context) {
        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
        final int start = 9 * 60;// 起始时间 9:00的分钟数
        final int end = 21 * 60;// 结束时间 21:00的分钟数
        if (minuteOfDay >= start && minuteOfDay <= end) {
            System.out.println("在范围内");
            return true;
        } else {
            System.out.println("在范围外");
            return false;
        }
    }
    static Context mContext;
    public static void callQq(Context context) {
        mContext=context;
        if(!ContactUtil.isQQClientAvailable(context)){
            final MADialog mMDialog = new MADialog(context);
            mMDialog.setMessage("未安装QQ，请安装QQ后进行交流");
            mMDialog.setBtnOK("确定", v1 -> {
                mMDialog.miss();
            });
        }
        else if(!ContactUtil.isRightTime(context)){
            final MADialog mMDialog = new MADialog(context);
            mMDialog.setMessage("客服不在线，请留言！我们会第一时间联系您！");
            mMDialog.setBtnOK("确定", v1 -> {
                mMDialog.miss();
                String url="mqqwpa://im/chat?chat_type=crm&uin=938009686&version=1&src_type=web&web_src=http:://wpa.b.qq.com";
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            });
            mMDialog.setBtnCancelGone();
        }
        else{
            String url="mqqwpa://im/chat?chat_type=crm&uin=938009686&version=1&src_type=web&web_src=http:://wpa.b.qq.com";


            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public static void checkPermission(Activity context) {
        // 先判断是否有权限。
        if (AndPermission.hasPermission(context, Manifest.permission.CALL_PHONE)) {
            // 有权限，直接do anything.

            call(context);
            isCheck=false;
        } else if (!AndPermission.hasPermission(context, Manifest.permission.CALL_PHONE)) {
            // 申请单个权限。
//            if(isCheck){
                System.out.println("申请电话权限");
                AndPermission.with(context)
                        .requestCode(100)
                        .permission(Manifest.permission.CALL_PHONE)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale((requestCode, rationale) ->
                                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                AndPermission.rationaleDialog(context, rationale).show()
                        )
                        .send();
                isCheck=true;
//                checkPermission(context);
//            }

        }
    }


    //----------------------------------权限----------------------------------//


    @PermissionYes(100)
    private void getMultiYes(List<String> grantedPermissions) {
        Toast.makeText(mContext,R.string.message_post_success, Toast.LENGTH_SHORT).show();
    }

    @PermissionNo(100)
    private void getMultiNo(List<String> deniedPermissions) {
        Toast.makeText(mContext, R.string.message_post_failed, Toast.LENGTH_SHORT).show();

        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
//        if (AndPermission.hasAlwaysDeniedPermission(mContext, deniedPermissions)) {
//            AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
//                    .setTitle(R.string.title_dialog)
//                    .setMessage(R.string.message_permission_failed)
//                    .setPositiveButton(R.string.btn_dialog_yes_permission)
//                    .setNegativeButton(R.string.btn_dialog_no_permission, null)
//                    .show();

            // 更多自定dialog，请看上面。
//        }
    }


    private static void call(Activity context) {
        final MADialog mMDialog = new MADialog(context);
        mMDialog.setMessage("确认拨打：400 186 9996 ");//400-186-9996
        mMDialog.setBtnOK("确定", v1 -> {
            mMDialog.miss();
            Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4001869996 "));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(intentPhone);;
        });
        mMDialog.setBtnCancel("取消", v12 -> mMDialog.miss());
    }
}
