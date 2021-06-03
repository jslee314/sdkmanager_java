package com.jslee.sdkmanager_java.util.retrofit;


import com.jslee.sdkmanager_java.data.NewVersionCall;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {

    @GET("version/checkNewVersion")
    Call<NewVersionCall> checkNewVersion();

}
