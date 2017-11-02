package org.sandec.wisatasmg.networking;

import org.sandec.wisatasmg.model.ListWisataModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by idn on 10/30/2017.
 */

public interface ApiServices {
    @GET("read_wisata.php")
    Call<ListWisataModel> ambilDataWisata();

    @Multipart
    @POST("create_wisata.php")
    Call<ResponseBody> CREATE_WISATA(@Part MultipartBody.Part file,
                                     @Part("nama_wisata") RequestBody nama_wisata,
                                     @Part("gambar_wisata") RequestBody gambar_wisata,
                                     @Part("deksripsi_wisata") RequestBody deksripsi_wisata,
                                     @Part("event_wisata") RequestBody event_wisata,
                                     @Part("longitude_wisata") RequestBody longitude_wisata,
                                     @Part("latitude_wisata") RequestBody latitude_wisata,
                                     @Part("alamat_wisata") RequestBody alamat_wisata);

}
