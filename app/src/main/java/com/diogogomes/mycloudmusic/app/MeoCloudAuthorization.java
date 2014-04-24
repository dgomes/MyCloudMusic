package com.diogogomes.mycloudmusic.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;


public class MeoCloudAuthorization extends Activity {
    public static final String TAG = "MeoCloudAuthorization";
    public static final String CALLBACK_URL = "oauth://mycloudmusic/";

    private WebView mWebView;
    private OAuthService mOAuthService;
    private Token mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meo_cloud_authorization);

        // Get the message from the intent
        Intent intent = getIntent();
        Log.d(TAG, "got Intent!");

        mOAuthService = new ServiceBuilder().debug()
                .provider(Scribe_MeoCloudApi.class)
                .apiKey(getString(R.string.MeoCloudApiKey))
                .apiSecret(getString(R.string.MeoCloudApiSecret))
                .callback(CALLBACK_URL)
                .build();

        mWebView = (WebView) findViewById(R.id.meocloud_webview);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.setWebViewClient(mWebViewClient);

        startAuthorize();
    }

    private void startAuthorize() {
        (new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                mRequestToken = mOAuthService.getRequestToken();

                Log.d(TAG, mRequestToken.getRawResponse());
                return mOAuthService.getAuthorizationUrl(mRequestToken);
            }

            @Override
            protected void onPostExecute(String url) {
                mWebView.loadUrl(url);
            }
        }).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meo_cloud_authorization, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if ((url != null) && (url.startsWith(CALLBACK_URL))) { // Override webview when user came back to CALLBACK_URL
                mWebView.stopLoading();
                mWebView.setVisibility(View.INVISIBLE); // Hide webview if necessary
                Uri uri = Uri.parse(url);
                final Verifier verifier = new Verifier(uri.getQueryParameter("oauth_verifier"));
                (new AsyncTask<Void, Void, Token>() {
                    @Override
                    protected Token doInBackground(Void... params) {
                        return mOAuthService.getAccessToken(mRequestToken, verifier);
                    }

                    @Override
                    protected void onPostExecute(Token accessToken) {
                        // AccessToken is passed here! Do what you want!
                        Log.d(TAG, accessToken.getRawResponse());
                        Intent intent = new Intent(getApplicationContext(),  MainActivity.class);
                        intent.putExtra("accessTokenSecret", accessToken.getSecret());
                        intent.putExtra("accessToken", accessToken.getToken());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).execute();
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    };
}
