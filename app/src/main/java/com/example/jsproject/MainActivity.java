package com.example.jsproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {
    WebView mWebView;
    public static final String JS_METHOD = "mJs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);//设置与Js交互的权限
        mWebView.setWebContentsDebuggingEnabled(true);
        //参数1：Java对象名 参数2：Javascript对象名
        //通过addJavascriptInterface() AJavaScriptInterface类对象映射到JS的mJs对象
        mWebView.addJavascriptInterface(new JSKit(),JS_METHOD);
        // 加载JS代码
        mWebView.loadUrl("file:///android_asset/javascript.html");

    }

    public class JSKit {
        // 定义JS需要调用的方法，被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void saveDeviceId(String id) {
            System.out.println("JS成功调用了Android的hello方法"+id);
            SharedPreferences preferences=getSharedPreferences("device", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("deviceId", id);
            editor.commit();
        }

        @JavascriptInterface
        public void getDeviceId(){
            //会执行页面刷新 callJS("result")
            SharedPreferences preferences=getSharedPreferences("device", Context.MODE_PRIVATE);
            String result=preferences.getString("deviceId", "");
            //""之外需要用''包围

            mWebView.postDelayed(() -> {
                mWebView.loadUrl("javascript:onDeviceId('" + result + "')");
            }, 0);

           /* mWebView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                }
            });*/

        }
    }


}