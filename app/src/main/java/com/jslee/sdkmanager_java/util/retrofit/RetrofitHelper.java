package com.jslee.sdkmanager_java.util.retrofit;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.jslee.sdkmanager_java.CaughtExceptionActivity;
import com.jslee.sdkmanager_java.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import lombok.Setter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static volatile RetrofitHelper INSTANCE;
    private Context context;
    @Setter private String url;

    private Retrofit retrofit;
    private Retrofit.Builder retrofitBuilder;
    private OkHttpClient.Builder okHttpClientBuilder;

    private RetrofitHelper() {}

    public void settingRetrofit() {

            try {
                okHttpClientBuilder = getSafeOkHttpClient();
            } catch (Exception e) {
                Intent intent = new Intent(this.context, CaughtExceptionActivity.class);
                intent.putExtra("errorMessage", e.getMessage());
                context.startActivity(intent);
            }


        retrofitBuilder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static RetrofitHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitHelper();
                }
            }
        }
        return INSTANCE;
    }

    public RetrofitInterface getServer() {
        return getServer(null);
    }

    public RetrofitInterface getServer(String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

            if (!okHttpClientBuilder.interceptors().contains(interceptor)) {
                okHttpClientBuilder.addInterceptor(interceptor);
            }
        }

        retrofitBuilder.client(okHttpClientBuilder.build());
        retrofit = retrofitBuilder.build();

        return retrofit.create(RetrofitInterface.class);
    }

    private KeyStore getKeyStore() throws NoSuchAlgorithmException, IOException, KeyStoreException, Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caIs = context.getResources().openRawResource(R.raw.eyeoclockm);
        Certificate ca = cf.generateCertificate(caIs);

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        if (ca == null) {
            return null;
        }
        keyStore.setCertificateEntry("ca", ca);
        return keyStore;
    }

    private SSLSocketFactory getPinnedCertSSLSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, Exception {
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(getKeyStore());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    private OkHttpClient.Builder getSafeOkHttpClient() throws NoSuchAlgorithmException, KeyStoreException, Exception {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        x509TrustManager x509TrustManager = new x509TrustManager(getKeyStore());

        return builder.sslSocketFactory(Objects.requireNonNull(getPinnedCertSSLSocketFactory()), Objects.requireNonNull(x509TrustManager))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
    }

    public void setContext(Context context) {
        synchronized (this) {
            if (this.context == null) this.context = context;
        }
    }
}
