package com.diogogomes.meocloud;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dgomes on 26/04/14.
 */
public class APIv1 {
    /* Endpoints */
    public static final String API_ENDPOINT = "https://publicapi.meocloud.pt/1/";
    public static final String API_CONTENT_ENDPOINT = "https://api-content.meocloud.pt";
    public static final String CALLBACK_URL = "oauth://mycloudmusic/";

    /* Requests */
    public static final String API_ACCOUNT_INFO = "Account/Info";
    public static final String API_SEARCH = "Search/meocloud/";

    public static final String TAG = "MeoCloud API";

    private OAuthService mOAuthService;
    private Token mAccessToken;
    private Token mRequestToken;

    public AccountInfo accountInfo = new AccountInfo();

    public APIv1(String apiKey, String apiSecret) {
        Log.d(TAG, "APIv1()");
        mOAuthService = new ServiceBuilder() //.debug()
                .provider(Scribe_MeoCloudApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(CALLBACK_URL)
                .build();
    }

    public String getAuthorizationUrl() {
        mRequestToken = mOAuthService.getRequestToken();

        Log.d(TAG, mRequestToken.getRawResponse());
        return mOAuthService.getAuthorizationUrl(mRequestToken);
    }

    public Token getAccessToken(String oauth_verifier) {
        Verifier verifier = new Verifier(oauth_verifier);

        mAccessToken = mOAuthService.getAccessToken(mRequestToken, verifier);

        return mAccessToken;
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
            return null;
        }
        Log.d(TAG, "return getAccountInfo()");
        return accountInfo;
    }

    public ArrayList<Metadata> search(String query, String mime_type, int file_limit, boolean include_deleted) {
        String url = API_ENDPOINT + API_SEARCH+ "Music";
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        if(query != null)
            request.addQuerystringParameter("query", query);
        if(mime_type != null)
            request.addQuerystringParameter("mime_type", mime_type);
        request.addQuerystringParameter("file_limit", Integer.toString(file_limit) );
        request.addQuerystringParameter("include_deleted", Boolean.toString(include_deleted) );

        Log.d(TAG, request.getCompleteUrl());
        mOAuthService.signRequest(mAccessToken, request);

        Response resp = request.send();
        Log.d(TAG, "Got it! Lets see what we found...");
        Log.d(TAG, resp.getBody());

        ArrayList<Metadata> metadata = new ArrayList<Metadata>();

        try {
            JSONArray res = new JSONArray(resp.getBody());
            for(int i = 0; i<res.length(); i++) {
                Metadata m = new Metadata(res.getJSONObject(i));
                metadata.add(m);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return metadata;
    }

}
