package com.jslee.sdkmanager_java;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jslee.sdkmanager_java.dialog.DialogSize;
import com.jslee.sdkmanager_java.dialog.ExceptionDialog;
import com.jslee.sdkmanager_java.loading.LoadingActivity;


/**
 * @작성자 : 길용현
 * @최초작성일 : 2019-09-03 오후 6:07
 * @내용 : Uncaught Exception 이 발생했을 때 추가 로직 및 에러 화면을 출력하는 클래스
 * @수정 :
 * @버젼 : 1.0.0
 **/
public class UncaughtExceptionActivity extends BaseActivity {
    private ExceptionDialog dialog;

    /**
     * @작성자 : 길용현
     * @최초작성일 : 2019-09-03 오후 6:09
     * @내용 : UncaughtExceptionActivity 가 생성되었을 때 다이얼로그 창을 띄우고 이벤트 리스너를 설정하는 함수
     * @수정 :
     * @버젼 : 1.0.0
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View.OnClickListener positiveBtnListener = v -> {
//            if (isAliveService()) stopService(new Intent(this, LoggingService.class));
            finishAffinity();
            Intent intent = new Intent(this, LoadingActivity.class);
            startActivity(intent);
            System.exit(0);
        };

        String errorMessage = "";
        if (getIntent().getExtras() != null) {
            errorMessage = getIntent().getExtras().getString("errorMessage");
        }

        dialog = new ExceptionDialog(this, positiveBtnListener, errorMessage);
        dialog.show();
        dialog.setFontSize(getConvertDpByRes(16), getConvertDpByRes(14), getConvertDpByRes(12), getConvertDpByRes(16));
        DialogSize dialogSize = DialogSize.getInstance();
        dialogSize.setUpDialogLayout(dialog, getApplicationContext());
    }

    private Boolean isAliveService() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (LoggingService.class.getName().equals(service.service.getClassName())) {
//                return true;
//            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        super.onStop();
    }
}
