package cn.leaf.record;

import android.app.Application;
import android.content.Context;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

/*
获取整个应用的全局context, 以便能在任何类中调用
但事实上好像并没怎么用到
 */
public class AppContextUtil extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
            Python py= Python.getInstance();
            PyObject test=py.getModule("main");
            test.callAttr("init");
        }

    }

    public static Context getContext(){
        return context;
    }
}