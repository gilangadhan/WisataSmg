package org.sandec.wisatasmg.networking;

import org.sandec.wisatasmg.helper.Konstanta;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by idn on 10/30/2017.
 */

public class RetrofitConfig {
    private static Retrofit getRetrofit(){
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Konstanta.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();

        return retrofit;
    }

    public static ApiServices getApiServices(){
        return getRetrofit().create(ApiServices.class);
    }
}
