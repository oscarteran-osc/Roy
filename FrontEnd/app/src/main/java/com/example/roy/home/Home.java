package com.example.roy.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.roy.R;

public class Home extends Fragment implements View.OnClickListener {

    private TextView categoryTodo, categoryEventos, categoryTecnologia, categoryTransporte, categoryHerramientas;
    private TextView explorarMasBtn, verMasBtn, verMasFinalBtn;
    private LinearLayout serviceItem1, serviceItem2, serviceItem3, serviceItem4;
    private ImageView menuIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryTodo = view.findViewById(R.id.category_todo);
        categoryEventos = view.findViewById(R.id.category_eventos);
        categoryTecnologia = view.findViewById(R.id.category_tecnologia);
        categoryTransporte = view.findViewById(R.id.category_transporte);
        categoryHerramientas = view.findViewById(R.id.category_herramientas);

        explorarMasBtn = view.findViewById(R.id.explorar_mas_btn);
        verMasBtn = view.findViewById(R.id.ver_mas_btn);
        verMasFinalBtn = view.findViewById(R.id.ver_mas_final_btn);

        serviceItem1 = view.findViewById(R.id.service_item_1);
        serviceItem2 = view.findViewById(R.id.service_item_2);
        serviceItem3 = view.findViewById(R.id.service_item_3);
        serviceItem4 = view.findViewById(R.id.service_item_4);

        menuIcon = view.findViewById(R.id.menu_icon);

        categoryTodo.setOnClickListener(v -> loadCategoryFragment("Todo"));
        categoryEventos.setOnClickListener(v -> loadCategoryFragment("Eventos"));
        categoryTecnologia.setOnClickListener(v -> loadCategoryFragment("Tecnología"));
        categoryTransporte.setOnClickListener(v -> loadCategoryFragment("Transporte"));
        categoryHerramientas.setOnClickListener(v -> loadCategoryFragment("Herramientas"));

        explorarMasBtn.setOnClickListener(v -> loadDetailFragment("Explorar"));
        verMasBtn.setOnClickListener(this);

        verMasFinalBtn.setOnClickListener(v -> loadDetailFragment("Ver Más"));

        serviceItem1.setOnClickListener(v -> loadDetailFragment("Proyecto"));
        serviceItem2.setOnClickListener(v -> loadDetailFragment("Escapada"));
        serviceItem3.setOnClickListener(v -> loadDetailFragment("Reparar"));
        serviceItem4.setOnClickListener(v -> loadDetailFragment("Evento"));

        return view;
    }

    private void loadCategoryFragment(String category) {
        CategoryFragment categoryFragment = CategoryFragment.newInstance(category);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadDetailFragment(String detail) {
        DetailFragment detailFragment = DetailFragment.newInstance(detail);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openObjetoFragment() {
        Intent intent = new Intent(getContext(), Objeto.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int cadenita = v.getId();
        if(cadenita == R.id.ver_mas_btn){
            openObjetoFragment();
        }
    }
}