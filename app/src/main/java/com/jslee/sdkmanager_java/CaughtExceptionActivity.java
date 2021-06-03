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
 * @내용 : 에러 발생 시 다이얼로그를 출력하고 종료하는 클래스
 * @수정 :
 * @버젼 : 0.0.0
 * @최초작성일 : 2021-03-16 오후 4:07
 * @작성자 : 길용현
 **/
public class CaughtExceptionActivity extends BaseActivity {
    private ExceptionDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent secondIntent = getIntent();
        String message = secondIntent.getStringExtra("errorMessage");

        final View.OnClickListener positiveBtnListener = v -> {
//            if (isAliveService()) stopService(new Intent(this, LoggingService.class));
            finishAffinity();
            Intent intent = new Intent(this, LoadingActivity.class);
            startActivity(intent);
            System.exit(0);
        };

        dialog = new ExceptionDialog(this, positiveBtnListener, message);
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