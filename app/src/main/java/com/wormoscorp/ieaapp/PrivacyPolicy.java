package com.wormoscorp.ieaapp;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicy extends AppCompatActivity {

    WebView privacyPolicyWv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        String url = getIntent().getStringExtra("url");


        WebSettings websettings = privacyPolicyWv.getSettings();
        websettings.setJavaScriptEnabled(true);
        privacyPolicyWv.setWebViewClient(new Callback());
        privacyPolicyWv.loadUrl(url);
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}