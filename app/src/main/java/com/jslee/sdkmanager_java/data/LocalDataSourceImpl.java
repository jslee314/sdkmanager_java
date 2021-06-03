package com.jslee.sdkmanager_java.data;

import androidx.annotation.NonNull;

import com.jslee.sdkmanager_java.util.AppExecutors;


public class LocalDataSourceImpl implements DataSource {
    private static volatile LocalDataSourceImpl INSTANCE;
    private AppExecutors mAppExecutors;

    private LocalDataSourceImpl(@NonNull AppExecutors mAppExecutors) {
        this.mAppExecutors = mAppExecutors;
    }

    /**
     * @작성자 : 길용현
     * @최초작성일 : 2019-09-04 오후 6:03
     * @내용 : DCL 방식으로 ManualIrisAnalysisLocalDataSourceImpl 인스턴스를 초기화하는 함수
     * @수정 :
     * @버젼 : 1.0.0
     **/
    public static LocalDataSourceImpl getInstance(
            AppExecutors mAppExecutors) {
        if (INSTANCE == null) {
            synchronized (LocalDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSourceImpl(mAppExecutors);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void checkNewVersion(CheckNewVersionCallback callback) {

    }


}
