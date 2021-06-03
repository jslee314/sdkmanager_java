/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jslee.sdkmanager_java.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.Getter;

/**
 * @작성자 : 길용현
 * @최초작성일 : 2019-09-02 오후 5:24
 * @내용 : Application 에서 사용하는 쓰레드들을 쓰레드 풀로 관리하는 클래스
 *          MainThread: 1개, DiskIOThread : 1개, NetworkIOThread: 3개
 * @수정 :
 * @버젼 : 1.0.0
 **/
public class AppExecutors {

    private static final int THREAD_COUNT = 3;

    @Getter private final Executor diskThread;
    @Getter private final Executor networkThread;
    @Getter private final Executor taskThread;
    @Getter private final Executor mainThread;

    @VisibleForTesting
    private AppExecutors(Executor diskThread,
                         Executor networkThread,
                         Executor taskThread,
                         Executor mainThread) {
        this.diskThread = diskThread;
        this.networkThread = networkThread;
        this.taskThread = taskThread;
        this.mainThread = mainThread;
    }

    private AppExecutors() {
        this(new DiskIOThreadExecutor(),
                Executors.newFixedThreadPool(THREAD_COUNT),
                Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

    private static class SingleToneHolder {
        static final AppExecutors instance = new AppExecutors();
    }

    public static AppExecutors getInstance() {
        return AppExecutors.SingleToneHolder.instance;
    }

       private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
