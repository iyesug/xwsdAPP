package com.xwsd.app.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.gnwai.iosdialog.ActionSheetDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.tools.FileUtil;
import com.xwsd.app.tools.ImgUtil;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.WheelView;
import com.xwsd.wheelselect.ChangeAddressDialog;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

/**
 * Created by Gx on 2016/8/25.
 * 个人资料
 */
public class UserInfoActiviy extends BaseActivity implements View.OnClickListener {

    protected static final int CHOOSE_PICTURE = 0;

    protected static final int TAKE_PICTURE = 1;

    private static final int CROP_SMALL_PICTURE = 2;

    protected static Uri tempUri;

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.user_nickname)
    LinearLayout user_nickname;

    @Bind(R.id.user_name)
    LinearLayout user_name;

    @Bind(R.id.user_identity)
    LinearLayout user_identity;

    @Bind(R.id.user_mobile)
    LinearLayout user_mobile;

    @Bind(R.id.user_address)
    LinearLayout user_address;

    @Bind(R.id.user_sex)
    LinearLayout user_sex;

    @Bind(R.id.user_marriage)
    LinearLayout user_marriage;

    @Bind(R.id.user_portrait_img)
    ImageView user_portrait_img;

    RequestCall call;
    RequestCall updateHeadCall;

    String sex;

    String city;

    String marriage;

    Bitmap userHead; //用户头像位图

    String headFileName;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_user_data);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.user_data);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.user_data));
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

