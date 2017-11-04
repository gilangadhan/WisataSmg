/*
 * Copyright (c) 2017. Gilang Ramadhan (gilangramadhan96.gr@gmail.com)
 */

package org.sandec.wisatasmg.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;

import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.activity.LoginActivity;
import org.sandec.wisatasmg.activity.MainActivity;
import org.sandec.wisatasmg.activity.MenuActivity;


public class SessionManager extends MyFuction {
    @VisibleForTesting

    /*variable sharepreference*/
            SharedPreferences pref;

    public SharedPreferences.Editor editor;
    public SessionManager sessionManager;

    /*mode share preference*/
    int mode = 0;

    /*nama dari share preference*/
    private static final String pref_name = "crudpref";

    /*kunci share preference*/
    private static final String is_login = "islogin";
    public static final String kunci_email = "keyemail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());

    }

    public SessionManager() {

    }

    /*construktor*/
    public SessionManager(Context context) {
        /*mengakses class ini*/
        c = context;
        /*share preference dari class ini*/ /*(nama, mode)*/
        pref = context.getSharedPreferences(pref_name, mode);
        editor = pref.edit();
    }

    /*methode membuat session*/
    public void createSession(String email) {
        /*login value menjadi true*/
        editor.putBoolean(is_login, true);
        /*memasukkan email ke dalam variable kunci email*/
        editor.putString(kunci_email, email);
        editor.commit();
    }

    public void setUser(String iduser, String nama, String email, String pass, String gambar) {
        editor.putBoolean(is_login, true);
        editor.putString(Konstanta.ID_USER, iduser);
        editor.putString(Konstanta.NAMA_USER, nama);
        editor.putString(Konstanta.EMAIL_USER, email);
        editor.putString(Konstanta.PASS_USER, pass);
        editor.putString(Konstanta.GAMBAR_USER, gambar);
        editor.commit();
    }

    public String getNama() {
        return pref.getString(Konstanta.NAMA_USER, "");
    }

    public String getEmail() {
        return pref.getString(Konstanta.EMAIL_USER, "");
    }

    public String getGambar() {
        return pref.getString(Konstanta.GAMBAR_USER, "");
    }

    public String getPass() {
        return pref.getString(Konstanta.PASS_USER, "");
    }

    public String getIdUser() {
        return pref.getString(Konstanta.ID_USER, "");
    }


    public void checkLogin() {
        /*jika is_login = false*/
        if (!this.islogin()) {
            /*pergi ke loginactivity*/
            Intent i = new Intent(c, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);

        } else {
            /*jika true, pergi ke mainactivity*/
            Intent i = new Intent(c, MenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
        }
    }

    /*set is_login menjadi false*/
    public boolean islogin() {
        return pref.getBoolean(is_login, false);
    }


    public void logout() {

        /*hapus semua data dan kunci*/
        editor.clear();
        editor.commit();

        //gmail logout


        /*pergi ke loginactivity*/
        Intent i = new Intent(c, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(i);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }


}
