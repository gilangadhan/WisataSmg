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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;
import org.sandec.wisatasmg.R;
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

public class CreateWisataActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ImageView imageView;
    EditText edtNama;
    TextInputLayout inputLayoutNama;
    EditText edtDeskripsi;
    TextInputLayout inputLayoutDeskripsi;
    EditText edtAlamat;
    TextInputLayout inputLayoutAlamat;
    EditText edtEvent;
    TextInputLayout inputLayoutEvent;
    Button btnMaps;
    TextView statusMaps;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wisata);
        setupUI();
        loading = new ProgressDialog(CreateWisataActivity.this);
        loading.setIndeterminate(true);
        loading.setCancelable(false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryPermissionDialog();
            }
        });
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(CreateWisataActivity.this), 2);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    private void setupUI() {
        imageView = findViewById(R.id.imageView);
        edtNama = findViewById(R.id.edt_nama);
        inputLayoutNama = findViewById(R.id.input_layout_nama);
        edtDeskripsi = findViewById(R.id.edt_deskripsi);
        inputLayoutDeskripsi = findViewById(R.id.input_layout_deskripsi);
        edtAlamat = findViewById(R.id.edt_alamat);
        inputLayoutAlamat = findViewById(R.id.input_layout_alamat);
        inputLayoutEvent = findViewById(R.id.input_layout_event);
        btnMaps = findViewById(R.id.btn_maps);
        statusMaps = findViewById(R.id.status_maps);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void check() {
        if (TextUtils.isEmpty(edtNama.getText().toString())) {
            edtNama.setError("Nama Masih Kosong");
        } else if (TextUtils.isEmpty(edtDeskripsi.getText().toString())) {
            edtDeskripsi.setError("Deksripsi Masih Kosong");
        } else if (TextUtils.isEmpty(edtAlamat.getText().toString())) {
            edtAlamat.setError("Alamat Masih Kosong");
        } else if (TextUtils.isEmpty(edtEvent.getText().toString())) {
            edtEvent.setError("Event Masih Kosong");
        } else if (uri == null) {
            Toast.makeText(getApplicationContext(), "Silahkan pilih gambar ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(statusMaps.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Tentukan Lokasi anda", Toast.LENGTH_SHORT).show();
        } else {

            submit();
        }
    }

    ProgressDialog loading;

    private void submit() {
        String path = null;
        if (Build.VERSION.SDK_INT < 11) {
            path = RealPathUtils.getRealPathFromURI_BelowAPI11(CreateWisataActivity.this, uri);

            // SDK >= 11 && SDK < 19
        } else if (Build.VERSION.SDK_INT < 19) {
            path = RealPathUtils.getRealPathFromURI_API11to18(CreateWisataActivity.this, uri);

            // SDK > 19 (Android 4.4)
        } else if (Build.VERSION.SDK_INT > 22) {
            path = RealPathUtils.getRealPathFromURI_API23(CreateWisataActivity.this, uri);

        } else {
            path = RealPathUtils.getRealPathFromURI_API19(CreateWisataActivity.this, uri);

        }

        String nama = edtNama.getText().toString().trim();
        String deskripsi = edtDeskripsi.getText().toString().trim();
        String event = edtEvent.getText().toString().trim();
        String alamat = edtAlamat.getText().toString().trim();

        File file = new File(path);
        String gambar = currentDate() + "-" + file.getName();

        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", gambar, mFile);
        RequestBody snama = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody sdeskripsi = RequestBody.create(MediaType.parse("text/plain"), deskripsi);
        RequestBody sevent = RequestBody.create(MediaType.parse("text/plain"), event);
        RequestBody salamat = RequestBody.create(MediaType.parse("text/plain"), alamat);
        RequestBody sgambar = RequestBody.create(MediaType.parse("text/plain"), gambar);
        RequestBody slat = RequestBody.create(MediaType.parse("text/plain"), lat);
        RequestBody slng = RequestBody.create(MediaType.parse("text/plain"), lng);

//        Toast.makeText(getApplicationContext(), "nama :" + nama +
//                        "\ndeskripsi :"+ deskripsi +"\nevent :"+ event +
//                        "\ngambar :"+ gambar +  "\nalamat :"+ alamat +
//                        "\nlng :"+ lng +"\nlat :"+ lat
//                , Toast.LENGTH_SHORT).show();
        loading = ProgressDialog.show(CreateWisataActivity.this, null, "Loading . . .", true, false);
        loading.show();
        ApiServices mApiService = RetrofitConfig.getApiServices();
        mApiService.CREATE_WISATA(fileToUpload, snama, sgambar, sdeskripsi, sevent, slng, slat, salamat).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("debug", "On Respon : " + response.body().toString());
                    loading.dismiss();
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getString("success").equals("true")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CreateWisataActivity.this);
                            builder.setTitle("Complete");
                            builder.setMessage(object.getString("message"));
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    Intent a = new Intent(getApplicationContext(), MenuActivity.class);
                                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(a);
                                    finish();
                                }
                            });
                            builder.create().show();
                        } else {
                            String error = object.getString("message");
                            Toast.makeText(CreateWisataActivity.this, error, Toast.LENGTH_SHORT).show();
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

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(CreateWisataActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateWisataActivity.this,
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, CreateWisataActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (uri != null) {
            Upload(uri);
        }
    }

    private void Upload(Uri uri) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(" ", "Permission has been denied");
    }

    String lat = "";
    String lng = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {

            uri = imageReturnedIntent.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("TAG", String.valueOf(bitmap));


                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(imageReturnedIntent, CreateWisataActivity.this);
            String information = String.format("%s", place.getName());
            lat = String.valueOf(place.getLatLng().latitude);
            lng = String.valueOf(place.getLatLng().longitude);
            statusMaps.setText(information);
        }
    }

}
