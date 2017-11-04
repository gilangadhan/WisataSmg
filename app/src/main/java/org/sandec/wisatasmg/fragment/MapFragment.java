package org.sandec.wisatasmg.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.adapter.WisataAdapter;
import org.sandec.wisatasmg.drawroutemap.DrawMarker;
import org.sandec.wisatasmg.drawroutemap.DrawRouteMaps;
import org.sandec.wisatasmg.drawroutemap.FetchUrl;
import org.sandec.wisatasmg.helper.LocationFinder;
import org.sandec.wisatasmg.model.ListWisataModel;
import org.sandec.wisatasmg.model.WisataModel;
import org.sandec.wisatasmg.networking.ApiServices;
import org.sandec.wisatasmg.networking.RetrofitConfig;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int RC_CAMERA_AND_LOCATION = 100;

    private GoogleMap mMap;
    ArrayList<WisataModel> listData;


    String jarak;
    String waktu;

    float latMe;
    float lngMe;
    //GPStrack gpStrack;
    TextView tvWaktu;
    TextView tvJarak;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listData = new ArrayList<>();


        //  gpStrack = new GPStrack(getContext());

        tvWaktu = (TextView) view.findViewById(R.id.tv_map_waktu);
        tvJarak = (TextView) view.findViewById(R.id.tv_map_jarak);

        return view;
    }

    boolean gpsStatus = false;
    boolean networkStatus = false;

    private void gpsSwitch() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkStatus = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsStatus && !networkStatus) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getActivity().sendBroadcast(poke);
        }
    }

    LatLng origin, destination;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        gpsSwitch();
        LocationFinder gps = new LocationFinder(getActivity());

        if (gps.locationavailable()) {
            latMe = gps.getLatitude();
            lngMe = gps.getLongitude();

        } else {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getActivity().sendBroadcast(poke);
            latMe = gps.getLatitude();
            lngMe = gps.getLongitude();
        }

        origin = new LatLng(latMe, lngMe);



        ambilData();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                mMap.clear();
                refreshMarker();
                Log.d("MAP", "sukses map");

                origin = new LatLng(latMe, lngMe);
                destination = marker.getPosition();

                DrawRouteMaps.getInstance(getContext()).draw(origin, destination, mMap);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
                getLocation(origin, destination, mMap);

//                progress.hide();
            }
        });
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));

//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//
//                mMap.clear();
//                refreshMarker();
//                Log.d("MAP", "sukses map");
//
//                origin = new LatLng(latMe, lngMe);
//                destination = marker.getPosition();
//
//                DrawRouteMaps.getInstance(getContext()).draw(origin, destination, mMap);
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));
//                getLocation(origin, destination, mMap);
//
////                progress.hide();
//
//                return true;
//
//            }
//        });

    }


    private void ambilData() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Mohon Bersabar");
        progress.show();

        ApiServices api = RetrofitConfig.getApiServices();
        Call<ListWisataModel> call = api.ambilDataWisata();
        call.enqueue(new Callback<ListWisataModel>() {
            @Override
            public void onResponse(Call<ListWisataModel> call, Response<ListWisataModel> response) {
                progress.hide();
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().toString().equals("true")) {
                        listData = response.body().getWisata();

                        for (int i = 0; i < listData.size(); i++) {

                            Double lat = Double.valueOf(listData.get(i).getLatitudeWisata());
                            Double lng = Double.valueOf(listData.get(i).getLongitudeWisata());

                            LatLng lokasi = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(lokasi).title(listData.get(i).getNamaWisata()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15));

                            Log.d(TAG, "onResponse: " + listData.get(i).getGambarWisata());
                        }
                    } else {
                        Toast.makeText(getActivity(), response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Respones is Not Succesfull", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ListWisataModel> call, Throwable t) {
                Toast.makeText(getActivity(), "ModelUser Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation(final LatLng origin, final LatLng destination, final GoogleMap map) {
        OkHttpClient client = new OkHttpClient();

        String url_route = FetchUrl.getUrl(origin, destination);

        Request request = new Request.Builder()
                .url(url_route)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.d("route", response.body().toString());

                try {

                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray routes = json.getJSONArray("routes");

                    JSONObject distance = routes.getJSONObject(0)
                            .getJSONArray("legs")
                            .getJSONObject(0)
                            .getJSONObject("distance");

                    JSONObject duration = routes.getJSONObject(0)
                            .getJSONArray("legs")
                            .getJSONObject(0)
                            .getJSONObject("duration");

                    jarak = distance.getString("text");
                    waktu = duration.getString("text");

                    Log.d("distance", distance.toString());
                    Log.d("duration", duration.toString());


                } catch (JSONException e) {

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(getContext(), "Hey", Toast.LENGTH_SHORT).show();
                        map.addMarker(new MarkerOptions()
                                .title("Origin")
                                .position(origin)
                                .snippet(jarak + " " + waktu)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location)));

                        tvJarak.setText("Jarak: " + jarak);

                        tvJarak.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude));
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        });

                        tvWaktu.setText("Waktu: " + waktu);

                    }
                });


            }
        });

    }

    private void refreshMarker() {

        for (int i = 0; i < listData.size(); i++) {

            Double lat = Double.valueOf(listData.get(i).getLatitudeWisata());
            Double lng = Double.valueOf(listData.get(i).getLongitudeWisata());

            LatLng sydney = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(sydney).title(listData.get(i).getNamaWisata()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location)));

            Log.d(TAG, "onResponse: " + listData.get(i).getGambarWisata());

        }

    }


}
