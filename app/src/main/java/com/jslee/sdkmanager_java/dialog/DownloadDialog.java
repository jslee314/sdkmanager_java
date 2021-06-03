package com.jslee.sdkmanager_java.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import com.jslee.sdkmanager_java.BaseDialog;
import com.jslee.sdkmanager_java.R;
import com.jslee.sdkmanager_java.databinding.DialogDownloadBinding;

public class DownloadDialog extends BaseDialog {
    private final DialogDownloadBinding binding;

    public DownloadDialog(@NonNull Context context, DialogDownloadBinding binding) {
        super(context);
        this.binding = binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.contentText.setText(R.string.activity_version_update_progress_dialog_update_title_txt);
    }

    public void setFontSize(float contentSize, float progressRateSize, float progressNumSize) {
        binding.contentText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, contentSize);
        binding.progressRateTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, progressRateSize);
        binding.progressNumTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, progressNumSize);
    }
}
