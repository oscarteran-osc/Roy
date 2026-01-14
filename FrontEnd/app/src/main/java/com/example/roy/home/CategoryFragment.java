package com.example.roy.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import com.example.roy.models.Objeto;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    public static CategoryFragment newInstance(String category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    private String category;           // "Eventos" o "buscar:taladro"
    private String queryBusqueda = ""; // si viene buscar:

    private View rootView;

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

    private enum ExtraFilter { NONE, MAS_BARATO }
    private ExtraFilter extraFilter = ExtraFilter.NONE;

    private TextWatcher searchWatcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_category, container, false);

        tvTituloCategoria  = rootView.findViewById(R.id.tvTituloCategoria);
        tvPillCategoria    = rootView.findViewById(R.id.tvPillCategoria);
        tvResultadosCount  = rootView.findViewById(R.id.tvResultadosCount);
        listView           = rootView.findViewById(R.id.lvObjetosCategoria);
        emptyText          = rootView.findViewById(R.id.empty_text);
        searchBar          = rootView.findViewById(R.id.etBuscarObjeto);

        chipTodo   = rootView.findViewById(R.id.chipTodo);
        chipCerca  = rootView.findViewById(R.id.chipCerca);
        chipBarato = rootView.findViewById(R.id.chipBarato);
        chipPopular= rootView.findViewById(R.id.chipPopular);

        ImageView btnBack   = rootView.findViewById(R.id.btnBack);
        ImageView btnFiltros= rootView.findViewById(R.id.btnFiltros);

        category = (getArguments() != null)
                ? getArguments().getString(ARG_CATEGORY, "Todo")
                : "Todo";

        // ====== SI VIENE DESDE INICIO CON buscar: ======
        if (category != null && category.toLowerCase().startsWith("buscar:")) {
            queryBusqueda = category.substring("buscar:".length()).trim();
            tvTituloCategoria.setText("Resultados");
            tvPillCategoria.setText("Búsqueda");
        } else {
            tvTituloCategoria.setText(category);
            tvPillCategoria.setText(category);
        }

        // Banner SOLO aquí (con rootView válido)
        configurarTextoBanner(rootView, category);

        adapter = new ObjetosCategoriaAdapter(
                requireContext(),
                listaFiltrada,
                objeto -> {
                    // TODO: abrir detalles
                }
        );

        listView.setAdapter(adapter);
        listView.setEmptyView(emptyText);

        btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        btnFiltros.setOnClickListener(v -> {
            // TODO
        });

        configurarChips();
        configurarBuscador(); // ✅ primero watcher

        // ✅ Si viene búsqueda: ponemos el texto SIN disparar el watcher raro
        if (!queryBusqueda.isEmpty()) {
            // quitamos watcher para no ejecutar filtros cuando aún no hay datos
            searchBar.removeTextChangedListener(searchWatcher);
            searchBar.setText(queryBusqueda);
            searchBar.setSelection(queryBusqueda.length());
            searchBar.addTextChangedListener(searchWatcher);
        }

        cargarObjetos();

        return rootView;
    }

    private void configurarTextoBanner(View root, String category) {
        if (root == null) return;

        TextView tvBannerTitulo = root.findViewById(R.id.tvBannerTitulo);
        TextView tvBannerSub = root.findViewById(R.id.tvBannerSub);
        if (tvBannerTitulo == null || tvBannerSub == null) return;

        if (category != null && category.toLowerCase().startsWith("buscar:")) {
            tvBannerTitulo.setText("Resultados de tu búsqueda");
            tvBannerSub.setText("Explora lo que encontramos para ti.");
            return;
        }

        String cat = category != null ? category.toLowerCase() : "todo";

        switch (cat) {
            case "eventos":
                tvBannerTitulo.setText("Todo para tu próximo evento");
                tvBannerSub.setText("Renta carpas, bocinas, mesas y más sin complicarte.");
                break;
            case "tecnología":
            case "tecnologia":
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
        searchWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros(s.toString(), extraFilter);
            }
        };
        searchBar.addTextChangedListener(searchWatcher);
    }

    private void configurarChips() {
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
            extraFilter = ExtraFilter.NONE;
            seleccionarChip(chipCerca);
            aplicarFiltros(searchBar.getText().toString(), extraFilter);
        });

        chipPopular.setOnClickListener(v -> {
            extraFilter = ExtraFilter.NONE;
            seleccionarChip(chipPopular);
            aplicarFiltros(searchBar.getText().toString(), extraFilter);
        });
    }

    private void seleccionarChip(Chip seleccionado) {
        Chip[] chips = new Chip[]{chipTodo, chipCerca, chipBarato, chipPopular};
        for (Chip chip : chips) chip.setChecked(chip == seleccionado);
    }

    private void cargarObjetos() {
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Objeto>> call = api.getObjetos();

        call.enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    listaOriginal.clear();
                    listaOriginal.addAll(response.body());

                    // ✅ Aplica filtros con lo que esté en searchBar
                    aplicarFiltros(searchBar.getText().toString(), extraFilter);
                }
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                // Toast opcional
            }
        });
    }

    private void aplicarFiltros(String texto, ExtraFilter extra) {
        if (!isAdded() || adapter == null) return;

        String textoBusqueda = (texto == null) ? "" : texto.toLowerCase().trim();

        listaFiltrada.clear();

        boolean esBusquedaGlobal = (category != null && category.toLowerCase().startsWith("buscar:"));
        if (esBusquedaGlobal) {
            textoBusqueda = queryBusqueda.toLowerCase().trim();
        }

        for (Objeto obj : listaOriginal) {

            // 1) categoría
            boolean coincideCategoria;
            if (esBusquedaGlobal || "Todo".equalsIgnoreCase(category)) {
                coincideCategoria = true;
            } else {
                String catObj = (obj.getCategoria() != null) ? obj.getCategoria() : "";
                coincideCategoria = category.equalsIgnoreCase(catObj);
            }
            if (!coincideCategoria) continue;

            // 2) texto
            if (!textoBusqueda.isEmpty()) {
                String nombre = (obj.getNombreObjeto() != null) ? obj.getNombreObjeto().toLowerCase() : "";
                String desc   = (obj.getDescripcion() != null) ? obj.getDescripcion().toLowerCase() : "";
                if (!nombre.contains(textoBusqueda) && !desc.contains(textoBusqueda)) continue;
            }

            // ✅ agregar sí o sí
            listaFiltrada.add(obj);
        }

        if (extra == ExtraFilter.MAS_BARATO) {
            Collections.sort(listaFiltrada, new Comparator<Objeto>() {
                @Override
                public int compare(Objeto o1, Objeto o2) {
                    double p1 = (o1.getPrecio() != null) ? o1.getPrecio() : 0.0;
                    double p2 = (o2.getPrecio() != null) ? o2.getPrecio() : 0.0;
                    return Double.compare(p1, p2);
                }
            });
        }

        adapter.updateData(new ArrayList<>(listaFiltrada));
        actualizarContador();
    }

    private void actualizarContador() {
        if (tvResultadosCount == null) return;
        int n = listaFiltrada.size();
        tvResultadosCount.setText(n + (n == 1 ? " objeto" : " objetos"));
    }
}
