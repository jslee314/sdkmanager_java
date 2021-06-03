package com.jslee.sdkmanager_java.version_update;

import android.app.Application;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.jslee.sdkmanager_java.R;
import com.jslee.sdkmanager_java.data.DataSource;
import com.jslee.sdkmanager_java.data.Repository;
import com.jslee.sdkmanager_java.util.FileDownloader;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionUpdateViewModel extends AndroidViewModel
        implements DataSource.CheckNewVersionCallback,
        DataSource.FileDownloaderCallback {

    private Repository repository;
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Throwable> throwException = new MutableLiveData<>();    // throw를 통해 발생시킨 에러(crashlytics에 보낼것)
    private MutableLiveData<Boolean> loadingStatus = new MutableLiveData<>();
    private MutableLiveData<String> currVersionTxt = new MutableLiveData<>();
    private MutableLiveData<String> newVersionTxt = new MutableLiveData<>();
    private String currVersion;
    private String newVersion;
    private MutableLiveData<Boolean> isUpdate = new MutableLiveData<>();
    private MutableLiveData<Boolean> goUpdate = new MutableLiveData<>();

    /**
     * @내용 : APK 파일 다운로드 관련 변수들
     * @작성자 : 길용현 2021-02-20 오후 8:45
     **/
    private FileDownloader fileDownloader;
    private String localAPKFileSavePath;
    private String remoteAPKFileSavePath;
    private MutableLiveData<Integer> progress = new MutableLiveData<>();
    private MutableLiveData<String> progressRate = new MutableLiveData<>();
    private MutableLiveData<String> progressLen = new MutableLiveData<>();
    private PackageManager packageManager;
    private MutableLiveData<Uri> localAPKFileUri = new MutableLiveData<>();

    public VersionUpdateViewModel(@NonNull Application app) {
        super(app);
        repository = Repository.getInstance(app);

        fileDownloader = FileDownloader.getInstance();
        fileDownloader.setContext(app.getApplicationContext());
    }

    public void init(String localAPKFileSavePath, String remoteAPKFileSavePath, String currVersion, PackageManager packageManager) {
        this.currVersion = currVersion;
        this.localAPKFileSavePath = localAPKFileSavePath;
        this.remoteAPKFileSavePath = remoteAPKFileSavePath;
        this.packageManager = packageManager;
    }

    public void checkNewVersion() {
        this.loadingStatus.setValue(true);
        repository.checkNewVersion(this);
    }

    /**
     * @내용 : 최신 버전 체크 후 현재 버전과 비교하는 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 8:20
     * @작성자 : 길용현
     **/
    @Override
    public void onSucceedCheckNewVersion(String newVersion) {
        this.loadingStatus.setValue(false);
        this.newVersion = newVersion;

        try {
            String[] currVersionSplit = currVersion.trim().split("\\.");
            String[] newVersionSplit = newVersion.trim().split("\\.");

            int currMajorVer = Integer.parseInt(currVersionSplit[0]);
            int currMinorVer = Integer.parseInt(currVersionSplit[1]);
            int currPointVer = Integer.parseInt(currVersionSplit[2]);
            int newMajorVer = Integer.parseInt(newVersionSplit[0]);
            int newMinorVer = Integer.parseInt(newVersionSplit[1]);
//            int newPointVer = Integer.parseInt(newVersionSplit[2]);
            int newPointVer = 1;


            boolean isUpdate = false;
            if (currMajorVer < newMajorVer) isUpdate = true;
            if ((currMajorVer == newMajorVer) && (currMinorVer < newMinorVer)) isUpdate = true;
            if ((currMajorVer == newMajorVer) && (currMinorVer == newMinorVer) && (currPointVer < newPointVer)) isUpdate = true;

            this.isUpdate.setValue(isUpdate);

            this.currVersionTxt.setValue(getApplication().getString(R.string.activity_version_curr_ver_txt) + this.currVersion);
            this.newVersionTxt.setValue(getApplication().getString(R.string.activity_version_latest_ver_txt) + this.newVersion);
        } catch (Exception e) {
            this.throwException.setValue(e);
        }
    }

    @Override
    public void onErrorCheckNewVersion(String errorMessage) {
        this.loadingStatus.setValue(false);
        this.errorMessage.setValue(errorMessage);
    }

    /**
     * @내용 : 업데이트 하러가기 버튼을 클릭했을 때 이벤트를 처리하는 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 7:56
     * @작성자 : 길용현
     **/
    public void onUpdateBtnPressed()  {
        if (!TextUtils.isEmpty(localAPKFileSavePath) && !TextUtils.isEmpty(remoteAPKFileSavePath)) {
            loadingStatus.setValue(true);
            this.fileDownloader.deleteDownloadFile(localAPKFileSavePath, this);
        } else {
            this.errorMessage.setValue(getApplication().getString(R.string.activity_version_update_sdcard_not_exist_txt));
        }
    }

    /**
     * @내용 : 미리 받아놓은 APK 파일 삭제에 성공했을 때 다음 작업을 수행하는 콜백 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 9:03
     * @작성자 : 길용현
     **/
    @Override
    public void onCompleteToFileDelete() {
        Log.d("jslee", "onCompleteToFileDelete()");
        loadingStatus.setValue(false);
        this.fileDownloader.httpsFileDownload(this.localAPKFileSavePath, this.remoteAPKFileSavePath, this);
        this.goUpdate.setValue(true);
    }

    /**
     * @내용 : 미리 받아놓은 APK 파일 삭제에 실패했을 때 수행되는 콜백 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 9:04
     * @작성자 : 길용현
     **/
    @Override
    public void onExceptionToFileDelete(Throwable throwable) {
        Log.d("jslee", "onExceptionToFileDelete() " + throwable);
        this.loadingStatus.setValue(false);
        this.throwException.setValue(throwable);
    }

    /**
     * @내용 : 원격지 서버로부터 최신 APK 파일을 성공적으로 다운로드 받았을 때 수행되는 콜백 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 9:04
     * @작성자 : 길용현
     **/
    @Override
    public void onSucceedToFileDownload() {
        Log.d("jslee", "onSucceedToFileDownload()");

        this.goUpdate.setValue(false);
        this.loadingStatus.setValue(true);
        this.fileDownloader.convertApkFileToUri(this.localAPKFileSavePath, this.packageManager, this);
    }

    /**
     * @내용 : 원격지 서버로부터 APK 파일을 다운로드 받는 중 에러가 발생했을 때 수행되는 콜백 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 9:09
     * @작성자 : 길용현
     **/
    @Override
    public void onFailToFileDownload(String errorMessage) {
        Log.d("jslee", "onFailToFileDownload() " + errorMessage);

        this.errorMessage.setValue(errorMessage);
    }

    /**
     * @내용 : 원격지 서버로부터 APK 파일을 다운로드 받는 중 진행도를 출력하기 위한 콜백 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 9:08
     * @작성자 : 길용현
     **/
    @Override
    public void onProgressToFileDownload(int currDownloadLen, int totDownloadLen) {
        this.progress.setValue((int) (((double) currDownloadLen / totDownloadLen) * 100.0));
        this.progressRate.setValue(String.format("%.1f", ((double) currDownloadLen / totDownloadLen) * 100.0) + "%");
        this.progressLen.setValue((String.format("%.1f", (double) currDownloadLen/1024.0/1024.0)) + "/" + (String.format("%.1f", (double) totDownloadLen/1024.0/1024.0)) + " MB");
    }

    /**
     * @내용 : 다운로드 받은 APK 파일을 URI 로 변경 완료 했을 때 수행되는 콜백 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 9:08
     * @작성자 : 길용현
     **/
    @Override
    public void onSucceedToConvertApkFileToUri(Uri localAPKFileUri) {
        Log.d("jslee", "onSucceedToConvertApkFileToUri() " + localAPKFileUri);

        this.loadingStatus.setValue(false);
        this.localAPKFileUri.setValue(localAPKFileUri);
    }

    /**
     * @내용 : 다운로드 받은 APK 파일을 URI 로 변경 실패했을 때 수행되는 콜백 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 9:07
     * @작성자 : 길용현
     **/
    @Override
    public void onFailToConvertApkFileToUri(String errorMessage) {
        Log.d("jslee", "onFailToConvertApkFileToUri() " + errorMessage);

        this.loadingStatus.setValue(false);
        this.errorMessage.setValue(errorMessage);
    }
}
