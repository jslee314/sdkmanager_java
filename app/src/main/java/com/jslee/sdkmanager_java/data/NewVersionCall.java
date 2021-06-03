package com.jslee.sdkmanager_java.data;

import com.google.gson.annotations.SerializedName;

public class NewVersionCall {
    @SerializedName("status")
    public int status;
    @SerializedName("new_version")
    public String newVersion;
    @SerializedName("err_msg")
    public String errMsg;
}
