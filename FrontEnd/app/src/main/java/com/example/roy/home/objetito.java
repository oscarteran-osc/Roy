package com.example.roy.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.roy.R;

public class objetito extends Fragment {

    ImageView imgmain, img1, img2, img3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita;
        vistita = inflater.inflate(R.layout.fragment_objetito, container, false);
        imgmain = vistita.findViewById(R.id.mainimg);
        img1 = vistita.findViewById(R.id.mini1);
        img2 = vistita.findViewById(R.id.mini2);
        img3 = vistita.findViewById(R.id.mini3);

        imgmain.setImageResource(R.drawable.casacampania);
        img1.setOnClickListener(v -> changeImage(R.drawable.casacampania));
        img2.setOnClickListener(v -> changeImage(R.drawable.tienda2));
        img3.setOnClickListener(v -> changeImage(R.drawable.tienda3));

        return vistita;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.resenas, new resenas())
                    .commit();
        }
    }

    private void changeImage(int resId) {

        imgmain.setImageResource(resId);
    }

}