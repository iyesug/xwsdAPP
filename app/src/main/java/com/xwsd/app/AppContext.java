package com.xwsd.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.xwsd.app.bean.UserBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tbswebview.APIWebviewTBS;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.TLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 全局类，可在这里初始化一些重要数据
 * 可获取全局单例上下文
 * 封装了toast可方便调用
 */
public class AppContext extends Application {

    static Context context;

    /**
     * 是否需要开启手势锁
     */
    private static boolean needLock = false;


    /**
     * 当前登录的用户
     */
    private static UserBean currentUser;

    public static void setUserBean(UserBean userBean) {
        AppContext.currentUser = userBean;
        BuriedPointUtil.setObject(UserParam.USERBEAN, userBean);
    }

    public static UserBean getUserBean() {
//        if (null == currentUser || null == currentUser.data) {
            currentUser = BuriedPointUtil.getObject(UserParam.USERBEAN, UserBean.class);
//        }

Log.e("currentUsersp",BuriedPointUtil.getObject(UserParam.USERBEAN, UserBean.class).data.userName);

        return currentUser;
    }

    public static boolean isNeedLock() {
        return needLock;
    }

    public static void setNeedLock(boolean needLock) {
        TLog.error("Lock:" + needLock);
        AppContext.needLock = needLock;
    }

    APIWebviewTBS mAPIWebviewTBS;

    @Override
    public void onCreate() {
        super.onCreate();


        //sophix初始化
        SophixManager.getInstance().setContext(this)
                .setAppVersion(getVersionName())
                .setAesKey(null)
                .setEnableDebug(false)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            // SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
        SophixManager.getInstance().queryAndLoadNewPatch();

        context = getApplicationContext();

        //开启日志输出
        TLog.DEBUG = true;


        //bugly初始化
            /*第三个参数为SDK调试模式开关，调试模式的行为特性如下：
                    输出详细的Bugly SDK的Log；
            每一条Crash都会被立即上报；
            自定义日志将会在Logcat中输出。
            建议在测试阶段建议设置成true，发布时设置为false。*/
        CrashReport.initCrashReport(getApplicationContext(), "e625212e2d", false);

//        初始化网络请求框架
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .addInterceptor(new LoggerInterceptor("TAG"))
//                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
//                .readTimeout(10000L, TimeUnit.MILLISECONDS)
//                //其他配置
//                .build();
//        OkHttpUtils.initClient(okHttpClient);
//创建默认的ImageLoader配置参数
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
//                .createDefault(this);
//
//        //Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(configuration);

        ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

        HttpsUtils.SSLParams sslParams = null;
        try {
            sslParams = HttpsUtils.getSslSocketFactory(getAssets().open("xwsd.crt"), null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(cookieJar1)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);


        //WebviewTBS个人封装，针对升级----开始
        mAPIWebviewTBS = APIWebviewTBS.getAPIWebview();
        mAPIWebviewTBS.initTbs(getApplicationContext());
        //个人封装，针对升级----结束
    }


    public static synchronized AppContext context() {
        return (AppContext) context;
    }
    /**
     * 获取本地版本号
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pki = pm.getPackageInfo(getPackageName(), 0);
            return pki.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
