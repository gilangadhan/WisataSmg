package org.sandec.wisatasmg.networking;

import org.sandec.wisatasmg.helper.Konstanta;
import org.sandec.wisatasmg.model.ListWisataModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by idn on 10/30/2017.
 */

public interface ApiServices {
    @GET(Konstanta.READ_WISATA)
    Call<ListWisataModel> ambilDataWisata();

    @Multipart
    @POST(Konstanta.POST_WISATA)
    Call<ResponseBody> CREATE_WISATA(@Part MultipartBody.Part file,
                                     @Part("nama_wisata") RequestBody nama_wisata,
                                     @Part("gambar_wisata") RequestBody gambar_wisata,
                                     @Part("deksripsi_wisata") RequestBody deksripsi_wisata,
                                     @Part("event_wisata") RequestBody event_wisata,
                                     @Part("longitude_wisata") RequestBody longitude_wisata,
                                     @Part("latitude_wisata") RequestBody latitude_wisata,
                                     @Part("alamat_wisata") RequestBody alamat_wisata);

    @Multipart
    @POST(Konstanta.SIGNUP_USER)
    Call<ResponseBody> SIGNUP_USER(@Part MultipartBody.Part file,
                                     @Part("nama_user") RequestBody nama_user,
                                     @Part("email_user") RequestBody email_user,
                                     @Part("pass_user") RequestBody pass_user,
                                     @Part("gambar_user") RequestBody gambar_user);
    @FormUrlEncoded
    @POST(Konstanta.LOGIN_USER)
    Call<ResponseBody> loginUser(
            @Field("email_user") String email_user,
            @Field("pass_user") String pass_user
    );

    @FormUrlEncoded
    @POST(Konstanta.KUNJUNGAN)
    Call<ResponseBody> KUNJUNGAN(
            @Field("id_user") String id_user,
            @Field("id_wisata") String id_wisata
    );
}
