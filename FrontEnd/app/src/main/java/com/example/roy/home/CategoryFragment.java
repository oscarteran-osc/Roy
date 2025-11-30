package com.example.roy.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.misobjetos.objetosAdapter;
import com.example.roy.models.Objeto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ImageView;
import com.google.android.material.chip.Chip;
import java.util.Collections;
import java.util.Comparator;

public class CategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    public static CategoryFragment newInstance(String category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    private String category;

    private ListView listView;
    private TextView emptyText;
    private EditText searchBar;
    private TextView tvTituloCategoria;
    private TextView tvPillCategoria;
    private TextView tvResultadosCount;

    private Chip chipTodo, chipCerca, chipBarato, chipPopular;

    private ObjetosCategoriaAdapter adapter;

    private final List<Objeto> listaOriginal = new ArrayList<>();
    private final List<Objeto> listaFiltrada = new ArrayList<>();

    // para saber qué filtro extra está activo
    private enum ExtraFilter { NONE, MAS_BARATO }
    private ExtraFilter extraFilter = ExtraFilter.NONE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // === FIND VIEWS ===
        tvTituloCategoria  = view.findViewById(R.id.tvTituloCategoria);
        tvPillCategoria    = view.findViewById(R.id.tvPillCategoria);
        tvResultadosCount  = view.findViewById(R.id.tvResultadosCount);
        listView           = view.findViewById(R.id.lvObjetosCategoria);
        emptyText          = view.findViewById(R.id.empty_text);
        searchBar          = view.findViewById(R.id.etBuscarObjeto);

        chipTodo   = view.findViewById(R.id.chipTodo);
        chipCerca  = view.findViewById(R.id.chipCerca);
        chipBarato = view.findViewById(R.id.chipBarato);
        chipPopular= view.findViewById(R.id.chipPopular);

        ImageView btnBack   = view.findViewById(R.id.btnBack);
        ImageView btnFiltros= view.findViewById(R.id.btnFiltros);

        // === CATEGORÍA ACTUAL ===
        category = (getArguments() != null)
                ? getArguments().getString(ARG_CATEGORY, "Todo")
                : "Todo";

        tvTituloCategoria.setText(category);
        tvPillCategoria.setText(category);

        // DESPUÉS
        configurarTextoBanner(view, category);

        adapter = new ObjetosCategoriaAdapter(
                requireContext(),
                listaFiltrada,
                objeto -> {
                    // TODO: abrir pantalla de detalles del objeto
                    // aquí puedes lanzar un fragmento o activity
                }
        );

        listView.setAdapter(adapter);
        listView.setEmptyView(emptyText);


        listView.setAdapter(adapter);
        listView.setEmptyView(emptyText);

        // === EVENTOS UI ===
        btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        btnFiltros.setOnClickListener(v -> {
            // TODO: future bottom sheet de filtros avanzados
        });

        configurarBuscador();
        configurarChips();

        // === CARGA DEL BACKEND ===
        cargarObjetos();

        return view;
    }

    private void configurarTextoBanner(View root, String category) {
        TextView tvBannerTitulo = root.findViewById(R.id.tvBannerTitulo);
        TextView tvBannerSub = root.findViewById(R.id.tvBannerSub);

        if (tvBannerTitulo == null || tvBannerSub == null) return;

        switch (category.toLowerCase()) {
            case "eventos":
                tvBannerTitulo.setText("Todo para tu próximo evento");
                tvBannerSub.setText("Renta carpas, bocinas, mesas y más sin complicarte.");
                break;
            case "tecnología":
                tvBannerTitulo.setText("Tech sin tener que comprar");
                tvBannerSub.setText("Cámaras, laptops, proyectores y más cuando los necesites.");
                break;
            case "transporte":
                tvBannerTitulo.setText("Muévete fácil con Roy");
                tvBannerSub.setText("Renta bicis, scooters o más solo por el tiempo que ocupes.");
                break;
            default:
                tvBannerTitulo.setText("Encuentra justo lo que necesitas");
                tvBannerSub.setText("Explora los objetos de esta categoría y renta en segundos.");
                break;
        }
    }


    private void configurarBuscador() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros(s.toString(), extraFilter);
            }

            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void configurarChips() {
        // por defecto: Todo seleccionado
        seleccionarChip(chipTodo);
        extraFilter = ExtraFilter.NONE;

        chipTodo.setOnClickListener(v -> {
            extraFilter = ExtraFilter.NONE;
            seleccionarChip(chipTodo);
            aplicarFiltros(searchBar.getText().toString(), extraFilter);
        });

        chipBarato.setOnClickListener(v -> {
            extraFilter = ExtraFilter.MAS_BARATO;
            seleccionarChip(chipBarato);
            aplicarFiltros(searchBar.getText().toString(), extraFilter);
        });

        chipCerca.setOnClickListener(v -> {
            // A futuro: filtro por ubicación
            extraFilter = ExtraFilter.NONE;
            seleccionarChip(chipCerca);
            aplicarFiltros(searchBar.getText().toString(), extraFilter);
        });

        chipPopular.setOnClickListener(v -> {
            // A futuro: ordenar por popularidad
            extraFilter = ExtraFilter.NONE;
            seleccionarChip(chipPopular);
            aplicarFiltros(searchBar.getText().toString(), extraFilter);
        });
    }

    private void seleccionarChip(Chip seleccionado) {
        // solo marca visualmente uno
        Chip[] chips = new Chip[]{chipTodo, chipCerca, chipBarato, chipPopular};
        for (Chip chip : chips) {
            chip.setChecked(chip == seleccionado);
        }
    }

    private void cargarObjetos() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Objeto>> call = api.getObjetos(); // mismo endpoint que en Postman

        call.enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaOriginal.clear();
                    listaOriginal.addAll(response.body());
                    aplicarFiltros(searchBar.getText().toString(), extraFilter);
                }
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                // podrías mostrar un Toast aquí si quieres
            }
        });
    }

    /**
     * Aplica categoría + texto + filtro extra (chips)
     */
    private void aplicarFiltros(String texto, ExtraFilter extra) {
        texto = texto.toLowerCase().trim();

        listaFiltrada.clear();

        for (Objeto obj : listaOriginal) {
            boolean coincideCategoria;

            if ("Todo".equalsIgnoreCase(category)) {
                coincideCategoria = true;
            } else {
                coincideCategoria = category.equalsIgnoreCase(obj.getCategoria());
            }

            if (!coincideCategoria) continue;

            if (!texto.isEmpty()) {
                String nombre = obj.getNombreObjeto() != null ? obj.getNombreObjeto().toLowerCase() : "";
                String desc   = obj.getDescripcion() != null ? obj.getDescripcion().toLowerCase() : "";

                if (!nombre.contains(texto) && !desc.contains(texto)) {
                    continue;
                }
            }

            listaFiltrada.add(obj);
        }

        // Extra filter: ordenar por precio
        if (extra == ExtraFilter.MAS_BARATO) {
            Collections.sort(listaFiltrada, new Comparator<Objeto>() {
                @Override
                public int compare(Objeto o1, Objeto o2) {
                    return Double.compare(o1.getPrecio(), o2.getPrecio());
                }
            });
        }

        adapter.updateData(new ArrayList<>(listaFiltrada));
        actualizarContador();
    }

    private void actualizarContador() {
        int n = listaFiltrada.size();
        String texto = n + (n == 1 ? " objeto" : " objetos");
        tvResultadosCount.setText(texto);
    }
}
