package com.diogogomes.meocloud;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dgomes on 02/05/14.
 */
public class Metadata implements Parcelable {

    private int bytes;
    private boolean thumb_exists;
    private String rev;
    private Date modified;
    private String path;
    private boolean is_dir;
    private String icon; //TODO what is this??
    private String root;
    private String mime_type; //TODO native type ?
    private String size; //TODO parse to bytes ?

    public Metadata(JSONObject obj) throws JSONException {
        setBytes(obj.getInt("bytes"));
        setThumb_exists(obj.getBoolean("thumb_exists"));
        setRev(obj.getString("rev"));
        setModified(obj.getString("modified"));
        setPath(obj.getString("path"));
        setIs_dir(obj.getBoolean("is_dir"));
        setIcon(obj.getString("icon"));
        setRoot(obj.getString("root"));
        setMime_type(obj.getString("mime_type"));
        setSize(obj.getString("size"));
    }
    // Parcelling part
    public Metadata(Parcel in){
        String[] data = new String[10];

        in.readStringArray(data);
        setBytes(Integer.parseInt(data[0]));
        setThumb_exists(Boolean.parseBoolean(data[1]));
        setRev(data[2]);
        setModified(data[3]);
        setPath(data[4]);
        setIs_dir(Boolean.parseBoolean(data[5]));
        setIcon(data[6]);
        setRoot(data[7]);
        setMime_type(data[8]);
        setSize(data[9]);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {Integer.toString(this.bytes), Boolean.toString(this.thumb_exists), this.rev, this.modified.toString(), this.path, Boolean.toString(this.is_dir), this.icon, this.root, this.mime_type, this.size  });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Metadata createFromParcel(Parcel in) {
            return new Metadata(in);
        }

        public Metadata[] newArray(int size) {
            return new Metadata[size];
        }
    };


    public int getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    public boolean isThumb_exists() {
        return thumb_exists;
    }

    public void setThumb_exists(boolean thumb_exists) {
        this.thumb_exists = thumb_exists;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(String modified) {

        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        try {
            this.modified = df.parse(modified);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isIs_dir() {
        return is_dir;
    }

    public void setIs_dir(boolean is_dir) {
        this.is_dir = is_dir;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
