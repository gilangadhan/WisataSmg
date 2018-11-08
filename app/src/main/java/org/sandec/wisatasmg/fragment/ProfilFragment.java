package org.sandec.wisatasmg.fragment;


import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.sandec.wisatasmg.R;
import org.sandec.wisatasmg.activity.DetailWisataActivity;
import org.sandec.wisatasmg.helper.Konstanta;
import org.sandec.wisatasmg.helper.SessionManager;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilFragment extends Fragment {


    public ProfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CircleImageView user_profile_photo = (CircleImageView) view.findViewById(R.id.user_profile_photo);
        TextView user_profile_name = (TextView)view.findViewById(R.id.user_profile_name);
        TextView user_profile_short_bio = (TextView) view.findViewById(R.id.user_profile_short_bio);

        SessionManager manager = new SessionManager(getActivity());
        Glide.with(getActivity()).load(Konstanta.USER_URL+manager.getGambar()).into(user_profile_photo);
        user_profile_name.setText(manager.getNama());
        user_profile_short_bio.setText(manager.getEmail());

    }
}
