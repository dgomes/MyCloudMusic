package com.diogogomes.meocloud;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dgomes on 04/05/14.
 */
public class Media implements Parcelable {

    private String url;
    private String expires; //TODO Date

    public Media(JSONObject obj) throws JSONException {
        url = obj.getString("url");
        expires = obj.getString("expires");
    }

    public Media(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);
        this.url = data[0];
        this.expires = data[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.url, this.expires});
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
