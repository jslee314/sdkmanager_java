package com.jslee.sdkmanager_java.util;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @작성자 : 길용현
 * @최초작성일 : 2019-09-02 오후 5:59
 * @내용 : DiskIOThread 풀을 생성하는 클래스
 * @수정 :
 * @버젼 : 1.0.0
 **/
public class DiskIOThreadExecutor implements Executor {

    private final Executor mDiskIO;

    public DiskIOThreadExecutor() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIO.execute(command);
    }
}
