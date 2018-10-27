package com.ashokvarma.gander.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Class description
 *
 * @author ashok
 * @version 1.0
 * @since 02/06/18
 */
class SampleApiService {

    static HttpbinApi getInstance(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://httpbin.org")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(HttpbinApi.class);
    }

    static class Data {
        final String thing;
        final List<String> things = new ArrayList<>();
        final HashMap<String, Integer> thingsMap = new HashMap<>();

        Data(String thing) {
            this.thing = thing;
            int i = 10;
            while (i > 0) {
                things.add(thing + "_" + i);
                thingsMap.put(thing + "_key_" + i, i);
                i--;
            }
        }
    }

    // estimated size 60 MB
    static class VeryLargeData {
        final String data;

        VeryLargeData() {

            StringBuilder stringBuilder = new StringBuilder(20_000_000);
            for (int i = 1; i < 200_000; i++) {
                stringBuilder.append("The quick brown fox jumps over the lazy dog over and over again many times,100 word sentence formed.");
            }
            this.data = stringBuilder.toString();
        }
    }

    interface HttpbinApi {
        @GET("/get")
        Call<Void> get();

        @POST("/post")
        Call<Void> post(@Body Data body);

        @POST("/post")
        Call<Void> post(@Body VeryLargeData body);

        @POST("/post")
        @FormUrlEncoded
        @Headers({
                "ContentType: application/x-www-form-urlencoded",
        })
        Call<Void> postForm(@Field("param_string") String string, @Field("param_string_null") String stringNil, @Field("param_double") double param2, @Field("param_int") int param3, @Field("param_bool") boolean param4);

        @PATCH("/patch")
        Call<Void> patch(@Body Data body);

        @PUT("/put")
        @Headers({
                "Cache-Control: max-age=640000",
                "Library: Gander",
                "Client: Sample",
                "X-Foo: Bar",
                "X-Ping: Pong"
        })
        Call<Void> put(@Body Data body);

        @DELETE("/delete")
        Call<Void> delete();

        @GET("/status/{code}")
        Call<Void> status(@Path("code") int code);

        @GET("/stream/{lines}")
        Call<Void> stream(@Path("lines") int lines);

        @GET("/stream-bytes/{bytes}")
        Call<Void> streamBytes(@Path("bytes") int bytes);

        @GET("/delay/{seconds}")
        Call<Void> delay(@Path("seconds") int seconds);

        @GET("/redirect-to")
        Call<Void> redirectTo(@Query("url") String url);

        @GET("/redirect/{times}")
        Call<Void> redirect(@Path("times") int times);

        @GET("/relative-redirect/{times}")
        Call<Void> redirectRelative(@Path("times") int times);

        @GET("/absolute-redirect/{times}")
        Call<Void> redirectAbsolute(@Path("times") int times);

        @GET("/image")
        Call<Void> image(@Header("Accept") String accept);

        @GET("/gzip")
        Call<Void> gzip();

        @GET("/xml")
        Call<Void> xml();

        @GET("/encoding/utf8")
        Call<Void> utf8();

        @GET("/deflate")
        Call<Void> deflate();

        @GET("/cookies/set")
        Call<Void> cookieSet(@Query("k1") String value);

        @GET("/basic-auth/{user}/{passwd}")
        Call<Void> basicAuth(@Path("user") String user, @Path("passwd") String passwd);

        @GET("/drip")
        Call<Void> drip(@Query("numbytes") int bytes, @Query("duration") int seconds, @Query("delay") int delay, @Query("code") int code);

        @GET("/deny")
        Call<Void> deny();

        @GET("/cache")
        Call<Void> cache(@Header("If-Modified-Since") String ifModifiedSince);

        @GET("/cache/{seconds}")
        Call<Void> cache(@Path("seconds") int seconds);
    }
}