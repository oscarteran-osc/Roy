package com.example.roy.solicitudes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.roy.R;
import com.example.roy.models.SolicitudRenta;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesArrendatarioFragment extends Fragment
        implements SolicitudesArrendatarioAdapter.OnSolicitudActionListener {

    private ListView listView;
    private SolicitudesArrendatarioAdapter adapter;
    private List<SolicitudRenta> lista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_solis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int idUsuario = 1;
        listView = view.findViewById(R.id.listSolicitudes);

        lista = new ArrayList<>();
        cargarDatosFalsos(); // 🔴 luego lo cambias por API

        adapter = new SolicitudesArrendatarioAdapter(
                requireContext(),
                lista,
                this
        );

        listView.setAdapter(adapter);
    }

    private void cargarDatosFalsos() {
        lista.add(new SolicitudRenta(1, 10, 5, "2025-10-05", "2025-10-06", "APROBADA"));
        lista.add(new SolicitudRenta(2, 11, 6, "2025-10-07", null, "PENDIENTE"));
        lista.add(new SolicitudRenta(3, 12, 7, "2025-10-08", "2025-10-09", "RECHAZADA"));
    }

    // ==================================================
    // ACCIONES DEL ADAPTER
    // ==================================================

    @Override
    public void onPagarClicked(SolicitudRenta solicitud) {
        Intent intent = new Intent(getContext(), PayPalPaymentActivity.class);
        intent.putExtra("idSolicitud", solicitud.getIdSolicitud());
        intent.putExtra("monto", 500.00);
        intent.putExtra("nombreObjeto", "Objeto #" + solicitud.getIdObjeto());
        startActivity(intent);
    }

    @Override
    public void onCancelarClicked(SolicitudRenta solicitud) {
        Toast.makeText(getContext(), "Solicitud cancelada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEliminarClicked(SolicitudRenta solicitud) {
        lista.remove(solicitud);
        adapter.notifyDataSetChanged();
    }
}
