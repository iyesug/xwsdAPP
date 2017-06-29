package com.xwsd.app.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.BankCardBean;
import com.zhy.http.okhttp.request.RequestCall;

import java.text.DecimalFormat;

/**
 * Created by Gy on 2017/6/27.
 * 支付宝支付
 */
public class AliPaymentFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.pic)
    ImageView pic;
//    @Bind(R.id.ll_bank_card)
//    LinearLayout ll_bank_card;
//
//    @Bind(R.id.tv_bank_name)
//    TextView tv_bank_name;
//    @Bind(R.id.tv_bank_num)
//    TextView tv_bank_num;
//    @Bind(R.id.tv_money)
//    TextView tv_money;
//    @Bind(R.id.tv_list)
//    TextView tv_list;
//
//    @Bind(R.id.error_layout)
//    EmptyLayout mErrorLayout;

    public static boolean needRefresh = false;
    BankCardBean bankCardBean;
    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    RequestCall call;


    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_ali_payment, null);
        return view;
    }

    @Override
    protected void init() {
        //设置图片
        loadIntoUseFitWidth(getActivity(), R.mipmap.alipay, R.mipmap.alipay, pic);


    }

    @Override
    public void onClick(View v) {

    }


//
//    @OnClick({ll_add_bank_card, tv_list, R.id.commit})
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case ll_add_bank_card://增加银行卡
//
//
//                break;
//            case tv_list://银行列表
//
//
//                break;
//            case R.id.commit://提交
//
//
//                break;
//
//        }
//
//    }

    /**
     * 自适应宽度加载图片。保持图片的长宽比例不变，通过修改imageView的高度来完全显示图片。
     */
    public static void loadIntoUseFitWidth(Context context, int imageUrl, int errorImageId, final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (imageView == null) {
                            return false;
                        }
                        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
                        float scale = (float) vw / (float) resource.getIntrinsicWidth();
                        int vh = Math.round(resource.getIntrinsicHeight() * scale);
                        params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                        imageView.setLayoutParams(params);
                        return false;
                    }
                })
                .placeholder(errorImageId)
                .error(errorImageId)
                .into(imageView);
    }
}