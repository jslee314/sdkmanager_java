package com.jslee.sdkmanager_java.data;

import android.net.Uri;

public interface DataSource {

    /**
     * @내용 : 최신 앱 버전을 체크하는 데 사용되는 메소드/인터페이스
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 4:21
     * @작성자 : 길용현
     **/
    void checkNewVersion(CheckNewVersionCallback callback);
    interface CheckNewVersionCallback {
        void onSucceedCheckNewVersion(String newVersion);
        void onErrorCheckNewVersion(String errorMessage);
    }

    /**
     * @내용 : APK 배포 관련 콜백 함수를 처리하는 인터페이스
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 8:24
     * @작성자 : 길용현
     **/
    interface FileDownloaderCallback {
        void onCompleteToFileDelete();
        void onExceptionToFileDelete(Throwable throwable);
        void onSucceedToFileDownload();
        void onFailToFileDownload(String errorMessage);
        void onProgressToFileDownload(int currDownloadLen, int totDownloadLen);
        void onSucceedToConvertApkFileToUri(Uri localApkUri);
        void onFailToConvertApkFileToUri(String errorMessage);
    }

}

