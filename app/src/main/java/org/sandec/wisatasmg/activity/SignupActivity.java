package org.sandec.wisatasmg.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.helper.MyFuction;
import org.sandec.wisatasmg.helper.RealPathUtils;
import org.sandec.wisatasmg.networking.ApiServices;
import org.sandec.wisatasmg.networking.RetrofitConfig;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends MyFuction implements EasyPermissions.PermissionCallbacks {

    ImageView tambah;
    EditText edtNama;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtRePassword;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupUI();

        loading = new ProgressDialog(SignupActivity.this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryPermissionDialog();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    private void setupUI() {
        tambah = findViewById(R.id.tambah);
        edtNama = findViewById(R.id.edtNama);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        btnSignup = findViewById(R.id.btnSignup);
    }


    private void check() {
        if (TextUtils.isEmpty(edtNama.getText().toString())) {
            edtNama.setError("Nama Masih Kosong");
        } else if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            edtEmail.setError("Email Masih Kosong");
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            edtPassword.setError("Password Masih Kosong");
        } else if (TextUtils.isEmpty(edtRePassword.getText().toString())) {
            edtRePassword.setError("Password Masih Kosong");
        } else if (!edtRePassword.getText().toString().equals(edtRePassword.getText().toString())) {
            edtRePassword.setError("Password Tidak Sama");
        } else if (uri == null) {
            Toast.makeText(getApplicationContext(), "Silahkan pilih gambar ", Toast.LENGTH_SHORT).show();
        } else {
//            String nama = edtNama.getText().toString().trim();
//            String email = edtEmail.getText().toString().trim();
//            String password = edtPassword.getText().toString().trim();
//            myToast( nama + " " + email + " " + password);
            submit();
        }
    }

    ProgressDialog loading;

    private void submit() {
        String path = null;
        if (Build.VERSION.SDK_INT < 11) {
            path = RealPathUtils.getRealPathFromURI_BelowAPI11(SignupActivity.this, uri);

            // SDK >= 11 && SDK < 19
        } else if (Build.VERSION.SDK_INT < 19) {
            path = RealPathUtils.getRealPathFromURI_API11to18(SignupActivity.this, uri);

            // SDK > 19 (Android 4.4)
        } else if (Build.VERSION.SDK_INT > 22) {
            path = RealPathUtils.getRealPathFromURI_API19(SignupActivity.this, uri);

        } else {
            path = RealPathUtils.getRealPathFromURI_API19(SignupActivity.this, uri);
        }

        String nama = edtNama.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        File file = new File(path);
        String gambar = currentDate() + "-" + file.getName();

        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", gambar, mFile);
        RequestBody snama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody sgambar = RequestBody.create(MediaType.parse("text/plain"), gambar);
        RequestBody semail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody spassword = RequestBody.create(MediaType.parse("text/plain"), password);

//        Toast.makeText(getApplicationContext(), "nama :" + nama +
//                        "\ndeskripsi :"+ deskripsi +"\nevent :"+ event +
//                        "\ngambar :"+ gambar +  "\nalamat :"+ alamat +
//                        "\nlng :"+ lng +"\nlat :"+ lat
//                , Toast.LENGTH_SHORT).show();
        loading = ProgressDialog.show(SignupActivity.this, null, "Loading . . .", true, false);
        loading.show();
        ApiServices mApiService = RetrofitConfig.getApiServices();
        mApiService.SIGNUP_USER(fileToUpload, snama, semail, spassword, sgambar).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("debug", "On Respon : " + response.body().toString());
                    loading.dismiss();
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getString("success").equals("true")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            builder.setTitle("Complete");
                            builder.setMessage(object.getString("message"));
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    Intent a = new Intent(getApplicationContext(), LoginActivity.class);
                                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(a);
                                    finish();
                                }
                            });
                            builder.create().show();
                        } else {
                            String error = object.getString("message");
                            myToast(error);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("debug", "On Respon : Gagal");
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loading.dismiss();
                Log.e("debug", "OnFailure : Error > " + t.getMessage());
            }
        });
    }

    public String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private int PICK_IMAGE_REQUEST = 1;
    Uri uri = null;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    void galleryPermissionDialog() {

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(SignupActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SignupActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;

        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, SignupActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (uri != null) {
            galleryPermissionDialog();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(" ", "Permission has been denied");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {

            uri = imageReturnedIntent.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("TAG", String.valueOf(bitmap));


                tambah.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
