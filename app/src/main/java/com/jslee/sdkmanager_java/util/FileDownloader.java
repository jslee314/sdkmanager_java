package com.jslee.sdkmanager_java.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.jslee.sdkmanager_java.R;
import com.jslee.sdkmanager_java.data.DataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import lombok.Setter;

public class FileDownloader {
    private AppExecutors appExecutors;
    @Setter private Context context;

    public FileDownloader() {
        appExecutors = AppExecutors.getInstance();
    }

    private static class SingleTonHolder {
        static final FileDownloader INSTANCE = new FileDownloader();
    }

    public static FileDownloader getInstance() {
        return SingleTonHolder.INSTANCE;
    }

    /**
     * @내용 : 어플리케이션 내부 스토리지에 다운로드 받은 파일이 존재하는 경우 삭제하는 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 8:49
     * @작성자 : 길용현
     **/
    public void deleteDownloadFile(String localAPKFileSavePath, DataSource.FileDownloaderCallback callback) {
        Runnable runnable = () -> {
            try {
                File file = new File(localAPKFileSavePath);

                if (!file.exists()) {
                    file.mkdirs();
                } else {
                    file.delete();
                }

                appExecutors.getMainThread().execute(callback::onCompleteToFileDelete);
            } catch (Exception e) {
                appExecutors.getNetworkThread().execute(() -> {
                    callback.onExceptionToFileDelete(e);
                });
            }
        };

        appExecutors.getDiskThread().execute(runnable);
    }

    /**
     * @내용 : 원격지 서버에 있는 APK 파일을 다운로드하는 함수 (http)
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 8:49
     * @작성자 : 길용현
     **/
    public void httpFileDownload(String localAPKFileSavePath, String remoteAPKFileSavePath, DataSource.FileDownloaderCallback callback) {
        Runnable runnable = () -> {
            InputStream is = null;
            FileOutputStream fos = null;
            HttpURLConnection conn = null;

            try {
                URL apkUrl = new URL(remoteAPKFileSavePath);
                conn = (HttpURLConnection) apkUrl.openConnection();
                conn.setRequestProperty("key", context.getString(R.string.auth_token));
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.connect();

                int downloadLen = 0;
                final int totDownloadLen = conn.getContentLength();
                byte[] tmpByte = new byte[1024];

                is = conn.getInputStream();
                fos = new FileOutputStream(new File(localAPKFileSavePath));
                int bufferNum = 0;
                while ((bufferNum = is.read(tmpByte)) != -1) {
                    fos.write(tmpByte, 0, bufferNum);

                    downloadLen += bufferNum;
                    final int currDownloadLen = downloadLen;

                    appExecutors.getMainThread().execute(() -> {
                        callback.onProgressToFileDownload(currDownloadLen, totDownloadLen);
                    });
                }

                appExecutors.getMainThread().execute(() -> {
                    callback.onSucceedToFileDownload();
                });

            } catch (Exception e) {
                appExecutors.getMainThread().execute(() -> {
                    callback.onFailToFileDownload(e.getMessage());
                });
            } finally {
                try {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                    if (conn != null) conn.disconnect();
                } catch (Exception e) {
                    appExecutors.getMainThread().execute(() -> {
                        callback.onFailToFileDownload(e.getMessage());
                    });
                }
            }
        };

        appExecutors.getNetworkThread().execute(runnable);
    }

    /**
     * @내용 : 원격지 서버에 있는 APK 파일을 다운로드하는 함수 (https)
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 8:49
     * @작성자 : 길용현
     **/
    public void httpsFileDownload(String localAPKFileSavePath, String remoteAPKFileSavePath, DataSource.FileDownloaderCallback callback) {
        Runnable runnable = () -> {
            InputStream is = null;
            FileOutputStream fos = null;
            HttpsURLConnection conn = null;

            try {
                URL apkUrl = new URL(remoteAPKFileSavePath);
                conn = (HttpsURLConnection) apkUrl.openConnection();
                conn.setRequestProperty("key", context.getString(R.string.auth_token));
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.connect();

                int downloadLen = 0;
                final int totDownloadLen = conn.getContentLength();
                byte[] tmpByte = new byte[1024];

                is = conn.getInputStream();
                fos = new FileOutputStream(new File(localAPKFileSavePath));
                int bufferNum = 0;

                while ((bufferNum = is.read(tmpByte)) != -1) {
                    fos.write(tmpByte, 0, bufferNum);

                    downloadLen += bufferNum;

                    final int currDownloadLen = downloadLen;

                    appExecutors.getMainThread().execute(() -> {
                        callback.onProgressToFileDownload(currDownloadLen, totDownloadLen);
                    });
                }

                appExecutors.getMainThread().execute(() -> {
                    callback.onSucceedToFileDownload();
                });

            } catch (Exception e) {
                Log.d("kyh", "bbbbbbbb" + e.getMessage());
                appExecutors.getMainThread().execute(() -> {
                    callback.onFailToFileDownload(e.getMessage());
                });
            } finally {
                try {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                    if (conn != null) conn.disconnect();
                } catch (Exception e) {
                    appExecutors.getMainThread().execute(() -> {
                        callback.onFailToFileDownload(e.getMessage());
                    });
                }
            }
        };

        appExecutors.getNetworkThread().execute(runnable);
    }

    /**
     * @내용 : 다운로드 받은 APK 파일을 FileProvider 를 사용해 Uri 로 변환하는 함수
     * @수정 :
     * @버젼 : 0.0.0
     * @최초작성일 : 2021-02-20 오후 8:49
     * @작성자 : 길용현
     **/
    public void convertApkFileToUri(String localAPKFileSavePath, PackageManager packageManager, DataSource.FileDownloaderCallback callback) {
        Runnable runnable = () -> {
            try {
                PackageInfo pi = packageManager.getPackageArchiveInfo(localAPKFileSavePath, PackageManager.GET_ACTIVITIES);

                if (pi == null) {
                    callback.onFailToConvertApkFileToUri("Package could not be parsed.");
                }

                File localApkFile = new File(localAPKFileSavePath);

                Uri localApkUri = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  // API 24 이상
                    localApkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", localApkFile);
                } else {
                    localApkUri = Uri.fromFile(localApkFile);
                }

                final Uri finalLocalApkFile = localApkUri;

                appExecutors.getMainThread().execute(() -> {  // 익명 클래스의 인스턴스로 넘겨지는 모든 변수들은 final 이어야 하므로 분기문마다 callback 메서드를 호출
                    callback.onSucceedToConvertApkFileToUri(finalLocalApkFile);
                });
            } catch (Exception e) {
                appExecutors.getMainThread().execute(() -> {
                    callback.onFailToConvertApkFileToUri(e.getMessage());
                });
            }
        };

        appExecutors.getDiskThread().execute(runnable);
    }
}
