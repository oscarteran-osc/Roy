package com.example.roy.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {

    private static final String TAG = "HomeFragment";

    // Categorías
    private TextView categoryTodo, categoryEventos, categoryTecnologia, categoryTransporte, categoryHerramientas;
    private TextView categoryHogar, categoryDeportes, categoryElectro, categoryRopa, categoryJuegos, categoryMascotas;

    private TextView explorarMasBtn;
    private LinearLayout serviceItem1, serviceItem2, serviceItem3, serviceItem4;
    private ImageView menuIcon;
    private EditText searchBar;

    // Destacado
    private CardView featuredCard;
    private TextView tituloDestacado, descDestacado;
    private ImageView imagenDestacado;
    private Objeto objetoDestacado;

    // Recyclers
    private RecyclerView recomendadosRecycler, cercaRecycler;
    private RecomendadosAdapter recomendadosAdapter, cercaAdapter;

    private final List<Objeto> recomendadosList = new ArrayList<>();
    private final List<Objeto> cercaList = new ArrayList<>();

    // UI estados
    private View recomendadosLoading;
    private TextView recomendadosEmpty;

    private ApiService api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        api = RetrofitClient.getClient().create(ApiService.class);

        bindViews(view);
        setupClicks();
        setupSearch();
        setupRecyclers();

        cargarDestacado();
        cargarRecomendadosYCerca();

        return view;
    }

    private void bindViews(View view) {
        // Categorías
        categoryTodo = view.findViewById(R.id.category_todo);
        categoryEventos = view.findViewById(R.id.category_eventos);
        categoryTecnologia = view.findViewById(R.id.category_tecnologia);
        categoryHerramientas = view.findViewById(R.id.category_herramientas);

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

        recomendadosRecycler = view.findViewById(R.id.recomendados_recycler);
        cercaRecycler = view.findViewById(R.id.cerca_recycler);

        recomendadosLoading = view.findViewById(R.id.recomendados_loading);
        recomendadosEmpty = view.findViewById(R.id.recomendados_empty);
    }

    private void setupClicks() {
        safeClick(categoryTodo, () -> loadCategoryFragment("Todo"));
        safeClick(categoryEventos, () -> loadCategoryFragment("Eventos"));
        safeClick(categoryTecnologia, () -> loadCategoryFragment("Tecnología"));
        safeClick(categoryTransporte, () -> loadCategoryFragment("Transporte"));
        safeClick(categoryHerramientas, () -> loadCategoryFragment("Herramientas"));

        safeClick(categoryHogar, () -> loadCategoryFragment("Hogar y Muebles"));
        safeClick(categoryDeportes, () -> loadCategoryFragment("Deportes y Aire Libre"));
        safeClick(categoryElectro, () -> loadCategoryFragment("Electrodomesticos"));
        safeClick(categoryRopa, () -> loadCategoryFragment("Ropa Y Accesorios"));
        safeClick(categoryJuegos, () -> loadCategoryFragment("Juegos y Entretenimiento"));
        safeClick(categoryMascotas, () -> loadCategoryFragment("Mascotas"));

        safeClick(explorarMasBtn, () -> loadDetailFragment("Explorar"));

        safeClick(serviceItem1, () -> loadDetailFragment("Proyecto"));
        safeClick(serviceItem2, () -> loadDetailFragment("Escapada"));
        safeClick(serviceItem3, () -> loadDetailFragment("Reparar"));
        safeClick(serviceItem4, () -> loadDetailFragment("Evento"));

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "Menú (pendiente)", Toast.LENGTH_SHORT).show()
            );
        }

        if (featuredCard != null) {
            featuredCard.setOnClickListener(v -> {
                if (objetoDestacado != null) {
                    openObjetoActivity(objetoDestacado.getIdObjeto());
                }
            });
        }
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

    private void setupRecyclers() {
        if (recomendadosRecycler != null) {
            recomendadosAdapter = new RecomendadosAdapter(recomendadosList, objeto ->
                    openObjetoActivity(objeto.getIdObjeto())
            );
            recomendadosRecycler.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            recomendadosRecycler.setAdapter(recomendadosAdapter);
        }

        if (cercaRecycler != null) {
            cercaAdapter = new RecomendadosAdapter(cercaList, objeto ->
                    openObjetoActivity(objeto.getIdObjeto())
            );
            cercaRecycler.setLayoutManager(
                    new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            cercaRecycler.setAdapter(cercaAdapter);
        }
    }

    private void safeClick(View v, Runnable r) {
        if (v != null) v.setOnClickListener(view -> r.run());
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

    private void openObjetoActivity(int objetoId) {
        Intent intent = new Intent(requireContext(), com.example.roy.home.Objetoo.class);
        intent.putExtra("objetoId", objetoId);
        startActivity(intent);
    }

    // ------------------ DATA LOADERS ------------------

    private void setLoading(boolean loading) {
        if (recomendadosLoading != null) recomendadosLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (recomendadosRecycler != null) recomendadosRecycler.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
    }

    private void setEmptyState(boolean empty) {
        if (recomendadosEmpty != null) recomendadosEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private void cargarDestacado() {
        if (tituloDestacado == null || descDestacado == null || imagenDestacado == null) return;

        api.getDestacado().enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                if (!isAdded()) return;

                Log.d(TAG, "destacado code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    objetoDestacado = response.body();

                    tituloDestacado.setText(objetoDestacado.getNombreObjeto());
                    descDestacado.setText(objetoDestacado.getDescripcion());

                    String url = objetoDestacado.getImagenUrl();
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

                } else {
                    Log.w(TAG, "destacado falló code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                Log.e(TAG, "destacado failure: " + t.getMessage(), t);
            }
        });
    }

    private void cargarRecomendadosYCerca() {
        setLoading(true);
        setEmptyState(false);

        api.getRecomendados().enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (!isAdded()) return;

                Log.d(TAG, "recomendados code=" + response.code());

                setLoading(false);

                if (!response.isSuccessful() || response.body() == null) {
                    setEmptyState(true);
                    Log.e(TAG, "recomendados error code=" + response.code());
                    return;
                }

                List<Objeto> all = response.body();
                Log.d(TAG, "recomendados size=" + all.size());

                if (all.isEmpty()) {
                    setEmptyState(true);
                    return;
                }

                // Mezclar para que se vea variado
                Collections.shuffle(all);

                // Recomendados
                recomendadosList.clear();
                for (int i = 0; i < Math.min(10, all.size()); i++) {
                    recomendadosList.add(all.get(i));
                }

                // Cerca de ti (si hay zona guardada)
                cercaList.clear();
                String miZona = getZonaUsuario();

                if (miZona != null && !miZona.trim().isEmpty()) {
                    String zonaNorm = miZona.trim().toLowerCase();
                    for (Objeto o : all) {
                        if (o.getZona() != null && o.getZona().toLowerCase().contains(zonaNorm)) {
                            cercaList.add(o);
                        }
                        if (cercaList.size() >= 6) break;
                    }
                }

                // Si no había zona o no hubo matches, fallback: siguientes 6 distintos
                if (cercaList.isEmpty()) {
                    for (int i = 10; i < Math.min(16, all.size()); i++) {
                        cercaList.add(all.get(i));
                    }
                }

                if (recomendadosAdapter != null) recomendadosAdapter.notifyDataSetChanged();
                if (cercaAdapter != null) cercaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                if (!isAdded()) return;

                setLoading(false);
                setEmptyState(true);

                Log.e(TAG, "recomendados failure: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getZonaUsuario() {
        // Ajusta el nombre del sharedprefs a tu app (ej: "ROY_PREFS")
        try {
            SharedPreferences prefs = requireContext().getSharedPreferences("ROY_PREFS", 0);
            // Ajusta la key a la que tú uses (ej: "zona", "userZona", etc.)
            return prefs.getString("zona", null);
        } catch (Exception e) {
            return null;
        }
    }
}
