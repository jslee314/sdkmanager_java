package com.jslee.sdkmanager_java;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.Window;

import androidx.annotation.NonNull;

public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥 영역 클릭시 닫히지 않게 설정
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }
}
