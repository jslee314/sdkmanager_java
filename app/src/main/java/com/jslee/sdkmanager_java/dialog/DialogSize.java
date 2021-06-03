package com.jslee.sdkmanager_java.dialog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DialogSize {
    private DialogSize() {
    }

    private static class SingleTonHolder {
        private static final DialogSize instance = new DialogSize();
    }

    public static DialogSize getInstance() {
        return SingleTonHolder.instance;
    }

    /**
     * @내용 : CaughtExceptionDialog 의 화면 크기를 설정하는 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-22 오후 6:26
     * @작성자 : 길용현
     **/
    public void setUpDialogLayout(ExceptionDialog dialog, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (width * 0.822f);
        lp.height = (int) (height * 0.35f);
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * @작성자 : 길용현
     * @최초작성일 : 2019-09-18 오전 11:02
     * @내용 : DownloadDialog 의 화면 크기를 설정하는 함수
     * @수정 :
     * @버젼 : 0.0.0
     **/
    public void setUpDialogLayout(DownloadDialog dialog, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (width * 0.822f);
        dialog.getWindow().setAttributes(lp);
    }

}
