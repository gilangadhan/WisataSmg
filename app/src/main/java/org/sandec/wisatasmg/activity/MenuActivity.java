package org.sandec.wisatasmg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.fragment.FavoriteFragment;
import org.sandec.wisatasmg.fragment.HomeFragment;
import org.sandec.wisatasmg.fragment.MapFragment;
import org.sandec.wisatasmg.fragment.ProfilFragment;
import org.sandec.wisatasmg.helper.SessionManager;

public class MenuActivity extends SessionManager {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager manager = getSupportFragmentManager();
            switch (item.getItemId()) {
                case R.id.nav_home:

                    manager.beginTransaction()
                            .replace(R.id.container, new HomeFragment())
                            .commit();
                    getSupportActionBar().setTitle("Wisata Semarang");
                    return true;
                case R.id.nav_peta:

                    manager.beginTransaction()
                            .replace(R.id.container, new MapFragment())
                            .commit();

                    getSupportActionBar().setTitle("Petaku");
                    return true;
                case R.id.nav_profil:

                    manager.beginTransaction()
                            .replace(R.id.container, new ProfilFragment())
                            .commit();
                    getSupportActionBar().setTitle("Profil");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.container, new HomeFragment())
                .commit();
        getSupportActionBar().setTitle("Wisata Semarang");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            sessionManager.logout();
            return true;
        } else if (id == R.id.action_fav){
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.container, new FavoriteFragment())
                    .commit();
            getSupportActionBar().setTitle("Wisata Favorit");
        }else  if (id== R.id.action_tambah){
            myIntent(CreateWisataActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
