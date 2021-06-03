package com.jslee.sdkmanager_java.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jslee.sdkmanager_java.BaseDialog;
import com.jslee.sdkmanager_java.R;

/**
 * @내용 : 에러 발생 문구를 띄워주는 다이얼로그
 * @수정 :
 * @버젼 : 0.0.0
 * @최초작성일 : 2021-02-22 오후 6:01
 * @작성자 : 길용현
 **/
public class ExceptionDialog extends BaseDialog {
    private View.OnClickListener pl;
    private String mMessage;

    private TextView title;
    private TextView subTitle;
    private TextView content;
    private Button btn;

    public ExceptionDialog(@NonNull Context context, View.OnClickListener pl, String message) {
        super(context);
        this.pl = pl;
        this.mMessage = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_exception);

        title = findViewById(R.id.dialog_title);
        subTitle = findViewById(R.id.dialog_sub_title);
        content = findViewById(R.id.dialog_content);
        content.setText(mMessage);
        content.setMovementMethod(new ScrollingMovementMethod());
        btn = findViewById(R.id.ok_btn);
        btn.setOnClickListener(pl);

        ExceptionDialog.this.setCancelable(false);
    }

    public void setFontSize(float titleFontSize, float subTitleFontSize, float contentFontSize, float btnFontSize) {
        this.title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleFontSize);
        this.subTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, subTitleFontSize);
        this.content.setTextSize(TypedValue.COMPLEX_UNIT_DIP, contentFontSize);
        this.btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, btnFontSize);
    }
}