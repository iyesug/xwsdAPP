package com.xwsd.app.view;/**
 * Created by Administrator on 2017/4/19.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.IPickerViewData;
import com.xwsd.app.tools.Helper;

import java.util.Arrays;
import java.util.List;

/**
 * 作者:LiuYF on 2017/4/19 11:07
 *  选择器 支持一到三级
 */

public class SelectDialog<T extends IPickerViewData> {
    private Context context;
    private List<T> listData1;
    private List<List<T>> listData2;
    private List<List<List<T>>> listData3;
    private String titleName="";
    private OnOptionsSelectListener listener;
    private OptionsPickerView pvOptions;

    public SelectDialog(Context context) {
        this.context=context;
    }

    public SelectDialog setTitle(String titleName){
        this.titleName=titleName;
        return this;
    }

    public SelectDialog setData1(List<T> listData1){
        this.listData1=listData1;
        return this;
    }
    public SelectDialog setData1(T [] arrData1){
        if(Helper.isNotNull(arrData1)){
            this.listData1=Arrays.asList(arrData1);
        }
        return this;
    }
    public SelectDialog setData2(List<List<T>> listData2){
        this.listData2=listData2;
        return this;
    }
    public SelectDialog setData3(List<List<List<T>>> listData3){
        this.listData3=listData3;
        return this;
    }

    public SelectDialog setSelectListener(OnOptionsSelectListener listener){
        this.listener=listener;
        return this;
    }

    public List<T> getListData1() {
        return listData1;
    }

    public List<List<T>> getListData2() {
        return listData2;
    }

    public List<List<List<T>>> getListData3() {
        return listData3;
    }

    public void create(){
        pvOptions = new OptionsPickerView.Builder(context, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                pvOptions.dismiss();
                if (Helper.isNotNull(listener)){
                    T data1=null;
                    if (Helper.isNotNull(listData1)){
                        data1=listData1.get(options1);
                    }
                    T data2=null;
                    if (Helper.isNotNull(listData2)){
                        data2=listData2.get(options1).get(options2);
                    }
                    T data3=null;
                    if (Helper.isNotNull(listData3)){
                        data3=listData3.get(options1).get(options2).get(options3);
                    }
                    listener.onOptionsSelect(data1,data2,data3);
                }
            }
        }).setTitleText(titleName)
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.rgb(204, 204, 204))//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)//列表背景颜色
                .setTitleBgColor(Color.rgb(50, 169, 242))//标题栏颜色
                .setTitleColor(Color.WHITE)//标题栏名字颜色
                .setCancelColor(Color.WHITE)
                .setSubmitColor(Color.WHITE)
                .setTextColorCenter(Color.BLACK)//选中区域文本颜色
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLineSpacingMultiplier(2f)
                .build();
        if (Helper.isNotNull(listData1)&&listData1.size()>0
                &&Helper.isNotNull(listData2)&&listData2.size()>0
                &&Helper.isNotNull(listData3)&&listData3.size()>0){
            //三级选择器
            pvOptions.setPicker(listData1,listData2,listData3);
        }else if (Helper.isNotNull(listData1)&&listData1.size()>0
                &&Helper.isNotNull(listData2)&&listData2.size()>0){
            pvOptions.setPicker(listData1,listData2);
        }else if (Helper.isNotNull(listData1)&&listData1.size()>0){
            pvOptions.setPicker(listData1);
        }else {
            throw new RuntimeException("选择器器三级数据都为空");
        }
        show();
    }

    public void show(){
        if (Helper.isNotNull(pvOptions)&&!pvOptions.isShowing()){
            pvOptions.show();
        }
    }

    public void dismiss(){
        if (Helper.isNotNull(pvOptions)&&pvOptions.isShowing()){
            pvOptions.dismiss();
        }
    }

    public interface OnOptionsSelectListener{
        void onOptionsSelect(Object data1, Object data2, Object data3);
    }
}
