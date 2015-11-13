package com.miranda.luis.appsupport;

import android.content.Context;
import android.os.Bundle;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class LogIn extends Activity {
    private WebView myWebView;
    private String TAG = "LogIn";
    private String LOCAL_FILE = "file:///android_asset/login.html";

    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myWebView = (WebView) findViewById(R.id.web);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(LOCAL_FILE);

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(final WebView view, String url) {


                                view.loadUrl("javascript:showSuccessToast();");

            }

        });


    }




    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        // This function can be called in our JS script now
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public int verify(String user, String pass) {

            Toast.makeText(mContext, user+"     "+pass, Toast.LENGTH_LONG).show();

            final String datos[]={user.trim(),pass.trim(),"12345"};

            final httpHandler handler = new httpHandler();
            String result = handler.post("http://flores.rosales.engineer/appSupport/register.php", datos);
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();

            return 0;

        }

    }

}