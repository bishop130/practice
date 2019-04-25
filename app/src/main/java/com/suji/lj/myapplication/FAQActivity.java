package com.suji.lj.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FAQActivity extends AppCompatActivity {
    private WebView mWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        mWebView = findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient());
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        String pdf = "http://bishop130.cafe24.com/index.html";

        mWebView.loadUrl(pdf);
    }
}
