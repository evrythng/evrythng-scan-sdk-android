package com.evrythng.android.sdk.wrapper.core.api;

import com.evrythng.android.sdk.model.Credentials;
import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanResponse;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by phillipcui on 5/29/17.
 */

public interface ApiService {

    @GET("/scan/identifications")
    Call<List<ScanResponse>> identify(@Query("filter") String filter);

    @POST("/auth/evrythng/users?anonymous=true")
    Call<User> createAnonymousUser(@Body Credentials anonymous);

    @POST("/auth/evrythng")
    Call<User> loginUser(@Body Credentials credentials);

    @POST("/auth/evrythng/users")
    Call<User> createUser(@Body User user);

    @POST("/auth/evrythng/users/{user_id}/validate")
    Call<User> validateUser(@Path("user_id") String userID, @Body User user);

    @POST("/auth/all/logout")
    Call<User> logoutUser();
}
