package com.subratgupta.deshbookalpha;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    WebView myWebView;
    SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);

        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings .setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        myWebView.setWebViewClient(new Browser_home());
        myWebView.setWebChromeClient(new MyChrome());
        myWebView.loadUrl("https://m.facebook.com");
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                myWebView.setVisibility(View.GONE);
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myWebView.setVisibility(View.VISIBLE);
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }
                }, 1000);

                String inUrl = url, outUrl = "";
                EditText inText = findViewById(R.id.input);
                EditText outText = findViewById(R.id.output);
                inText.setText("Input Url: "+inUrl);

                if (inUrl.contains("browser_fallback_url=https://m.facebook.com/messages/")) {
                    outUrl = "https://m.facebook.com/messages?soft=messages";
                    view.loadUrl(outUrl);
                    outText.setText("Output Url: "+outUrl);
                } else {
                    view.loadUrl(inUrl);
                }
                return false;
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //Clearing the WebView

                EditText inText = findViewById(R.id.input);
                inText.setText("\nError Code: "+ errorCode +"\nDescription: "+ description);

                try {
                    myWebView.stopLoading();
                } catch (Exception e) {
                }
                try {
                    myWebView.clearView();
                } catch (Exception e) {
                }
                if (myWebView.canGoBack()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myWebView.goBack();
                        }
                    }, 1);
                }
                super.onReceivedError(myWebView, errorCode, description, failingUrl);
            }
        });
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        myWebView.reload();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 1000);
                    }
                }
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void toastPrint(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                myWebView.loadUrl(myWebView.getUrl());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }
    }
}

class Browser_home extends WebViewClient {

    Browser_home() {
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

    }
}