package com.jslee.sdkmanager_java.loading;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadingViewModel extends AndroidViewModel {

//    private Repository repository;

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Throwable> throwException = new MutableLiveData<>();    // throw를 통해 발생시킨 에러(crashlytics에 보낼것)


    public LoadingViewModel(@NonNull Application app) {
        super(app);
//        this.repository = Repository.getInstance(app);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
