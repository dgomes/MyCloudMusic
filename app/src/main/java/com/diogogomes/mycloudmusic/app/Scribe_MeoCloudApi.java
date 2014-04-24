package com.diogogomes.mycloudmusic.app;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.model.Verb;

/**
 * Created by dgomes on 23/04/14.
 */

public class Scribe_MeoCloudApi extends DefaultApi10a {

    @Override
    public String getAccessTokenEndpoint()
    {
        return "https://cloudpt.pt/oauth/access_token";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken)
    {
        return "https://cloudpt.pt/oauth/authorize?oauth_token="+requestToken.getToken();
    }

    @Override
    public String getRequestTokenEndpoint()
    {
        return "https://cloudpt.pt/oauth/request_token";
    }

    @Override
    public Verb getRequestTokenVerb() {
        return Verb.GET;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }
}