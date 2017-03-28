package com.xwsd.app.tools;

import android.view.Gravity;
import android.widget.Toast;

import com.xwsd.app.AppContext;


/**
 * Created by llbt on 2016/4/25.
 */
public class ToastUtil {

    public static void show(Object object) {
        Toast toast = Toast.makeText(AppContext.context(), object + "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
