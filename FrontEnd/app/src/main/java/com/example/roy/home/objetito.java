package com.example.roy.home;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.RetrofitClient;
import com.example.roy.api.ApiService;
import com.example.roy.models.Objeto;
import com.example.roy.profile.perfilArrendador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class objetito extends Fragment {

    private ImageView imgmain, img1, img2, img3;
    private TextView nombreObj, nomRentador, precio, disponibilidad, categoria, descrip;
    private RatingBar rating;

    private ApiService apiService;
    private int objetoId;
    private Objeto objetoActual;
    private int arrendadorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita = inflater.inflate(R.layout.fragment_objetito, container, false);

        // Inicializar vistas
        imgmain = vistita.findViewById(R.id.mainimg);
        img1 = vistita.findViewById(R.id.mini1);
        img2 = vistita.findViewById(R.id.mini2);
        img3 = vistita.findViewById(R.id.mini3);
        nombreObj = vistita.findViewById(R.id.nombreobj);
        nomRentador = vistita.findViewById(R.id.nomrentador);
        precio = vistita.findViewById(R.id.precio);
        disponibilidad = vistita.findViewById(R.id.disponibilidad);
        categoria = vistita.findViewById(R.id.categoria);
        descrip = vistita.findViewById(R.id.descrip);
        rating = vistita.findViewById(R.id.rating);

        // Inicializar API service
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Obtener ID del objeto de los argumentos
        if (getArguments() != null) {
            objetoId = getArguments().getInt("objetoId", -1);
        }

        // Configurar zoom en imagen principal
        setupImageZoom();

        // Configurar click en nombre del arrendador
        setupArrendadorClick();

        // Cargar datos del objeto
        cargarDatosObjeto();

        return vistita;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            // Crear el fragmento de reseñas con el objetoId
            resenas resenasFragment = new resenas();
            Bundle args = new Bundle();
            args.putInt("objetoId", objetoId);
            resenasFragment.setArguments(args);

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.resenas, resenasFragment)
                    .commit();
        }
    }

    private void setupImageZoom() {
        // Zoom en imagen principal
        imgmain.setOnClickListener(v -> mostrarImagenZoom((ImageView) v));

        // Click en miniaturas
        img1.setOnClickListener(v -> {
            cambiarImagenPrincipal((ImageView) v);
        });

        img2.setOnClickListener(v -> {
            cambiarImagenPrincipal((ImageView) v);
        });

        img3.setOnClickListener(v -> {
            cambiarImagenPrincipal((ImageView) v);
        });
    }

    private void mostrarImagenZoom(ImageView imageView) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_zoom);

        ImageView zoomImage = dialog.findViewById(R.id.zoom_image);
        ImageView closeBtn = dialog.findViewById(R.id.close_button);

        // Copiar la imagen actual
        zoomImage.setImageDrawable(imageView.getDrawable());

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        // Hacer el diálogo transparente
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void cambiarImagenPrincipal(ImageView miniatura) {
        imgmain.setImageDrawable(miniatura.getDrawable());
    }

    private void setupArrendadorClick() {
        nomRentador.setOnClickListener(v -> {
            if (arrendadorId > 0) {
                navegarAPerfilArrendador(arrendadorId);
            } else {
                Toast.makeText(getContext(), "Información del arrendador no disponible",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Hacer que se vea como un enlace
        nomRentador.setPaintFlags(nomRentador.getPaintFlags() |
                android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        nomRentador.setTextColor(getResources().getColor(R.color.colorPrimary, null));
    }

    private void navegarAPerfilArrendador(int arrendadorId) {
        Fragment perfilFragment = new perfilArrendador();
        Bundle args = new Bundle();
        args.putInt("arrendadorId", arrendadorId);
        perfilFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, perfilFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void cargarDatosObjeto() {
        if (objetoId == -1) {
            Toast.makeText(getContext(), "Error: ID de objeto no válido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getObjetoPorId(objetoId).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    objetoActual = response.body();
                    mostrarDatosObjeto(objetoActual);
                } else {
                    Toast.makeText(getContext(), "Error al cargar el objeto",
                            Toast.LENGTH_SHORT).show();
                    Log.e("objetito", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("objetito", "Error: ", t);
            }
        });
    }

    private void mostrarDatosObjeto(Objeto objeto) {
        // Datos básicos
        nombreObj.setText(objeto.getNombre());
        nomRentador.setText(objeto.getNombreArrendador());
        precio.setText("$" + objeto.getPrecio());
        descrip.setText(objeto.getDescripcion());
        categoria.setText(objeto.getCategoria());

        // ID del arrendador para navegación
        arrendadorId = objeto.getArrendadorId();

        // Disponibilidad
        if (objeto.isDisponible()) {
            disponibilidad.setText("Disponible");
            disponibilidad.setTextColor(getResources().getColor(R.color.disponible, null));
        } else {
            disponibilidad.setText("No disponible");
            disponibilidad.setTextColor(getResources().getColor(R.color.no_disponible, null));
        }

        // Rating (calculado desde las reseñas)
        rating.setRating(objeto.getCalificacionPromedio());
        rating.setIsIndicator(true); // Solo lectura

        // Cargar imágenes con Glide
        if (objeto.getImagenPrincipal() != null && !objeto.getImagenPrincipal().isEmpty()) {
            Glide.with(this)
                    .load(objeto.getImagenPrincipal())
                    .placeholder(R.drawable.casacampania)
                    .error(R.drawable.casacampania)
                    .into(imgmain);
        }

        // Cargar miniaturas
        if (objeto.getImagenes() != null && objeto.getImagenes().size() > 0) {
            cargarMiniatura(img1, objeto.getImagenes().get(0));
            if (objeto.getImagenes().size() > 1) {
                cargarMiniatura(img2, objeto.getImagenes().get(1));
            }
            if (objeto.getImagenes().size() > 2) {
                cargarMiniatura(img3, objeto.getImagenes().get(2));
            }
        }
    }

    private void cargarMiniatura(ImageView imageView, String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.casacampania)
                .error(R.drawable.casacampania)
                .into(imageView);
    }

    // Método estático para crear instancia con argumentos
    public static objetito newInstance(int objetoId) {
        objetito fragment = new objetito();
        Bundle args = new Bundle();
        args.putInt("objetoId", objetoId);
        fragment.setArguments(args);
        return fragment;
    }
}