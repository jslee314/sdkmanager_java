package com.jslee.sdkmanager_java;

import android.app.Application;
import android.content.Context;
import android.content.Intent;


public class BasicApp extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler(this));

    }
}

/**
 * @작성자 : 길용현
 * @최초작성일 : 2019-09-03 오후 5:59
 * @내용 : try-catch 문을 제외하고 예외가 발생했을 때 이벤트를 처리하는 클래스
 * @수정 :
 * @버젼 : 1.0.0
 **/
class AppExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Context mContext;

    AppExceptionHandler(Context context) {
        mContext = context;
    }


    /**
     * @작성자 : 길용현
     * @최초작성일 : 2019-09-03 오후 6:03
     * @내용 : uncaught exception 이 발생했을 때 추가 로직을 수행하는 함수
     * @수정 :
     * @버젼 : 1.0.0
     **/
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        Intent crashedIntent = new Intent(mContext, UncaughtExceptionActivity.class);
        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        crashedIntent.putExtra("errorMessage", throwable.toString());
        mContext.startActivity(crashedIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}