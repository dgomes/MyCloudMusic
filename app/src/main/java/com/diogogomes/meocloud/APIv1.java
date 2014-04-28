package com.diogogomes.meocloud;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.concurrent.ExecutionException;

/**
 * Created by dgomes on 26/04/14.
 */
public class APIv1 {
    /* Endpoints */
    public static final String API_ENDPOINT = "https://publicapi.meocloud.pt/1/";
    public static final String API_CONTENT_ENDPOINT = "https://api-content.meocloud.pt";

    /* Requests */
    public static final String API_ACCOUNT_INFO = "Account/Info";

    public static final String TAG = "MeoCloud API";

    private OAuthService mOAuthService;
    private Token mAccessToken;

    public AccountInfo accountInfo = new AccountInfo();

    public APIv1(String apiKey, String apiSecret) {
        Log.d(TAG, "APIv1()");
        mOAuthService = new ServiceBuilder() //.debug()
                .provider(Scribe_MeoCloudApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();
    }

    public void setAccessToken(String accessToken, String accessTokenSecret) {
        Log.d(TAG, "setAccessToken()");

        mAccessToken = new Token(accessToken, accessTokenSecret);
    }

    public AccountInfo getAccountInfo() {
        Log.d(TAG, "retrieving account information...");

        OAuthRequest request = new OAuthRequest(Verb.GET, API_ENDPOINT + API_ACCOUNT_INFO);
        mOAuthService.signRequest(mAccessToken, request);

        Response resp = request.send();
        Log.d(TAG, "Got it! Lets see what we found...");
        Log.d(TAG, resp.getBody());

        try {
            JSONObject obj = new JSONObject(resp.getBody());
            Log.d(TAG, obj.getString("display_name"));

            accountInfo.setDisplay_name(obj.getString("display_name"));
            accountInfo.setCreated_on(obj.getString("created_on"));
            accountInfo.setUid(obj.getLong("uid"));

            //TODO SEND to Activity
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "return getAccountInfo()");
        return accountInfo;
    }

}