//        初始化标题
        ((TextView) user_nickname.findViewById(R.id.text_title)).setText(getString(R.string.nickname));
        ((TextView) user_name.findViewById(R.id.text_title)).setText(getString(R.string.reality_name));
        ((TextView) user_identity.findViewById(R.id.text_title)).setText(getString(R.string.identity));
        ((TextView) user_mobile.findViewById(R.id.text_title)).setText(getString(R.string.mobile));
        ((TextView) user_address.findViewById(R.id.text_title)).setText(getString(R.string.address));
        ((TextView) user_sex.findViewById(R.id.text_title)).setText(getString(R.string.sex));
        ((TextView) user_marriage.findViewById(R.id.text_title)).setText(getString(R.string.marriage));

        setData();
    }

    /**
     * 设置数据
     */
    private void setData() {

        ApiHttpClient.lodCircleImg(user_portrait_img,
                AppContext.getUserBean().data.userimg,
                R.drawable.ic_load, R.drawable.ic_load);

        ((TextView) user_nickname.findViewById(R.id.text_content)).setText(AppContext.getUserBean().data.userName);

        if (!TextUtils.isEmpty(AppContext.getUserBean().data.name)) {
            ((TextView) user_name.findViewById(R.id.text_content))
                    .setText(AppContext.getUserBean().data.name.replace(AppContext.getUserBean().data.name.substring(0, 1), "*"));
        }else {
            ((TextView) user_name.findViewById(R.id.text_content))
                    .setText("");
        }

        if (!TextUtils.isEmpty(AppContext.getUserBean().data.cardnum)) {
            if(AppContext.getUserBean().data.cardnum.length()<=16){
                ((TextView) user_identity.findViewById(R.id.text_content))
                        .setText(AppContext.getUserBean().data.cardnum.replace(AppContext.getUserBean().data.cardnum.substring(3, AppContext.getUserBean().data.cardnum.length()-3), "**********"));
            }else{
                ((TextView) user_identity.findViewById(R.id.text_content))
                        .setText(AppContext.getUserBean().data.cardnum.replace(AppContext.getUserBean().data.cardnum.substring(3, 13), "**********"));

            }
        }else {
            ((TextView) user_identity.findViewById(R.id.text_content))
                    .setText("");
        }

        if (!TextUtils.isEmpty(AppContext.getUserBean().data.phone)) {
            ((TextView) user_mobile.findViewById(R.id.text_content))
                    .setText(AppContext.getUserBean().data.phone.replace(AppContext.getUserBean().data.phone.substring(5, 9), "****"));
        }else {
            ((TextView) user_mobile.findViewById(R.id.text_content))
                    .setText("");
        }

        city = AppContext.getUserBean().data.city;
        if (!TextUtils.isEmpty(city)) {
            ((TextView) user_address.findViewById(R.id.text_content)).setText(AppContext.getUserBean().data.city);
        } else {
            ((TextView) user_address.findViewById(R.id.text_content)).setText("选择地址");
        }

        sex = AppContext.getUserBean().data.sex;
        if (sex.equals("man")) {
            ((TextView) user_sex.findViewById(R.id.text_content)).setText("男");
        } else {
            ((TextView) user_sex.findViewById(R.id.text_content)).setText("女");
        }

        marriage = AppContext.getUserBean().data.maritalstatus;
        if (marriage.equals(ApiHttpClient.YES)) {
            ((TextView) user_marriage.findViewById(R.id.text_content)).setText("已婚");
        } else {
            ((TextView) user_marriage.findViewById(R.id.text_content)).setText("未婚");
        }
    }

    @OnClick({R.id.commit, R.id.user_portrait, R.id.user_address, R.id.user_sex, R.id.user_marriage})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_portrait:

                //判断是否有权限
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    new ActionSheetDialog(UserInfoActiviy.this)
                            .builder()
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .addSheetItem(getString(R.string.photograph), ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        //拍照点击事件
                                        @Override
                                        public void onClick(int which) {
                                            if (Environment.getExternalStorageDirectory() == null) {
                                                ToastUtil.showToastShort("未找到存储设备！");
                                                return;
                                            }

                                            //这里关闭锁，防止跳转到相机再回来后需要解锁
                                            AppContext.setNeedLock(false);

                                            Intent openCameraIntent = new Intent(
                                                    MediaStore.ACTION_IMAGE_CAPTURE);
                                            tempUri = Uri.fromFile(new File(Environment
                                                    .getExternalStorageDirectory(), "photo.jpg"));
                                            // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                                            startActivityForResult(openCameraIntent, TAKE_PICTURE);
                                        }
                                    })
                            .addSheetItem(getString(R.string.photo), ActionSheetDialog.SheetItemColor.Blue,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                        @Override
                                        public void onClick(int which) {

                                            //这里关闭锁，防止跳转到相册再回来后需要解锁
                                            AppContext.setNeedLock(false);

                                            Intent openAlbumIntent = new Intent(
                                                    Intent.ACTION_GET_CONTENT);
                                            openAlbumIntent.setType("image/*");
                                            startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);

                                        }
                                    }).show();

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }
                break;
            case R.id.user_address:
                ChangeAddressDialog mChangeAddressDialog = new ChangeAddressDialog(UserInfoActiviy.this);
                mChangeAddressDialog.setAddress("福建", "福州", "鼓楼区");
                mChangeAddressDialog.show();
                mChangeAddressDialog
                        .setAddresskListener(new ChangeAddressDialog.OnAddressCListener() {
                            @Override
                            public void onClick(String province, String city, String area) {
                                ((TextView) user_address.findViewById(R.id.text_content))
                                        .setText(province + " " + city + " " + area);
                                UserInfoActiviy.this.city = province + " " + city + " " + area;
                            }
                        });
                break;
            case R.id.user_sex:
                setWheelDialog(0, "请选择性别", new String[]{"男", "女"});
                break;
            case R.id.user_marriage:
                setWheelDialog(1, "请选择婚姻状态", new String[]{"未婚", "已婚"});
                break;
            case R.id.commit:
                if (TextUtils.isEmpty(city)) {
                    ToastUtil.showToastShort(R.string.address_null);
                    return;
                }

                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });

                call = ApiHttpClient.setUserInfo(AppContext.getUserBean().data.userId, sex, marriage, city, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (userHead != null) {
                            //不管用户信息是否请求成功都要执行更新头像请求
                            updateHead();
                        } else {
                            hideWaitDialog();
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("修改用户信息：" + response);
//                        hideWaitDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
//                            ToastUtil.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {
                                AppContext.getUserBean().data.sex = sex;
                                AppContext.getUserBean().data.maritalstatus = marriage;
                                AppContext.getUserBean().data.city = city;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
                        if (userHead != null) {
                            updateHead();
                        }else {
                            hideWaitDialog();
                            //这里只更新用户信息
                            ToastUtil.showToastShort(getString(R.string.update_success));
                        }
                    }
                });

                break;
        }
    }

    //更新后台头像
    public void updateHead() {
        File file = new File(FileUtil.PATH_TEMP + headFileName);
        updateHeadCall = ApiHttpClient.setUserHead(AppContext.getUserBean().data.userId, file, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
                ToastUtil.showToastShort(getString(R.string.network_exception));
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ToastUtil.showToastShort(jsonObject.getString("msg"));
                    if (jsonObject.getInt("status") == 1) {
                        ToastUtil.showToast(getString(R.string.update_success));

                        //获取data转换成json对象
                        String data = jsonObject.getString("data");
                        JSONObject dataObject = new JSONObject(data);

                        //把新的头像url传给userimg，这样不用退出app更新
                        AppContext.getUserBean().data.userimg = dataObject.getString("photo");
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }

    MaterialDialog materialDialog;

    /**
     * 设置滚动选择器
     */
    private void setWheelDialog(final int type, String title, String[] parameter) {

        View outerView = LayoutInflater.from(UserInfoActiviy.this).inflate(R.layout.view_wheel, null);
        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setSeletion(0);
        wv.setItems(Arrays.asList(parameter));

        materialDialog = new MaterialDialog(UserInfoActiviy.this);
        materialDialog.setTitle(title);
        materialDialog.setContentView(outerView);
        materialDialog.setCanceledOnTouchOutside(true);
        materialDialog.setPositiveButton(R.string.confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                switch (type) {
                    case 0:
                        if (wv.getSeletedItem().equals("男")) {
                            sex = "man";
                        } else {
                            sex = "women";
                        }
                        ((TextView) user_sex.findViewById(R.id.text_content)).setText(wv.getSeletedItem());
                        break;
                    case 1:
                        if (wv.getSeletedItem().equals("已婚")) {
                            marriage = ApiHttpClient.YES;
                        } else {
                            marriage = ApiHttpClient.NO;
                        }
                        ((TextView) user_marriage.findViewById(R.id.text_content)).setText(wv.getSeletedItem());
                        break;
                }
            }
        });
        materialDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            Bitmap headImg = ImgUtil.makeRoundCorner(photo);

                            //随机生成图片名
                            headFileName = String.valueOf(System.currentTimeMillis()) + "head.jpg";

                            //把剪裁后的图片储存到临时文件夹
                            FileUtil.saveBitmap(photo, FileUtil.PATH_TEMP, headFileName);


                            userHead = photo;

                            //把裁剪的Bitmap图返回到头像的ImageView处
                            user_portrait_img.setImageBitmap(headImg);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            TLog.error("The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
}
