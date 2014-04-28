package com.diogogomes.meocloud;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dgomes on 26/04/14.
 */

public class AccountInfo implements Serializable {
    public class Quota implements Serializable {
        private long shared;
        private long quota;
        private long normal;
    }

    private long uid;
    private String referral_code;
    private Date created_on;
    private Quota quota_info;
    private String display_name;
    private Date last_event;
    private boolean trial;
    private String email;
    private boolean active;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        try {
            this.created_on = df.parse(created_on);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Quota getQuota_info() {
        return quota_info;
    }

    public void setQuota_info(Quota quota_info) {
        this.quota_info = quota_info;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Date getLast_event() {
        return last_event;
    }

    public void setLast_event(String last_event) {
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        try {
            this.last_event = df.parse(last_event);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean isTrial() {
        return trial;
    }

    public void setTrial(boolean trial) {
        this.trial = trial;
    }

}
