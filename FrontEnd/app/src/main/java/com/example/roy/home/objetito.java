package com.example.roy.home;

import android.app.Dialog;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.roy.R;
import com.example.roy.api.RetrofitClient;
import com.example.roy.api.ApiService;
import com.example.roy.models.Objeto;
import com.example.roy.models.ImagenObjeto;
import com.example.roy.profile.PerfilArrendadorActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class objetito extends Fragment {

    private static final String TAG = "ObjetitoFragment";

    private ImageView imgmain, img1, img2, img3;
    private TextView nombreObj, nomRentador, precio, disponibilidad, categoria, descrip;
    private RatingBar rating;

    private ApiService apiService;
    private int objetoId;
    private Objeto objetoActual;
    private int arrendadorId;

    // Para almacenar las URLs de las imágenes
    private String urlImagen1 = null;
    private String urlImagen2 = null;
    private String urlImagen3 = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistita = inflater.inflate(R.layout.fragment_objetito, container, false);

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

        apiService = RetrofitClient.getClient().create(ApiService.class);

        if (getArguments() != null) {
            objetoId = getArguments().getInt("objetoId", -1);
            Log.d(TAG, "objetoId desde arguments: " + objetoId);
        }

        if (objetoId == -1) {
            Toast.makeText(getContext(), "Error: ID de objeto no válido",
                    Toast.LENGTH_SHORT).show();
            return vistita;
        }

        setupImageZoom();
        setupArrendadorClick();
        cargarDatosObjeto();

        return vistita;
    }

    private void setupImageZoom() {
        imgmain.setOnClickListener(v -> mostrarImagenZoom(imgmain));
        img1.setOnClickListener(v -> cambiarImagenPrincipal(img1, urlImagen1));
        img2.setOnClickListener(v -> cambiarImagenPrincipal(img2, urlImagen2));
        img3.setOnClickListener(v -> cambiarImagenPrincipal(img3, urlImagen3));
    }

    private void mostrarImagenZoom(ImageView imageView) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_zoom);

        ImageView zoomImage = dialog.findViewById(R.id.zoom_image);
        ImageView closeBtn = dialog.findViewById(R.id.close_button);

        zoomImage.setImageDrawable(imageView.getDrawable());
        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void cambiarImagenPrincipal(ImageView miniatura, String url) {
        if (url != null && !url.trim().isEmpty()) {
            Glide.with(this)
                    .load(url.trim())
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .into(imgmain);
        }
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

        // Hacer que se vea como un link
        nomRentador.setPaintFlags(nomRentador.getPaintFlags() |
                android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        nomRentador.setTextColor(getResources().getColor(R.color.blue_primary, null));
    }

    private void navegarAPerfilArrendador(int arrendadorId) {
        Log.d(TAG, "Navegando a perfil del arrendador ID: " + arrendadorId);

        // Abrir Activity independiente del perfil del arrendador
        Intent intent = new Intent(getActivity(), PerfilArrendadorActivity.class);
        intent.putExtra("arrendadorId", arrendadorId);
        startActivity(intent);
    }

    private void cargarDatosObjeto() {
        apiService.getObjetoPorId(objetoId).enqueue(new Callback<Objeto>() {
            @Override
            public void onResponse(Call<Objeto> call, Response<Objeto> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    objetoActual = response.body();
                    Log.d(TAG, "Objeto cargado: " + objetoActual.getNombreObjeto());
                    mostrarDatosObjeto(objetoActual);

                    // Cargar imágenes adicionales del objeto
                    cargarImagenesObjeto(objetoId);
                } else {
                    Toast.makeText(getContext(), "Error al cargar el objeto",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Objeto> call, Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error: ", t);
            }
        });
    }

    private void cargarImagenesObjeto(int objetoId) {
        // Asumiendo que tienes un endpoint para obtener imágenes del objeto
        apiService.getImagenesObjeto(objetoId).enqueue(new Callback<List<ImagenObjeto>>() {
            @Override
            public void onResponse(Call<List<ImagenObjeto>> call, Response<List<ImagenObjeto>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<ImagenObjeto> imagenes = response.body();
                    Log.d(TAG, "Imágenes cargadas: " + imagenes.size());

                    if (imagenes.size() > 0) {
                        urlImagen1 = imagenes.get(0).getUrlImagen();
                        cargarMiniatura(img1, urlImagen1);
                    } else {
                        img1.setVisibility(View.GONE);
                    }

                    if (imagenes.size() > 1) {
                        urlImagen2 = imagenes.get(1).getUrlImagen();
                        cargarMiniatura(img2, urlImagen2);
                    } else {
                        img2.setVisibility(View.GONE);
                    }

                    if (imagenes.size() > 2) {
                        urlImagen3 = imagenes.get(2).getUrlImagen();
                        cargarMiniatura(img3, urlImagen3);
                    } else {
                        img3.setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG, "Error al cargar imágenes: " + response.code());
                    // Ocultar todas las miniaturas si no hay imágenes
                    img1.setVisibility(View.GONE);
                    img2.setVisibility(View.GONE);
                    img3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<ImagenObjeto>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "Error al cargar imágenes: " + t.getMessage());
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);
                img3.setVisibility(View.GONE);
            }
        });
    }

    private void mostrarDatosObjeto(Objeto objeto) {
        nombreObj.setText(objeto.getNombreObjeto());

        // Mostrar nombre del arrendador si existe, sino mostrar "Arrendador"
        String nombreArrendador = objeto.getNomArrendador();
        if (nombreArrendador != null && !nombreArrendador.trim().isEmpty()) {
            nomRentador.setText("Publicado por: " + nombreArrendador);
        } else {
            nomRentador.setText("Ver perfil del arrendador");
        }

        precio.setText("$" + objeto.getPrecio());
        descrip.setText(objeto.getDescripcion());
        categoria.setText(objeto.getCategoria());

        arrendadorId = objeto.getIdUsArrendador();
        Log.d(TAG, "ID del arrendador: " + arrendadorId);

        boolean estaDisponible = esDisponible(objeto.getEstado());
        Log.d(TAG, "Estado recibido: '" + objeto.getEstado() + "' - Disponible: " + estaDisponible);

        if (estaDisponible) {
            disponibilidad.setText("Disponible");
            disponibilidad.setTextColor(getResources().getColor(R.color.disponible, null));
        } else {
            disponibilidad.setText("No disponible");
            disponibilidad.setTextColor(getResources().getColor(R.color.no_disponible, null));
        }

        // Cargar imagen principal
        String urlPrincipal = objeto.getImagenUrl();
        if (urlPrincipal != null && !urlPrincipal.trim().isEmpty()) {
            Glide.with(this)
                    .load(urlPrincipal.trim())
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .into(imgmain);
        } else {
            imgmain.setImageResource(R.drawable.error);
        }

        // Cargar fragment de reseñas
        cargarFragmentResenas(objetoId, objeto.getIdUsArrendador());
    }

    private void cargarFragmentResenas(int objetoId, int idUsReceptor) {
        Log.d(TAG, "Cargando reseñas - objetoId: " + objetoId + ", receptor: " + idUsReceptor);

        resenas resenasFragment = new resenas();
        Bundle args = new Bundle();
        args.putInt("objetoId", objetoId);
        args.putInt("idUsReceptor", idUsReceptor);
        resenasFragment.setArguments(args);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.resenas, resenasFragment)
                .commit();
    }

    private void cargarMiniatura(ImageView imageView, String url) {
        if (url != null && !url.trim().isEmpty()) {
            Glide.with(this)
                    .load(url.trim())
                    .placeholder(R.drawable.error)
                    .error(R.drawable.error)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.error);
        }
    }

    private boolean esDisponible(String estado) {
        if (estado == null) return false;

        String estadoLower = estado.trim().toLowerCase();
        return estadoLower.equals("disponible") || estadoLower.equals("true");
    }

    public static objetito newInstance(int objetoId) {
        objetito fragment = new objetito();
        Bundle args = new Bundle();
        args.putInt("objetoId", objetoId);
        fragment.setArguments(args);
        return fragment;
    }
}