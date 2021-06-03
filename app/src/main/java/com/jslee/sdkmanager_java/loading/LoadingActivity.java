package com.jslee.sdkmanager_java.loading;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jslee.sdkmanager_java.BaseActivity;
import com.jslee.sdkmanager_java.CaughtExceptionActivity;
import com.jslee.sdkmanager_java.R;
import com.jslee.sdkmanager_java.databinding.ActivityLoadingBinding;
import com.jslee.sdkmanager_java.util.SvgSoftwareLayerSetter;
import com.jslee.sdkmanager_java.util.retrofit.RetrofitHelper;
import com.jslee.sdkmanager_java.version_update.VersionUpdateActivity;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class LoadingActivity extends BaseActivity {
    private LoadingViewModel mViewModel;
    private ActivityLoadingBinding binding;

    private Intent errorMessageIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpBinding();
        setUpObserver();
        setUpView();

        initVariables();
        Log.d("jjslee", "LoadingActivity onCreate");

        startActivity(new Intent(this, VersionUpdateActivity.class));
        finish();
    }

    private void setUpBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        mViewModel = obtainViewModel();
        binding.setLifecycleOwner(this);
        binding.setViewModel(mViewModel);

    }

    private LoadingViewModel obtainViewModel() {
        ViewModelProvider.AndroidViewModelFactory viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        return new ViewModelProvider(this, viewModelFactory).get(LoadingViewModel.class);
    }

    /**
     * @내용 : 로딩이 끝나면 Login 페이지로 이동
     * @최초작성일 : 2021-01-05 오후 6:17
     * @작성자 : 이재선(조원태)
     * @버젼 : 0.0.0
     **/
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
    }
    private void setUpView() {
        RequestBuilder<PictureDrawable> requestBuilder = Glide.with(getApplicationContext())
                .as(PictureDrawable.class)
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new SvgSoftwareLayerSetter());

        requestBuilder.
                load(String.format(String.format(getString(R.string.image_url), getString(R.string.remote_host)), getString(R.string.remote_host)) + getString(R.string.eoc_icon_logo_main_mobile_208)).
                into(binding.loadingIv);


        binding.loadingAppResetTv.setText(R.string.activity_loading_app_reset_txt);
        binding.loadingAppResetTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getConvertDpByRes(10));

    }

    /**
     * @내용 : 앱 구동시 변수 및 설정 값 초기화 함수
     * - Retrofit IP/Context 설정
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2019-12-24 오후 3:37
     * @작성자 : 길용현
     **/
    private void initVariables() {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        retrofitHelper.setContext(getApplicationContext());
        retrofitHelper.setUrl(getString(R.string.remote_host));
        retrofitHelper.settingRetrofit();
    }
}