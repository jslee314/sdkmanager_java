package com.jslee.sdkmanager_java.version_update;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jslee.sdkmanager_java.BaseActivity;
import com.jslee.sdkmanager_java.CaughtExceptionActivity;
import com.jslee.sdkmanager_java.R;
import com.jslee.sdkmanager_java.databinding.ActivityVersionUpdateBinding;
import com.jslee.sdkmanager_java.databinding.DialogDownloadBinding;
import com.jslee.sdkmanager_java.dialog.DialogSize;
import com.jslee.sdkmanager_java.dialog.DownloadDialog;
import com.jslee.sdkmanager_java.util.SvgSoftwareLayerSetter;


import java.io.File;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class VersionUpdateActivity extends BaseActivity {
    private ActivityVersionUpdateBinding binding;
    private VersionUpdateViewModel mViewModel;
    private RequestBuilder<PictureDrawable> requestBuilder;
    private DownloadDialog downloadDialog;
    private DialogDownloadBinding downloadBinding;
    private Intent errorMessageIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("jjslee", "VersionUpdateActivity onCreate");

        setUpBinding();
        setUpView();
        setUpObserver();

        mViewModel.checkNewVersion();
    }

    private void setUpBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_version_update);
        mViewModel = obtainViewModel();
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        downloadBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_download, null, false);
        downloadBinding.setViewModel(mViewModel);
        downloadBinding.setLifecycleOwner(this);

        PackageManager packageManager = getPackageManager();
        try {
            // 현재 버전 가져오기
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;

            // apk 파일 저장 path
            String localAPKFileSavePath = "";
            String remoteAPKFileSavePath = "";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                localAPKFileSavePath = getFilesDir() + File.separator + getString(R.string.local_resource_save_folder);
                remoteAPKFileSavePath = String.format(getString(R.string.apk_file_url), getString(R.string.remote_host)) + getString(R.string.apk_file_name);
            }

            mViewModel.init(localAPKFileSavePath, remoteAPKFileSavePath, versionName, packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            mViewModel.getErrorMessage().setValue(e.getMessage());
        }
    }

    public VersionUpdateViewModel obtainViewModel() {
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, viewModelFactory).get(VersionUpdateViewModel.class);
    }

    private void setUpView() {
        binding.updateVerTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(16));
        binding.currVerTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(12));
        binding.newVerTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(12));
        binding.updateBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(18));

        ConstraintLayout.LayoutParams updateTvLayoutParams = (ConstraintLayout.LayoutParams) binding.updateVerTxt.getLayoutParams();
        updateTvLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(16)),getResources().getDisplayMetrics());
        binding.updateVerTxt.setLayoutParams(updateTvLayoutParams);
        binding.updateVerTxt.requestLayout();

        ConstraintLayout.LayoutParams curVersionTvLayoutParams = (ConstraintLayout.LayoutParams) binding.currVerTxt.getLayoutParams();
        curVersionTvLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(16)),getResources().getDisplayMetrics());
        binding.currVerTxt.setLayoutParams(curVersionTvLayoutParams);
        binding.currVerTxt.requestLayout();

        ConstraintLayout.LayoutParams updateBtnLayoutParams = (ConstraintLayout.LayoutParams) binding.updateBtn.getLayoutParams();
        updateBtnLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(29)),getResources().getDisplayMetrics());
        binding.updateBtn.setLayoutParams(updateBtnLayoutParams);
        binding.updateBtn.requestLayout();
    }

    private void setUpObserver() {
        /* throw를 통해 발생시킨 에러(crashlytics에 보낼것)*/
        mViewModel.getThrowException().observe(this, (throwable -> {
            if (throwable != null) {

                // 2. 에러 다이얼로그 띄움
                errorMessageIntent = new Intent(this, CaughtExceptionActivity.class);
                errorMessageIntent.putExtra("errorMessage", throwable.getMessage());
                this.startActivity(errorMessageIntent);
                finishAffinity();
            }
        }));

        mViewModel.getErrorMessage().observe(this, (errorMessage -> {
            if (errorMessage != null) {

                // 2. 에러 다이얼로그 띄움
                errorMessageIntent = new Intent(this, CaughtExceptionActivity.class);
                errorMessageIntent.putExtra("errorMessage", errorMessage);
                this.startActivity(errorMessageIntent);
                finishAffinity();
            }
        }));

        mViewModel.getIsUpdate().observe(this, (isUpdate -> {
            String imgUrl;

            if (isUpdate) imgUrl = String.format(getString(R.string.image_url), getString(R.string.remote_host)) + getString(R.string.eoc_img_update_ver_old);
            else imgUrl = String.format(getString(R.string.image_url), getString(R.string.remote_host)) + getString(R.string.eoc_img_update_ver_new);

            if (!TextUtils.isEmpty(imgUrl)) {
                requestBuilder =
                        Glide.with(getApplicationContext())
                                .as(PictureDrawable.class)
                                .transition(withCrossFade())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .listener(new SvgSoftwareLayerSetter());

                requestBuilder.
                        load(imgUrl).
                        into(binding.updateVerImg);
            }
        }));

        mViewModel.getGoUpdate().observe(this, (goUpdate -> {
            if (goUpdate) {
                downloadDialog = new DownloadDialog(this, downloadBinding);
                downloadDialog.show();
                DialogSize dialogSize = DialogSize.getInstance();
                dialogSize.setUpDialogLayout(downloadDialog, getApplicationContext());

                downloadBinding.contentText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(12));
                downloadBinding.progressRateTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(9));
                downloadBinding.progressNumTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(9));

                ConstraintLayout.LayoutParams contentTextLayoutParams = (ConstraintLayout.LayoutParams) downloadBinding.contentText.getLayoutParams();
                contentTextLayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(12)), getResources().getDisplayMetrics());
                contentTextLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(12)), getResources().getDisplayMetrics());
                contentTextLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(12)), getResources().getDisplayMetrics());
                downloadBinding.contentText.setLayoutParams(contentTextLayoutParams);
                downloadBinding.contentText.requestLayout();

                ConstraintLayout.LayoutParams progressBarLayoutParams = (ConstraintLayout.LayoutParams) downloadBinding.progressBar.getLayoutParams();
                progressBarLayoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(10)), getResources().getDisplayMetrics());
                progressBarLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(12)), getResources().getDisplayMetrics());
                downloadBinding.progressBar.setLayoutParams(progressBarLayoutParams);
                downloadBinding.progressBar.requestLayout();

                ConstraintLayout.LayoutParams progressRateTxtLayoutParams = (ConstraintLayout.LayoutParams) downloadBinding.progressRateTxt.getLayoutParams();
                progressRateTxtLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(2)), getResources().getDisplayMetrics());
                progressRateTxtLayoutParams.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(getConvertDpByRes(12)), getResources().getDisplayMetrics());
                downloadBinding.progressRateTxt.setLayoutParams(progressRateTxtLayoutParams);
                downloadBinding.progressRateTxt.requestLayout();
            } else {
                if (downloadDialog != null) {
                    downloadDialog.dismiss();
                }
            }
        }));

        mViewModel.getLocalAPKFileUri().observe(this, (localAPKFileUri -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(localAPKFileUri, "application/vnd.android.package-archive");
            startActivity(intent);
        }));

    }
}
