package com.example.roy.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
import com.airbnb.lottie.LottieAnimationView;
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
    private EditText searchBar;
    private View searchingState;
    private TextView searchingTitle, searchingSubtitle;

    private enum UiState { LIST, EMPTY, SEARCHING }
    private UiState uiState = UiState.LIST;

    // Para evitar parpadeos cuando escribes rápido:
    private android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchingRunnable;

    private TextView tvTituloCategoria;
    private TextView tvPillCategoria;
    private TextView tvResultadosCount;

    private Chip chipTodo, chipCerca, chipBarato, chipPopular;

    private View emptyState;
    private TextView emptyTitle, emptySubtitle;
    private LottieAnimationView lottieEmpty;
    private LottieAnimationView lottieSearching;

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

        // Bind views
        tvTituloCategoria = rootView.findViewById(R.id.tvTituloCategoria);
        tvPillCategoria = rootView.findViewById(R.id.tvPillCategoria);
        tvResultadosCount = rootView.findViewById(R.id.tvResultadosCount);

        listView = rootView.findViewById(R.id.lvObjetosCategoria);
        searchBar = rootView.findViewById(R.id.etBuscarObjeto);

        chipTodo = rootView.findViewById(R.id.chipTodo);
        chipCerca = rootView.findViewById(R.id.chipCerca);
        chipBarato = rootView.findViewById(R.id.chipBarato);
        chipPopular = rootView.findViewById(R.id.chipPopular);

        emptyState = rootView.findViewById(R.id.empty_state);
        emptyTitle = rootView.findViewById(R.id.empty_title);
        emptySubtitle = rootView.findViewById(R.id.empty_subtitle);
        lottieEmpty = rootView.findViewById(R.id.lottieEmpty);

        searchingState = rootView.findViewById(R.id.searching_state);
        searchingTitle = rootView.findViewById(R.id.searching_title);
        searchingSubtitle = rootView.findViewById(R.id.searching_subtitle);
        lottieSearching = rootView.findViewById(R.id.lottieSearching);

        ImageView btnBack = rootView.findViewById(R.id.btnBack);
        ImageView btnFiltros = rootView.findViewById(R.id.btnFiltros);

        category = (getArguments() != null)
                ? getArguments().getString(ARG_CATEGORY, "Todo")
                : "Todo";

        // Si viene buscar:
        if (category != null && category.toLowerCase().startsWith("buscar:")) {
            queryBusqueda = category.substring("buscar:".length()).trim();
            tvTituloCategoria.setText("Resultados");
            tvPillCategoria.setText("Búsqueda");
        } else {
            tvTituloCategoria.setText(category);
            tvPillCategoria.setText(category);
        }

        // Banner text (con Transporte oculto)
        configurarTextoBanner(rootView, category);

        // Adapter (ListView)
        adapter = new ObjetosCategoriaAdapter(
                requireContext(),
                listaFiltrada,
                objeto -> {
                    // ✅ Abrir detalle del objeto con botón de solicitud
                    Intent intent = new Intent(requireContext(), com.example.roy.home.Objetoo.class);
                    intent.putExtra("objetoId", objeto.getIdObjeto());
                    startActivity(intent);
                }
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            com.example.roy.models.Objeto obj = (com.example.roy.models.Objeto) adapter.getItem(position);
            if (obj != null) {
                Intent intent = new Intent(requireContext(), com.example.roy.home.Objetoo.class);
                intent.putExtra("objetoId", obj.getIdObjeto());
                startActivity(intent);
            }
        });

        listView.setVerticalScrollBarEnabled(true);

        btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed()
        );

        btnFiltros.setOnClickListener(v -> {
            // TODO
        });

        configurarChips();
        configurarBuscador(); // watcher + enter

        // Si viene búsqueda: metemos el texto pero sin disparar filtros antes de cargar data
        if (!queryBusqueda.isEmpty()) {
            searchBar.removeTextChangedListener(searchWatcher);
            searchBar.setText(queryBusqueda);
            searchBar.setSelection(queryBusqueda.length());
            searchBar.addTextChangedListener(searchWatcher);
        }

        cargarObjetos();

        return rootView;
    }

    private void setUiState(UiState state) {
        uiState = state;

        if (listView != null) listView.setVisibility(state == UiState.LIST ? View.VISIBLE : View.GONE);
        if (emptyState != null) emptyState.setVisibility(state == UiState.EMPTY ? View.VISIBLE : View.GONE);
        if (searchingState != null) searchingState.setVisibility(state == UiState.SEARCHING ? View.VISIBLE : View.GONE);

        // ✅ Control del lottie de searching
        if (lottieSearching != null) {
            if (state == UiState.SEARCHING) {
                lottieSearching.setVisibility(View.VISIBLE);
                lottieSearching.playAnimation();
            } else {
                lottieSearching.cancelAnimation();
                lottieSearching.setVisibility(View.GONE);
            }
        }

        // ✅ Control del lottie empty (si aplica)
        if (lottieEmpty != null) {
            if (state == UiState.EMPTY) lottieEmpty.playAnimation();
            else lottieEmpty.cancelAnimation();
        }
    }

    private void configurarTextoBanner(View root, String category) {
        if (root == null) return;

        TextView tvBannerTitulo = root.findViewById(R.id.tvBannerTitulo);
        TextView tvBannerSub = root.findViewById(R.id.tvBannerSub);
        ImageView imgBanner = root.findViewById(R.id.imgBannerCategoria);

        if (tvBannerTitulo == null || tvBannerSub == null || imgBanner == null) return;

        // Caso búsqueda
        if (category != null && category.toLowerCase().startsWith("buscar:")) {
            tvBannerTitulo.setText("Resultados de tu búsqueda");
            tvBannerSub.setText("Explora lo que encontramos para ti.");
            imgBanner.setImageResource(R.drawable.ic_search_big);
            return;
        }

        String cat = normalizar(category);

        // Transporte oculto (si llegara)
        if (cat.equals("transporte")) {
            tvBannerTitulo.setText("Explora objetos disponibles");
            tvBannerSub.setText("Renta fácil y rápido.");
            imgBanner.setImageResource(R.drawable.bg_banner_categoria);
            return;
        }

        switch (cat) {
            case "eventos":
                tvBannerTitulo.setText("Todo para tu próximo evento");
                tvBannerSub.setText("Carpas, bocinas, mesas y más sin complicarte.");
                imgBanner.setImageResource(R.drawable.banner_eventos);
                break;

            case "tecnologia":
                tvBannerTitulo.setText("Tech sin tener que comprar");
                tvBannerSub.setText("Cámaras, laptops, proyectores y más cuando los necesites.");
                imgBanner.setImageResource(R.drawable.banner_tecnologia);
                break;

            case "herramientas":
                tvBannerTitulo.setText("Herramientas listas para el trabajo");
                tvBannerSub.setText("Taladros, escaleras y más sin comprar todo.");
                imgBanner.setImageResource(R.drawable.banner_herramientas);
                break;

            case "hogar y muebles":
            case "hogar":
                tvBannerTitulo.setText("Hogar con estilo, sin gastar de más");
                tvBannerSub.setText("Renta muebles o decoración para cada ocasión.");
                imgBanner.setImageResource(R.drawable.banner_hogar);
                break;

            case "deportes y aire libre":
            case "deportes":
                tvBannerTitulo.setText("Para tus aventuras al aire libre");
                tvBannerSub.setText("Carpas, bicicletas, equipo deportivo y más.");
                imgBanner.setImageResource(R.drawable.banner_deportes);
                break;

            case "electrodomesticos": // ✅ sin acento
                tvBannerTitulo.setText("Electrodomesticos cuando los necesitas");
                tvBannerSub.setText("Licuadoras, microondas y más sin comprar.");
                imgBanner.setImageResource(R.drawable.banner_electrodomesticos);
                break;

            case "ropa y accesorios":
            case "ropa":
                tvBannerTitulo.setText("Outfits para cada ocasión");
                tvBannerSub.setText("Vestidos, trajes, accesorios y más sin gastar de más.");
                imgBanner.setImageResource(R.drawable.banner_ropa);
                break;

            case "juegos y entretenimiento":
            case "juegos":
                tvBannerTitulo.setText("Diversión para compartir");
                tvBannerSub.setText("Consolas, juegos y más para tus reuniones.");
                imgBanner.setImageResource(R.drawable.banner_juegos);
                break;

            case "mascotas":
                tvBannerTitulo.setText("Todo para consentir a tu mascota");
                tvBannerSub.setText("Accesorios, juguetes y más.");
                imgBanner.setImageResource(R.drawable.banner_mascotas);
                break;

            case "todo":
                tvBannerTitulo.setText("Encuentra justo lo que necesitas");
                tvBannerSub.setText("Explora objetos listos para rentar.");
                imgBanner.setImageResource(R.drawable.banner_image);
                break;

            default:
                tvBannerTitulo.setText("Descubre cosas increíbles cerca de ti");
                tvBannerSub.setText("Explora esta categoría y renta en segundos.");
                imgBanner.setImageResource(R.drawable.bg_banner_categoria);
                break;
        }
    }

    private void configurarBuscador() {
        searchWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = s.toString();

                // ✅ Si hay texto, muestra searching inmediato
                if (!texto.trim().isEmpty()) {
                    setUiState(UiState.SEARCHING);
                    if (searchingTitle != null) searchingTitle.setText("Buscando…");
                    if (searchingSubtitle != null) searchingSubtitle.setText("Filtrando resultados…");
                }

                // ✅ Espera poquito para que se vea el searching y luego filtra
                if (searchingRunnable != null) handler.removeCallbacks(searchingRunnable);
                searchingRunnable = () -> aplicarFiltros(texto, extraFilter);
                handler.postDelayed(searchingRunnable, 220);
            }

        };
        searchBar.addTextChangedListener(searchWatcher);

        // ✅ ENTER: fuerza el filtro y el estado final (sin quedarse en searching)
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN);
            if (isEnter) {
                if (searchingRunnable != null) handler.removeCallbacks(searchingRunnable);
                aplicarFiltros(searchBar.getText().toString(), extraFilter);
            }
            return false;
        });
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

        setUiState(UiState.SEARCHING);
        if (searchingTitle != null) searchingTitle.setText("Buscando…");
        if (searchingSubtitle != null) searchingSubtitle.setText("Cargando objetos para filtrar.");

        call.enqueue(new Callback<List<Objeto>>() {
            @Override
            public void onResponse(Call<List<Objeto>> call, Response<List<Objeto>> response) {
                if (!isAdded()) return;

                listaOriginal.clear();
                if (response.isSuccessful() && response.body() != null) {
                    listaOriginal.addAll(response.body());
                }

                aplicarFiltros(searchBar.getText().toString(), extraFilter);
            }

            @Override
            public void onFailure(Call<List<Objeto>> call, Throwable t) {
                if (!isAdded()) return;
                listaOriginal.clear();
                aplicarFiltros(searchBar.getText().toString(), extraFilter);
            }
        });
    }

    private void aplicarFiltros(String texto, ExtraFilter extra) {
        if (!isAdded() || adapter == null) return;
        if (searchingRunnable != null) handler.removeCallbacks(searchingRunnable);

        String textoBusqueda = (texto == null) ? "" : texto.toLowerCase().trim();
        listaFiltrada.clear();

        boolean esBusquedaGlobal = (category != null && category.toLowerCase().startsWith("buscar:"));
        if (esBusquedaGlobal && (textoBusqueda.isEmpty())) {
            textoBusqueda = queryBusqueda.toLowerCase().trim();
        }

        String categoriaActual = (category == null) ? "" : category;

        for (Objeto obj : listaOriginal) {

            // 🚫 EXCLUIR Transporte de TODO el catálogo mostrado
            String catObjRaw = (obj.getCategoria() != null) ? obj.getCategoria() : "";
            String catObjNorm = normalizar(catObjRaw);
            if (catObjNorm.equals("transporte")) continue;

            // 1) categoría
            boolean coincideCategoria;
            if (esBusquedaGlobal || "Todo".equalsIgnoreCase(categoriaActual)) {
                coincideCategoria = true;
            } else {
                coincideCategoria = categoriaActual.equalsIgnoreCase(catObjRaw);
            }
            if (!coincideCategoria) continue;

            // 2) texto
            if (!textoBusqueda.isEmpty()) {
                String nombre = (obj.getNombreObjeto() != null) ? obj.getNombreObjeto().toLowerCase() : "";
                String desc   = (obj.getDescripcion() != null) ? obj.getDescripcion().toLowerCase() : "";
                if (!nombre.contains(textoBusqueda) && !desc.contains(textoBusqueda)) continue;
            }

            listaFiltrada.add(obj);
        }

       if (extra == ExtraFilter.MAS_BARATO) {
            Collections.sort(listaFiltrada, new Comparator<Objeto>() {
                @Override
                public int compare(Objeto o1, Objeto o2) {
                    double p1 = o1.getPrecio();
                    double p2 = o2.getPrecio();
                    return Double.compare(p1, p2);
                }

            });
        }
        adapter.updateData(new ArrayList<>(listaFiltrada));
        actualizarContador();
        mostrarEmptySiAplica(textoBusqueda);

        if (listaFiltrada.isEmpty()) setUiState(UiState.EMPTY);
        else setUiState(UiState.LIST);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnter = (event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN);

            if (isEnter) {
                if (searchingRunnable != null) handler.removeCallbacks(searchingRunnable);
                aplicarFiltros(searchBar.getText().toString(), extraFilter);
            }
            return false;
        });

    }

    private void actualizarContador() {
        if (tvResultadosCount == null) return;
        int n = listaFiltrada.size();
        tvResultadosCount.setText(n + (n == 1 ? " objeto" : " objetos"));
    }

    private void mostrarEmptySiAplica(String textoBusqueda) {
        if (emptyTitle == null || emptySubtitle == null) return;

        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            emptyTitle.setText("Ups… no encontramos \"" + textoBusqueda + "\"");
            emptySubtitle.setText("Prueba con otra palabra o revisa los filtros.");
        } else if (category != null && category.toLowerCase().startsWith("buscar:")) {
            emptyTitle.setText("No encontramos resultados");
            emptySubtitle.setText("Intenta una búsqueda diferente.");
        } else {
            emptyTitle.setText("No hay objetos en esta categoría");
            emptySubtitle.setText("Aún no hay publicaciones aquí. Explora otra categoría.");
        }
    }

    private String normalizar(String s) {
        if (s == null) return "";
        s = s.trim().toLowerCase();
        s = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return s;
    }
}

