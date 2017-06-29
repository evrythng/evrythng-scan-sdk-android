package com.evrythng.android.sdk.wrapper.core.api;

import android.support.annotation.NonNull;

import com.evrythng.android.sdk.wrapper.client.service.BaseAPIService;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by phillipcui on 5/29/17.
 */

public class ServiceGenerator {

    private Retrofit retrofit;

    public ServiceGenerator(final BaseAPIService baseService) {
        checkService(baseService);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", baseService.getClient().getApiKey());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        generateRetrofitObject(baseService.getClient().getUrl(), interceptor);
    }

    public ServiceGenerator(@NonNull String url, @NonNull final String apiKey, @NonNull final Map<String, String> additionalHeaders) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", apiKey);

                if(additionalHeaders != null) {
                    for (Map.Entry<String, String> headers : additionalHeaders.entrySet()) {
                        requestBuilder.addHeader(headers.getKey(), headers.getValue());
                    }
                }
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
        generateRetrofitObject(url, interceptor);
    }

    private void generateRetrofitObject(String url, Interceptor interceptor) {
        // add logging as last interceptor
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        //add interceptor if not null
        if(interceptor != null)
            httpClient.addInterceptor(interceptor);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // set your desired log level
        httpClient.addInterceptor(logging);

        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(GsonModule.getInstance().getGson()))
                .client(client)
                .build();
    }

    private void checkService(BaseAPIService baseService) {
        if(baseService == null)
            throw new IllegalStateException("ServiceGenerator: Service should not be null");

        if(baseService.getClient() == null)
            throw new IllegalStateException("ServiceGenerator: EVTApiClient should not be null");
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public ApiService createService() {
        return retrofit.create(ApiService.class);
    }
}
