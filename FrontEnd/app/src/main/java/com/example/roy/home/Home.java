package com.example.roy.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment implements View.OnClickListener {

    private TextView categoryTodo, categoryEventos, categoryTecnologia, categoryTransporte, categoryHerramientas;
    private TextView explorarMasBtn, verMasBtn, verMasFinalBtn;
    private LinearLayout serviceItem1, serviceItem2, serviceItem3, serviceItem4;
    private ImageView menuIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 🔹 Categorías
        categoryTodo = view.findViewById(R.id.category_todo);
        categoryEventos = view.findViewById(R.id.category_eventos);
        categoryTecnologia = view.findViewById(R.id.category_tecnologia);
        categoryTransporte = view.findViewById(R.id.category_transporte);
        categoryHerramientas = view.findViewById(R.id.category_herramientas);

        // 🔹 Botones de texto
        explorarMasBtn = view.findViewById(R.id.explorar_mas_btn);
        verMasBtn = view.findViewById(R.id.ver_mas_btn);
        verMasFinalBtn = view.findViewById(R.id.ver_mas_final_btn);

        // 🔹 Cards de “Te puede servir para…”
        serviceItem1 = view.findViewById(R.id.service_item_1);
        serviceItem2 = view.findViewById(R.id.service_item_2);
        serviceItem3 = view.findViewById(R.id.service_item_3);
        serviceItem4 = view.findViewById(R.id.service_item_4);

        menuIcon = view.findViewById(R.id.menu_icon);

        // 🔹 Clicks de categorías
        categoryTodo.setOnClickListener(v -> loadCategoryFragment("Todo"));
        categoryEventos.setOnClickListener(v -> loadCategoryFragment("Eventos"));
        categoryTecnologia.setOnClickListener(v -> loadCategoryFragment("Tecnología"));
        categoryTransporte.setOnClickListener(v -> loadCategoryFragment("Transporte"));
        categoryHerramientas.setOnClickListener(v -> loadCategoryFragment("Herramientas"));

        // 🔹 Clicks de botones
        explorarMasBtn.setOnClickListener(v -> loadDetailFragment("Explorar"));
        verMasBtn.setOnClickListener(this);
        verMasFinalBtn.setOnClickListener(v -> loadDetailFragment("Ver Más"));

        serviceItem1.setOnClickListener(v -> loadDetailFragment("Proyecto"));
        serviceItem2.setOnClickListener(v -> loadDetailFragment("Escapada"));
        serviceItem3.setOnClickListener(v -> loadDetailFragment("Reparar"));
        serviceItem4.setOnClickListener(v -> loadDetailFragment("Evento"));

        // 🔹 (Opcional) barra de búsqueda, si ya la tienes en el XML
        EditText searchBar = view.findViewById(R.id.search_bar);
        if (searchBar != null) {
            searchBar.setOnEditorActionListener((v, actionId, event) -> {
                String texto = searchBar.getText().toString().trim();
                if (!texto.isEmpty()) {
                    abrirResultadosBusqueda(texto);
                }
                return true;
            });
        }

        // 🔹 Cargar destacado desde el backend
        cargarDestacadoAleatorio(view);

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
        if (cadenita == R.id.ver_mas_btn) {
            openObjetoFragment();
        }
    }

    // 🔹 Navegar a resultados de búsqueda usando CategoryFragment
    private void abrirResultadosBusqueda(String texto) {
        CategoryFragment categoryFragment = CategoryFragment.newInstance("buscar:" + texto);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // 🔹 Llama al endpoint /api/objeto/destacado y llena la card
    private void cargarDestacadoAleatorio(View view) {
        CardView featuredCard = view.findViewById(R.id.featured_card);
        TextView titulo = featuredCard.findViewById(R.id.titulo_destacado);
        TextView desc = featuredCard.findViewById(R.id.desc_destacado);
        ImageView img = featuredCard.findViewById(R.id.imagen_destacado);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getDestacado().enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Objeto o = response.body();
                    titulo.setText(o.getNombreObjeto());
                    desc.setText(o.getDescripcion());
                    // por ahora dejamos la imagen fija (tent_image)
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                // Si algo falla, puedes loguear o dejar texto por defecto
            }
        });
    }
}
