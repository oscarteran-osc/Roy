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
import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.ApiService;
import com.example.roy.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {

    private static final String TAG = "HomeFragment";

    // Categorías (de tu XML)
    private TextView categoryTodo, categoryEventos, categoryTecnologia, categoryTransporte, categoryHerramientas;

    // Botones
    private TextView explorarMasBtn; // en tu XML es TextView
    private Button verMasBtn;        // en tu XML es Button

    // Cards horizontales
    private LinearLayout serviceItem1, serviceItem2, serviceItem3, serviceItem4;

    // UI top
    private ImageView menuIcon;
    private EditText searchBar;

    // Featured
    private CardView featuredCard;
    private TextView tituloDestacado, descDestacado;
    private ImageView imagenDestacado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "onCreateView: inflated fragment_home");

        bindViews(view);
        setupClicks();
        setupSearch();

        // Cargar destacado desde backend
        cargarDestacadoAleatorio();

        return view;
    }

    private void bindViews(View view) {
        // Categorías
        categoryTodo = view.findViewById(R.id.category_todo);
        categoryEventos = view.findViewById(R.id.category_eventos);
        categoryTecnologia = view.findViewById(R.id.category_tecnologia);
        categoryTransporte = view.findViewById(R.id.category_transporte);
        categoryHerramientas = view.findViewById(R.id.category_herramientas);

        // Botones
        explorarMasBtn = view.findViewById(R.id.explorar_mas_btn);
        verMasBtn = view.findViewById(R.id.ver_mas_btn);

        // Servicios
        serviceItem1 = view.findViewById(R.id.service_item_1);
        serviceItem2 = view.findViewById(R.id.service_item_2);
        serviceItem3 = view.findViewById(R.id.service_item_3);
        serviceItem4 = view.findViewById(R.id.service_item_4);

        // Top
        menuIcon = view.findViewById(R.id.menu_icon);
        searchBar = view.findViewById(R.id.search_bar);

        // Destacado
        featuredCard = view.findViewById(R.id.featured_card);
        if (featuredCard != null) {
            tituloDestacado = featuredCard.findViewById(R.id.titulo_destacado);
            descDestacado = featuredCard.findViewById(R.id.desc_destacado);
            imagenDestacado = featuredCard.findViewById(R.id.imagen_destacado);
        }
    }

    private void setupClicks() {
        // Categorías
        if (categoryTodo != null) categoryTodo.setOnClickListener(v -> loadCategoryFragment("Todo"));
        if (categoryEventos != null) categoryEventos.setOnClickListener(v -> loadCategoryFragment("Eventos"));
        if (categoryTecnologia != null) categoryTecnologia.setOnClickListener(v -> loadCategoryFragment("Tecnología"));
        if (categoryTransporte != null) categoryTransporte.setOnClickListener(v -> loadCategoryFragment("Transporte"));
        if (categoryHerramientas != null) categoryHerramientas.setOnClickListener(v -> loadCategoryFragment("Herramientas"));

        // Explorar
        if (explorarMasBtn != null) explorarMasBtn.setOnClickListener(v -> loadDetailFragment("Explorar"));

        // Ver más (botón del destacado)
        if (verMasBtn != null) verMasBtn.setOnClickListener(v -> openObjetoActivity());

        // Cards “Te puede servir para...”
        if (serviceItem1 != null) serviceItem1.setOnClickListener(v -> loadDetailFragment("Proyecto"));
        if (serviceItem2 != null) serviceItem2.setOnClickListener(v -> loadDetailFragment("Escapada"));
        if (serviceItem3 != null) serviceItem3.setOnClickListener(v -> loadDetailFragment("Reparar"));
        if (serviceItem4 != null) serviceItem4.setOnClickListener(v -> loadDetailFragment("Evento"));

        // Icono menú (por si luego lo usas)
        if (menuIcon != null) {
            menuIcon.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "Menú (pendiente)", Toast.LENGTH_SHORT).show()
            );
        }

        // Si quieres que la card completa también abra:
        if (featuredCard != null) {
            featuredCard.setOnClickListener(v -> openObjetoActivity());
        }
    }

    private void setupSearch() {
        if (searchBar == null) return;

        // Que el enter sea “buscar”
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

    private void loadCategoryFragment(String category) {
        Log.d(TAG, "loadCategoryFragment: " + category);

        CategoryFragment categoryFragment = CategoryFragment.newInstance(category);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadDetailFragment(String detail) {
        Log.d(TAG, "loadDetailFragment: " + detail);

        DetailFragment detailFragment = DetailFragment.newInstance(detail);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void abrirResultadosBusqueda(String texto) {
        Log.d(TAG, "abrirResultadosBusqueda: " + texto);

        CategoryFragment categoryFragment = CategoryFragment.newInstance("buscar:" + texto);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, categoryFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openObjetoActivity() {
        Log.d(TAG, "openObjetoActivity");

        // ✅ IMPORTANTE: Activity real, NO el modelo
        Intent intent = new Intent(requireContext(), Objetoo.class);
        startActivity(intent);
    }

    private void cargarDestacadoAleatorio() {
        if (tituloDestacado == null || descDestacado == null) {
            Log.w(TAG, "cargarDestacadoAleatorio: featured views are null (¿featured_card existe?)");
            return;
        }

        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getDestacado().enqueue(new Callback<com.example.roy.models.Objeto>() {
            @Override
            public void onResponse(Call<com.example.roy.models.Objeto> call, Response<com.example.roy.models.Objeto> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    com.example.roy.models.Objeto o = response.body();
                    tituloDestacado.setText(o.getNombre());
                    descDestacado.setText(o.getDescripcion());
                    String url = o.getImagenPrincipal(); // OJO: tu modelo Android debe tener imagenUrl

                    if (url != null) {
                        url = url.trim();
                    }

                    if (url != null && !url.isEmpty()) {
                        if (url.startsWith("/")) url = "http://10.0.2.2:8080" + url;
                        else if (!url.startsWith("http")) url = "http://10.0.2.2:8080/" + url;

                        Glide.with(requireContext())
                                .load(url)
                                .placeholder(R.drawable.tent_image)
                                .error(R.drawable.tent_image)
                                .into(imagenDestacado);
                    }
                    Log.d(TAG, "Destacado cargado: " + o.getNombre());
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
}
