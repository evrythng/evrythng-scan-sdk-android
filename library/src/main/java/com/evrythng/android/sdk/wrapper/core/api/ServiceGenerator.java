package com.evrythng.android.sdk.wrapper.core.api;

import com.evrythng.android.sdk.wrapper.client.service.BaseService;

import java.io.IOException;

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

    private final Retrofit retrofit;

    public ServiceGenerator(final BaseService baseService) {
        checkService(baseService);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", baseService.getClient().getApiKey());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        // add logging as last interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // set your desired log level
        httpClient.addInterceptor(logging);

        OkHttpClient client = httpClient.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseService.getClient().getUrl())
                .addConverterFactory(GsonConverterFactory.create(GsonModule.getInstance().getGson()))
                .client(client)
                .build();
    }

    private void checkService(BaseService baseService) {
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
