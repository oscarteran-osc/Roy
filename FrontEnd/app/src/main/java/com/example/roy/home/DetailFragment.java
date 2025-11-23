package com.example.roy.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;

public class DetailFragment extends Fragment {
    private static final String ARG_DETAIL = "detail";

    public static DetailFragment newInstance(String detail) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DETAIL, detail);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView textView = view.findViewById(R.id.empty_text);
        String detail = getArguments() != null ? getArguments().getString(ARG_DETAIL) : "Detalle";
        textView.setText("Detalle: " + detail);
        return view;
    }
}