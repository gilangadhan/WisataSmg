package org.sandec.wisatasmg.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.database.DatabaseHelper;
import org.sandec.wisatasmg.helper.Konstanta;
import org.sandec.wisatasmg.helper.MyFuction;
import org.sandec.wisatasmg.helper.SessionManager;
import org.sandec.wisatasmg.networking.ApiServices;
import org.sandec.wisatasmg.networking.RetrofitConfig;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailWisataActivity extends MyFuction {

    //logt
    private static final String TAG = "DetailWisataActivity";

    String dataId, dataNama, dataAlamat, dataDeskripsi, dataGambar, idUser, datakunjungan;
    Boolean isFavorit;
    SharedPreferences pref;
    FloatingActionButton fab;
    DatabaseHelper database = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wisata);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //terima data
        dataNama = getIntent().getExtras().getString(Konstanta.DATA_NAMA);
        dataId = getIntent().getExtras().getString(Konstanta.DATA_ID);
        dataAlamat = getIntent().getExtras().getString(Konstanta.DATA_ALAMAT);
        dataDeskripsi = getIntent().getExtras().getString(Konstanta.DATA_DESKRIPSI);
        dataGambar = getIntent().getExtras().getString(Konstanta.DATA_GAMBAR);
        datakunjungan = getIntent().getExtras().getString(Konstanta.KUNJUNGAN);

        SessionManager manager = new SessionManager(getApplicationContext());
        idUser = manager.getIdUser();

        //logd untuk menampilkan di logcat
        Log.d(TAG, "onCreate: " + dataNama + dataGambar + dataDeskripsi + dataAlamat);

        //ambil data dari sharedpreference
        pref = getSharedPreferences(Konstanta.SETTING, MODE_PRIVATE);
        isFavorit = pref.getBoolean(Konstanta.TAG_PREF + dataNama, false);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        cekFavorit(isFavorit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //simpan ke favorit jika pref false

                if (isFavorit) {
                    //jika love penuh
                    //hapus dari sqlite
                    long id = database.delete(dataNama);

                    if (id <= 0) {
                        Snackbar.make(view, "Favorit gagal dihapus dari database", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "Favorit dihapus dari database", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        //bikin false
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(Konstanta.TAG_PREF + dataNama, false);
                        editor.commit();
                        isFavorit = false;
                    }

                } else {
                    //jika love kosong
                    //simpan ke sqlite
                    long id = database.insertData(dataNama, dataGambar, dataAlamat, dataDeskripsi, "12.232", "3.212", datakunjungan );

                    Log.d(TAG, "id kembalian: " + id);
                    if (id <= 0) {
                        Snackbar.make(view, "Favorit gagal ditambahkan ke database", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "Favorit ditambahkan ke database", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        //bikin true
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(Konstanta.TAG_PREF + dataNama, true);
                        editor.commit();
                        isFavorit = true;
                    }

                }

                cekFavorit(isFavorit);

            }
        });


        TextView tvAlamat = (TextView) findViewById(R.id.tv_detail_alamat);
        TextView tvDeskripsi = (TextView) findViewById(R.id.tv_detail_deskripsi);
        ImageView ivGambar = (ImageView) findViewById(R.id.iv_detail_gambar);

        tvAlamat.setText(dataAlamat);
        tvDeskripsi.setText(dataDeskripsi);
        Glide.with(DetailWisataActivity.this).load(Konstanta.WISATA_URL + dataGambar).into(ivGambar);

        getSupportActionBar().setTitle(dataNama);


        AlertDialog.Builder builder = new AlertDialog.Builder(DetailWisataActivity.this);
        builder.setTitle("Peringatan");
        builder.setMessage("Apakah saat ini anda berkunjung pada lokasi wisata ini?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                update();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void update() {
        showProgressDialog("Loading");
        ApiServices api = RetrofitConfig.getApiServices();
        Call<ResponseBody> modelUserCall = api.KUNJUNGAN(idUser, dataId);

        modelUserCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        Log.i("debug", "On Respon : Berhasil ");
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            String error = object.getString("message");
                            if (object.getString("success").equals("true")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailWisataActivity.this);
                                builder.setTitle("Complete");
                                builder.setMessage(object.getString("message"));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            } else {
                                Log.i("debug", "On Respon : " + error);
                                // Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                                myToast(error);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    } else {
                        Log.i("debug", "On Respon : Gagal");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressDialog();
                myToast("gagal koneksi :" + t.getMessage());
            }
        });
    }

    private void cekFavorit(Boolean isFavorit) {
        //kalau true image favorit
        //kalau false image notfavorit
        if (isFavorit) {
            fab.setImageResource(R.drawable.ic_action_isfavorit);
        } else {
            fab.setImageResource(R.drawable.ic_action_isnotfavorit);
        }
    }
}
