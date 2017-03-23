package com.xwsd.app.base;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xwsd.app.R;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.view.EmptyLayout;

/**
 * Created by Gx on 2016/9/3.
 * 基础列表Fragment
 */
public abstract class BasePullUpListFragment extends BaseFragment implements AbsListView.OnScrollListener {

    public static final int TYPE_PULLUP = 1;

    public static final int TYPE_FIRST = 0;

    /**
     * 加载
     */
    public static final int LOAD = 0;

    /**
     * 加载成功
     */
    public static final int SUCCEED = 1;

    /**
     * 加载错误
     */
    public static final int LOAD_ERROR = 2;

    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = 3;

    /**
     * 没有更多
     */
    public static final int NO_MORE = 4;


    protected ListView list_view;

    protected QuickAdapter mAdapter;

    protected EmptyLayout mErrorLayout;

    /**
     * 底部显示正在加载的页面
     */
    private View footerView = null;

    /**
     * 加载的页面-进度条
     */
    private ProgressBar progressbar;

    /**
     * 加载的页面-提示
     */
    private TextView text;

    /**
     * 当前页数
     */
    protected int currentPages = 1;

    /**
     * 总条目数
     */
    protected int allItemCount = 1;

    /**
     * 每页条目数
     */
    protected int each_page_num = 10;

    /**
     * 是否正在加载
     */
    private boolean isLaod = false;

    private int padding;

    /**
     * 上拉加载布局延迟消失时间
     */
    private long DELYED = 2000;

    @Override
    protected void init() {
        initErrorLayout();
        initBottomView();
        list_view.setOnScrollListener(this);
    }

    /**
     * 初始化空布局
     */
    private void initErrorLayout() {
        if (mErrorLayout == null) {
            mErrorLayout = (EmptyLayout) rootView.findViewById(R.id.error_layout);
        }

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                firstRequestData();
            }
        });
    }


    /**
     * 初始化底部页面
     */
    public void initBottomView() {
        if (footerView == null) {
            footerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_cell_footer, null);
            progressbar = (ProgressBar) footerView.findViewById(R.id.progressbar);
            text = (TextView) footerView.findViewById(R.id.text);
        }

        if (list_view == null) {
            list_view = (ListView) rootView.findViewById(R.id.list_view);
        }
        list_view.addFooterView(footerView);
//        先隐藏
        padding = (int) getResources().getDimension(R.dimen.DIMEN_15);
        footerView.setVisibility(View.GONE);
        footerView.setPadding(0, -footerView.getHeight(), 0, 0);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // 当不滚动时
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部，是否正在加载
                if (view.getLastVisiblePosition() == (view.getCount() - 1) && !isLaod) {
                    //判断是否有网络
//                    if (NetWorkUtils.isNetworkConnected(getActivity())) {
                    if (currentPages < allItemCount / each_page_num) {
                        currentPages++;
                        setPullUpState(LOAD);
                        pullUpCallBack();
                    } else {
//                            setPullUpState(NO_MORE);
                    }

//                    } else {
//                        setPullUpState(NETWORK_ERROR);
//                    }
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 设置上拉加载状态
     *
     * @param state
     */
    public void setPullUpState(int state) {
//        显示上拉布局
        footerView.setVisibility(View.VISIBLE);
        footerView.setPadding(padding, padding, padding, padding);
        switch (state) {
            case LOAD:
                isLaod = true;
                progressbar.setVisibility(View.VISIBLE);
                text.setText(getString(R.string.load_more));
                break;
            case SUCCEED:
                footerView.setVisibility(View.GONE);
                footerView.setPadding(0, -footerView.getHeight(), 0, 0);
                isLaod = false;
                break;
            case LOAD_ERROR:
                progressbar.setVisibility(View.GONE);
                text.setText(getString(R.string.load_error));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, DELYED);
                break;
            case NETWORK_ERROR:
                progressbar.setVisibility(View.GONE);
                text.setText(getString(R.string.network_exception));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, DELYED);
                break;
            case NO_MORE:
                progressbar.setVisibility(View.GONE);
                text.setText(getString(R.string.no_more));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, DELYED);
                break;
        }

//        滚动到底部
        list_view.setSelection(list_view.getBottom());
    }

    //   延迟2秒后隐藏底部
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            footerView.setVisibility(View.GONE);
            footerView.setPadding(0, -footerView.getHeight(), 0, 0);
            isLaod = false;
        }
    };


    /**
     * 子类继承上拉刷新实现
     */
    public abstract void pullUpCallBack();

}
