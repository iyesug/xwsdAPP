package com.xwsd.app.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 基础Fragment类
 * setContentView 方法需要子类返回一个内容布局
 * init 方法可做一些初始化的操作
 */
public abstract class BaseFragment extends Fragment {
    /**
     * 根布局
     */
    protected View rootView;

    /**
     * 是否创建完成
     */
    private boolean isPrepared = false;

    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce = false;

    /**
     * Fragment当前状态是否可见
     */
    private boolean isVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        避免重复加载UI
        if (rootView == null) {
            rootView = setContentView(inflater);
        }
//        缓存的rootView需要判断是否已经被加入过parent, 如果有则需要从parent删除，否则会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        isPrepared = true;
        lazyLoad();
    }

    /**
     * 设置布局
     */
    protected abstract View setContentView(LayoutInflater inflater);

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 可见
     */
    public void onVisible() {
        lazyLoad();
    }

    /**
     * 不可见
     */
    public void onInvisible() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    /**
     * 懒加载
     */
    private void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        firstRequestData();
    }

    /**
     * Fragment显示在界面上了（第一次请求数据）
     */
    protected void firstRequestData() {
        mHasLoadedOnce = true;
    }
}
