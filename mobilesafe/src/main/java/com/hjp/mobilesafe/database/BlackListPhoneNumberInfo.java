package com.hjp.mobilesafe.database;

/**
 * Created by HJP on 2016/9/7 0007.
 */

public class BlackListPhoneNumberInfo {

    private int _id;
    private String phoneNumber;
    private String holdMode;

    public BlackListPhoneNumberInfo(String pn, String hm) {
        phoneNumber = pn;
        holdMode = hm;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHoldMode() {
        return holdMode;
    }

    public void setHoldMode(String holdMode) {
        this.holdMode = holdMode;
    }


}
