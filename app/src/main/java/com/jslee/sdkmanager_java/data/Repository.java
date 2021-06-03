package com.jslee.sdkmanager_java.data;

import android.app.Application;

import com.jslee.sdkmanager_java.util.AppExecutors;


public class Repository {
    private DataSource mLocalDataSource;
    private DataSource mRemoteDataSource;
    private static volatile Repository INSTANCE;

    public Repository(Application app){

        mLocalDataSource = LocalDataSourceImpl.getInstance(AppExecutors.getInstance());
        mRemoteDataSource = RemoteDataSourceImpl.getInstance(AppExecutors.getInstance());
    }

    public static Repository getInstance(Application app) {
        if (INSTANCE == null) {
            synchronized (Repository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Repository(app);
                }
            }
        }
        return INSTANCE;
    }

    public void checkNewVersion(DataSource.CheckNewVersionCallback callback) {
        mRemoteDataSource.checkNewVersion(callback);
    }
}