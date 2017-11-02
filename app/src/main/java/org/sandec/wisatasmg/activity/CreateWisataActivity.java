package org.sandec.wisatasmg.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.sandec.wisatasmg.R;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateWisataActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "CreateWisataActivity";
    private static final int RC_GALLERY = 1;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.edt_nama)
    EditText edtNama;
    @BindView(R.id.input_layout_nama)
    TextInputLayout inputLayoutNama;
    @BindView(R.id.edt_deskripsi)
    EditText edtDeskripsi;
    @BindView(R.id.input_layout_deskripsi)
    TextInputLayout inputLayoutDeskripsi;
    @BindView(R.id.edt_alamat)
    EditText edtAlamat;
    @BindView(R.id.input_layout_alamat)
    TextInputLayout inputLayoutAlamat;
    @BindView(R.id.edt_event)
    EditText edtEvent;
    @BindView(R.id.input_layout_event)
    TextInputLayout inputLayoutEvent;
    @BindView(R.id.btn_maps)
    Button btnMaps;
    @BindView(R.id.status_maps)
    TextView statusMaps;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wisata);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.imageView, R.id.btn_maps, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageView:
                //dapet foto dari hp
                openGallery();
                break;
            case R.id.btn_maps:
                break;
            case R.id.btn_submit:
                break;
        }
    }

    private void openGallery() {

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getApplicationContext(), perms)) {
            EasyPermissions.requestPermissions(CreateWisataActivity.this, "Butuh kamu", 100, perms);
        }

        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(gallery, RC_GALLERY);
    }

    public Uri uri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GALLERY && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            Log.d(TAG, "onActivityResult: " + uri.toString());
            try {
                Bitmap hasil = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(hasil);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == 100){
            openGallery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
