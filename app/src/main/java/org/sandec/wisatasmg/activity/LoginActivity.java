package org.sandec.wisatasmg.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.helper.Konstanta;
import org.sandec.wisatasmg.helper.SessionManager;
import org.sandec.wisatasmg.networking.ApiServices;
import org.sandec.wisatasmg.networking.RetrofitConfig;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SessionManager {

    EditText edtUsername;
    EditText edtPassword;
    TextView txtLupaPassword;
    Button btnLogin;
    TextView txtDaftar;
    String strusername, strpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //to remove the action bar (title bar)
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        txtLupaPassword = findViewById(R.id.txtLupaPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtDaftar = findViewById(R.id.txtDaftar);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strusername = edtUsername.getText().toString();
                strpassword = edtPassword.getText().toString();
                if (TextUtils.isEmpty(strusername)) {
                    edtUsername.setError("username tidak boleh kosong");
                } else if (TextUtils.isEmpty(strpassword)) {
                    edtPassword.setError("password tidak boleh kosong");
                } else if (strpassword.length() < 6) {
                    edtPassword.setError("minimal password 6 karakter");
                } else {
                    loginuser();
                }
            }
        });
        txtDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myIntent(SignupActivity.class);
            }
        });
    }

    private void setupUI() {

    }

    private void loginuser() {
        showProgressDialog("proses login user");
        ApiServices api = RetrofitConfig.getApiServices();
        Call<ResponseBody> modelUserCall = api.loginUser(strusername, strpassword);

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
                                JSONArray jsonArray = object.getJSONArray("user");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String id_user = jsonObject.getString(Konstanta.ID_USER);
                                    String nama_user = jsonObject.getString(Konstanta.NAMA_USER);
                                    String email_user = jsonObject.getString(Konstanta.EMAIL_USER);
                                    String pass_user = jsonObject.getString(Konstanta.PASS_USER);
                                    String gambar = jsonObject.getString(Konstanta.GAMBAR_USER);
                                    myToast(id_user);
                                    myIntent(MenuActivity.class);
                                    sessionManager.createSession(strusername);
                                    sessionManager.setUser(id_user, nama_user, email_user, pass_user, gambar);
                                    finish();
                                }
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
}
