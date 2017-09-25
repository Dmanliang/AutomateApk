package com.example.dell.automateapk;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.dell.automateapk.base.AndroidAdjustResizeBugFix;
import com.example.dell.automateapk.base.AndroidBug5497Workaround;
import com.example.dell.automateapk.base.BaseActivity;

public class MainActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_main);
        //跳转软键盘与屏幕尺寸的问题
        AndroidAdjustResizeBugFix.assistActivity(this);
        //解决WebView上软键盘覆盖输入框的问题
        AndroidBug5497Workaround.assistActivity(this);
        initView();
    }

    public void initView(){
        mWebView         =   (WebView)findViewById(R.id.webviews);
        mWebView.requestFocusFromTouch();
        WebSettings setting = mWebView.getSettings();
        mWebView.requestFocus();
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setDisplayZoomControls(true);
        setting.setJavaScriptEnabled(true);             //  设置支持javascript脚本
        setting.setAllowFileAccess(true);               //  允许访问文件
        setting.setLoadWithOverviewMode(true);          //  缩放至屏幕的大小
        setting.setBuiltInZoomControls(true);           //  设置显示缩放按钮
        setting.setSupportZoom(true);                   //  支持缩放
        setting.setUseWideViewPort(true);               //  将图片调整到适合webview的大小
        setting.setAppCacheEnabled(false);              //  加缓存
        setting.setRenderPriority(WebSettings.RenderPriority.HIGH);  //提高渲染的优先级
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);               //  支持数据库
        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//关闭webview中缓存
        setting.setNeedInitialFocus(true);              //当webview调用requestFocus时为webview设置节点
        setting.setLoadsImagesAutomatically(true);      //支持自动加载图片
        setting.setDefaultTextEncodingName("utf-8");    //设置编码格式
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mWebView.addJavascriptInterface(this, "android");
        //Webview兼容cookie
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView,true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                } else {
                    view.loadUrl(request.toString());
                }
                return false;
            }
        });
        mWebView.loadUrl(this.getResources().getString(R.string.url));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !mWebView.canGoBack()) {
            ///对于好多应用，会在程序中杀死 进程，这样会导致我们统计不到此时Activity结束的信息
            ///对于这种情况需要调用 'MobclickAgent.onKillProcess( Context )方法
            ///保存一些页面调用的数据。正常的应用是不需要调用此方法的。
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        System.exit(0);
     /*   if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            mSearchLayout.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
        } else {
            mSearchLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        }*/
        super.onDestroy();
    }
}
