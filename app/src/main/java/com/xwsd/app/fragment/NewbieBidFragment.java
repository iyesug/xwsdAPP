package com.xwsd.app.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.xwsd.app.R;
import com.xwsd.app.activity.ProjectDetailsActivity;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.constant.UserParam;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Gx on 2016/9/6.
 * 首页-新手标
 */
public class NewbieBidFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.arc_progress)
    ArcProgress arc_progress;

    @Bind(R.id.tv_oddYearRate)
    TextView tv_oddYearRate;

    @Bind(R.id.tv_oddPeriod)
    TextView tv_oddPeriod;

    int progress = 0;

    //    Odds newHandOdds;
    int position;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            arc_progress.setProgress(msg.arg1);
            handler.postDelayed(updateProgress, 50);
        }
    };

    Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            progress += 10;

            if (progress >= HomeFragment.indexBean.data.newHandOdds.get(position).schedule) {
                arc_progress.setProgress(HomeFragment.indexBean.data.newHandOdds.get(position).schedule);
                handler.removeCallbacks(updateProgress);
            } else {
                Message msg = handler.obtainMessage();
                msg.arg1 = progress;
                handler.sendMessage(msg);
            }
        }
    };

    @Override
    public void onVisible() {
        super.onVisible();
        handler.removeCallbacks(updateProgress);
        progress = 0;
        handler.post(updateProgress);
    }

    @Override
    public void onInvisible() {
        super.onInvisible();
        if (arc_progress != null) {
            arc_progress.setProgress(0);
        }
    }

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_newbie_bid, null);
    }

    @Override
    protected void init() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        position = getArguments().getInt(UserParam.POSITION);
//        arc_progress.setProgress(newHandOdds.schedule);
        tv_oddYearRate.setText(decimalFormat.format(HomeFragment.indexBean.data.newHandOdds.get(position).oddYearRate * 100) + "%");
        tv_oddPeriod.setText("50元起投 " + HomeFragment.indexBean.data.newHandOdds.get(position).oddPeriod + "到期");
    }

    @OnClick(R.id.arc_progress)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.arc_progress) {
            Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
            intent.putExtra("oddNumber", HomeFragment.indexBean.data.newHandOdds.get(position).oddNumber);
            startActivity(intent);
        }
    }
}
