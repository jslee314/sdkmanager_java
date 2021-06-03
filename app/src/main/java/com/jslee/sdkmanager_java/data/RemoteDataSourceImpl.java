package com.jslee.sdkmanager_java.data;

import com.jslee.sdkmanager_java.util.AppExecutors;
import com.jslee.sdkmanager_java.util.retrofit.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jslee.sdkmanager_java.data.ConstantVariable.RetrofitCallStatus.ERROR_STATUS;
import static com.jslee.sdkmanager_java.data.ConstantVariable.RetrofitCallStatus.SUCCESS_STATUS;


public class RemoteDataSourceImpl implements DataSource{
    private static volatile RemoteDataSourceImpl INSTANCE;
    private AppExecutors mAppExecutors;

    private RemoteDataSourceImpl(AppExecutors appExecutors) {
        this.mAppExecutors = appExecutors;
    }

    public static RemoteDataSourceImpl getInstance(AppExecutors mAppExecutors) {
        if (INSTANCE == null) {
            synchronized (RemoteDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RemoteDataSourceImpl(mAppExecutors);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @내용 : APK 최신 버전을 가져오는 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-22 오후 7:58
     * @작성자 : 길용현
     **/
    @Override
    public void checkNewVersion(CheckNewVersionCallback callback) {
        final RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();

        Runnable runnable = () -> {
            Call<NewVersionCall> checkNewVersionCall = retrofitHelper.getServer().checkNewVersion();
            checkNewVersionCall.enqueue(new Callback<NewVersionCall>() {
                @Override
                public void onResponse(Call<NewVersionCall> call, Response<NewVersionCall> response) {
                    NewVersionCall resData = response.body();

                    switch (resData.status) {
                        case ERROR_STATUS:
                            mAppExecutors.getMainThread().execute(() -> callback.onErrorCheckNewVersion(resData.errMsg));
                            break;
                        case SUCCESS_STATUS:
                            mAppExecutors.getMainThread().execute(() -> callback.onSucceedCheckNewVersion(resData.newVersion));
                            break;
                    }
                }

                @Override
                public void onFailure(Call<NewVersionCall> call, Throwable t) {
                    mAppExecutors.getMainThread().execute(() -> callback.onErrorCheckNewVersion(t.getMessage()));
                }
            });
        };
        mAppExecutors.getNetworkThread().execute(runnable);
    }



}
