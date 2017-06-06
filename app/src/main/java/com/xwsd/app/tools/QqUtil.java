package com.xwsd.app.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.xwsd.app.view.MADialog;

import java.util.Calendar;
import java.util.List;

/**
 * Created by iyesug on 2017/6/5.
 */

public class QqUtil {
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

    public static void callQq(Context context) {
        if(!QqUtil.isQQClientAvailable(context)){
            final MADialog mMDialog = new MADialog(context);
            mMDialog.setMessage("未安装QQ，请安装QQ后进行交流");
            mMDialog.setBtnOK("确定", v1 -> {
                mMDialog.miss();
            });
        }
        else if(!QqUtil.isRightTime(context)){
            final MADialog mMDialog = new MADialog(context);
            mMDialog.setMessage("客服不在线，请留言！我们会第一时间联系您！");
            mMDialog.setBtnOK("确定", v1 -> {
                mMDialog.miss();
                String url="mqqwpa://im/chat?chat_type=crm&uin=4008659993&version=1&src_type=web&web_src=http:://wpa.b.qq.com";
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            });
            mMDialog.setBtnGone();
        }
        else{
            String url="mqqwpa://im/chat?chat_type=crm&uin=4008659993&version=1&src_type=web&web_src=http:://wpa.b.qq.com";
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

}
