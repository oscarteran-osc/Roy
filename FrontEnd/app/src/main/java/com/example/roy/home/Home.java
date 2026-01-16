package com.example.roy.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {

    private static final String TAG = "HomeFragment";
    private TextView categoryTodo, categoryEventos, categoryTecnologia, categoryTransporte, categoryHerramientas;
    private TextView categoryHogar, categoryDeportes, categoryElectro, categoryRopa, categoryJuegos, categoryMascotas;
    private TextView explorarMasBtn;
    private LinearLayout serviceItem1, serviceItem2, serviceItem3, serviceItem4;
    private ImageView menuIcon;
    private EditText searchBar;
    private CardView featuredCard;
    private TextView tituloDestacado, descDestacado;
    private ImageView imagenDestacado;
    private RecyclerView recomendadosRecycler, cercaRecycler;
    private RecomendadosAdapter recomendadosAdapter, cercaAdapter;
    private final List<com.example.roy.models.Objeto> recomendadosList = new ArrayList<>();
    private final List<com.example.roy.models.Objeto> cercaList = new ArrayList<>();
    private View recomendadosLoading;
    private TextView recomendadosEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bindViews(view);
        setupClicks();
        setupSearch();
        setupRecomendadosRecycler();

        cargarDestacadoAleatorio();
        cargarRecomendados();

        setupRecyclers();
        cargarCercaDeTi();

        return view;
    }
    private Objeto objetoDestacado;


    private void bindViews(View view) {
        // Categorías (las que ya tenías)
        categoryTodo = view.findViewById(R.id.category_todo);
        categoryEventos = view.findViewById(R.id.category_eventos);
        categoryTecnologia = view.findViewById(R.id.category_tecnologia);
        categoryHerramientas = view.findViewById(R.id.category_herramientas);

        // ✅ Categorías nuevas (ya están en el XML pero antes no las estabas usando)
        categoryHogar = view.findViewById(R.id.category_hogar);
        categoryDeportes = view.findViewById(R.id.category_deportes);
        categoryElectro = view.findViewById(R.id.category_electrodomesticos);
        categoryRopa = view.findViewById(R.id.category_ropa);
        categoryJuegos = view.findViewById(R.id.category_juegos);
        categoryMascotas = view.findViewById(R.id.category_mascotas);

        explorarMasBtn = view.findViewById(R.id.explorar_mas_btn);
        serviceItem1 = view.findViewById(R.id.service_item_1);
        serviceItem2 = view.findViewById(R.id.service_item_2);
        serviceItem3 = view.findViewById(R.id.service_item_3);
        serviceItem4 = view.findViewById(R.id.service_item_4);
        searchBar = view.findViewById(R.id.search_bar);

        featuredCard = view.findViewById(R.id.featured_card);
        if (featuredCard != null) {
            tituloDestacado = featuredCard.findViewById(R.id.titulo_destacado);
            descDestacado = featuredCard.findViewById(R.id.desc_destacado);
            imagenDestacado = featuredCard.findViewById(R.id.imagen_destacado);
        }

        // ✅ Recomendados (si agregaste el Recycler)

        recomendadosRecycler = view.findViewById(R.id.recomendados_recycler);
        cercaRecycler = view.findViewById(R.id.cerca_recycler);
        recomendadosLoading = view.findViewById(R.id.recomendados_loading);
        recomendadosEmpty = view.findViewById(R.id.recomendados_empty);

    }

    private void setupClicks() {
        // Categorías
        safeClick(categoryTodo, () -> loadCategoryFragment("Todo"));
        safeClick(categoryEventos, () -> loadCategoryFragment("Eventos"));
        safeClick(categoryTecnologia, () -> loadCategoryFragment("Tecnología"));
        safeClick(categoryTransporte, () -> loadCategoryFragment("Transporte"));
        safeClick(categoryHerramientas, () -> loadCategoryFragment("Herramientas"));

        // ✅ nuevas
        safeClick(categoryHogar, () -> loadCategoryFragment("Hogar y Muebles"));
        safeClick(categoryDeportes, () -> loadCategoryFragment("Deportes y Aire Libre"));
        safeClick(categoryElectro, () -> loadCategoryFragment("Electrodomesticos"));
        safeClick(categoryRopa, () -> loadCategoryFragment("Ropa Y Accesorios"));
        safeClick(categoryJuegos, () -> loadCategoryFragment("Juegos y Entretenimiento"));
        safeClick(categoryMascotas, () -> loadCategoryFragment("Mascotas"));

        // Explorar
        safeClick(explorarMasBtn, () -> loadDetailFragment("Explorar"));

        // Cards “Te puede servir para...”
        safeClick(serviceItem1, () -> loadDetailFragment("Proyecto"));
        safeClick(serviceItem2, () -> loadDetailFragment("Escapada"));
        safeClick(serviceItem3, () -> loadDetailFragment("Reparar"));
        safeClick(serviceItem4, () -> loadDetailFragment("Evento"));

        // Menú (placeholder)
        if (menuIcon != null) {
            menuIcon.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "Menú (pendiente)", Toast.LENGTH_SHORT).show()
            );
        }

        if (featuredCard != null) {
            featuredCard.setOnClickListener(v -> {
                if (objetoDestacado != null) {
                    openObjetoActivity(objetoDestacado.getId());
                }
            });
        }
    }

    private void safeClick(View v, Runnable r) {
        if (v != null) v.setOnClickListener(view -> r.run());
    }

    private void setupSearch() {
        if (searchBar == null) return;
        searchBar.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String texto = searchBar.getText().toString().trim();
                if (!texto.isEmpty()) abrirResultadosBusqueda(texto);
                return true;
            }
            return false;
        });
    }

    private void setupRecomendadosRecycler() {
        if (recomendadosRecycler == null) return;

        recomendadosAdapter = new RecomendadosAdapter(recomendadosList, objeto -> {
            // ✅ Pasar el ID del objeto al abrir la Activity
            openObjetoActivity(objeto.getId());
        });

        recomendadosRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        recomendadosRecycler.setAdapter(recomendadosAdapter);
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

    private void abrirResultadosBusqueda(String texto) {
        CategoryFragment categoryFragment = CategoryFragment.newInstance("buscar:" + texto);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // ✅ Modificar el método openObjetoActivity para recibir el ID
    private void openObjetoActivity(int objetoId) {
        Intent intent = new Intent(requireContext(), com.example.roy.home.Objetoo.class);
        intent.putExtra("objetoId", objetoId); // Pasar el ID como extra
        startActivity(intent);
    }

    // ✅ Para el featuredCard, necesitas guardar el objeto destacado

    private void cargarDestacadoAleatorio() {
        if (tituloDestacado == null || descDestacado == null || imagenDestacado == null) return;

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getDestacado().enqueue(new Callback<com.example.roy.models.Objeto>() {
            @Override
            public void onResponse(Call<com.example.roy.models.Objeto> call,
                                   Response<com.example.roy.models.Objeto> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    objetoDestacado = response.body(); // ✅ Guardar el objeto

                    tituloDestacado.setText(objetoDestacado.getNombre());
                    descDestacado.setText(objetoDestacado.getDescripcion());

                    String url = objetoDestacado.getImagenPrincipal();
                    if (url != null) url = url.trim();

                    if (url != null && !url.isEmpty()) {
                        Glide.with(requireContext())
                                .load(url)
                                .placeholder(R.drawable.tent_image)
                                .error(R.drawable.tent_image)
                                .into(imagenDestacado);
                    } else {
                        imagenDestacado.setImageResource(R.drawable.tent_image);
                    }

                    Log.d(TAG, "Destacado cargado: " + objetoDestacado.getNombre());
                } else {
                    Log.w(TAG, "Destacado falló: code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<com.example.roy.models.Objeto> call, Throwable t) {
                Log.e(TAG, "Destacado onFailure: " + t.getMessage(), t);
            }
        });
    }

    private void setupRecyclers() {
        if (recomendadosRecycler != null) {
            recomendadosAdapter = new RecomendadosAdapter(recomendadosList, objeto -> {
                // ✅ Pasar el ID correcto
                openObjetoActivity(objeto.getId());
            });
            recomendadosRecycler.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            recomendadosRecycler.setAdapter(recomendadosAdapter);
        }

        if (cercaRecycler != null) {
            cercaAdapter = new RecomendadosAdapter(cercaList, objeto -> {
                // ✅ Pasar el ID correcto
                openObjetoActivity(objeto.getId());
            });
            cercaRecycler.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            cercaRecycler.setAdapter(cercaAdapter);
        }
    }

    private final List<com.example.roy.models.Objeto> allObjetos = new ArrayList<>();

    private void cargarRecomendados() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getRecomendados().enqueue(new Callback<List<com.example.roy.models.Objeto>>() {
            @Override
            public void onResponse(Call<List<com.example.roy.models.Objeto>> call,
                                   Response<List<com.example.roy.models.Objeto>> response) {

                if (!isAdded()) return;

                allObjetos.clear();
                if (response.isSuccessful() && response.body() != null) {
                    allObjetos.addAll(response.body());
                }

                // Mezcla para variedad
                java.util.Collections.shuffle(allObjetos);

                recomendadosList.clear();
                cercaList.clear();

                // Recomendados: primeros 10
                for (int i = 0; i < Math.min(10, allObjetos.size()); i++) {
                    recomendadosList.add(allObjetos.get(i));
                }

                // Cerca de ti: siguientes 6 (diferentes)
                for (int i = 10; i < Math.min(16, allObjetos.size()); i++) {
                    cercaList.add(allObjetos.get(i));
                }

                if (recomendadosAdapter != null) recomendadosAdapter.notifyDataSetChanged();
                if (cercaAdapter != null) cercaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<com.example.roy.models.Objeto>> call, Throwable t) { }
        });
    }

    private void cargarCercaDeTi() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getRecomendados().enqueue(new Callback<List<com.example.roy.models.Objeto>>() {
            @Override
            public void onResponse(Call<List<com.example.roy.models.Objeto>> call, Response<List<com.example.roy.models.Objeto>> response) {
                if (!isAdded()) return;

                cercaList.clear();
                if (response.isSuccessful() && response.body() != null) {
                    // ✅ para que se sienta distinto: tomamos los primeros 6
                    List<com.example.roy.models.Objeto> all = response.body();
                    for (int i = 0; i < Math.min(6, all.size()); i++) {
                        cercaList.add(all.get(i));
                    }
                    if (cercaAdapter != null) cercaAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<com.example.roy.models.Objeto>> call, Throwable t) {
                // opcional: no mostramos error acá para no ensuciar el home
            }
        });
    }

}
