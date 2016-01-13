package com.kinvey.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kinvey.android.Client;
import com.kinvey.android.R;

/***
 * Provides a WebView for easy logging into MIC.
 */
public class MICLoginActivity extends Activity {

    public static final String KEY_LOGIN_URL = "loginURL";
    public static final String KEY_REDIRECT_URL = "redirectURL";

    private WebView micView;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_miclogin);

        Intent i = getIntent();
        String loginURL = i.getStringExtra(KEY_LOGIN_URL);
        String redirectURL = i.getStringExtra(KEY_REDIRECT_URL);
        
        if (loginURL == null){
        	onNewIntent(this.getIntent());
        	return;
        }
        
        
        micView = (WebView) findViewById(R.id.mic_loginview);
        loadLoginPage(loginURL, redirectURL);
    }

    private void loadLoginPage(String url, final String redirectUrl){
    	
        micView.loadUrl(url);

        micView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith(redirectUrl.toLowerCase())){
                    Client.sharedInstance().user().onOAuthCallbackRecieved(Uri.parse(url));
                    MICLoginActivity.this.finish();
                }
            }
        });
    }


    @Override
    public void onNewIntent(Intent intent){

        super.onNewIntent(intent);
        Client.sharedInstance().user().onOAuthCallbackRecieved(intent);
        this.finish();
    }
}
