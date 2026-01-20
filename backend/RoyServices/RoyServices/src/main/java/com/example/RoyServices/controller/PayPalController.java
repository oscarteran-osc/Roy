package com.example.RoyServices.controller;

import com.example.RoyServices.dto.PayPalOrderResponse;
import com.example.RoyServices.model.SolicitudRenta;
import com.example.RoyServices.repository.SolicitudRentaRepository;
import com.example.RoyServices.service.PayPalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@CrossOrigin(origins = "*")
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private SolicitudRentaRepository solicitudRentaRepository; // ✅ Inyección del repositorio

    private static final String SUCCESS_URL = "com.example.roy://paypalpay";
    private static final String CANCEL_URL = "com.example.roy://paypalcancel";

    @PostMapping("/crear-orden")
    public ResponseEntity<?> crearOrden(
            @RequestParam Double total,
            @RequestParam(defaultValue = "MXN") String moneda,
            @RequestParam(defaultValue = "Renta de objeto") String descripcion
    ) {
        try {
            Payment payment = payPalService.crearPago(
                    total,
                    moneda,
                    descripcion,
                    CANCEL_URL,
                    SUCCESS_URL
            );

            String approvalUrl = "";
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    approvalUrl = link.getHref();
                    break;
                }
            }

            PayPalOrderResponse response = new PayPalOrderResponse(
                    payment.getId(),
                    payment.getState(),
                    approvalUrl
            );

            return ResponseEntity.ok(response);

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear orden: " + e.getMessage());
        }
    }

    @PostMapping("/capturar-pago")
    public ResponseEntity<?> capturarPago(@RequestBody Map<String, Object> request) {
        try {
            String paymentId = (String) request.get("paymentId");
            String payerId = (String) request.get("payerId");
            Integer idSolicitud = Integer.parseInt(request.get("idSolicitud").toString());

            System.out.println("🔄 Capturando pago:");
            System.out.println("   Payment ID: " + paymentId);
            System.out.println("   Payer ID: " + payerId);
            System.out.println("   ID Solicitud: " + idSolicitud);

            // ✅ Ejecutar el pago en PayPal
            Payment payment = payPalService.ejecutarPago(paymentId, payerId, idSolicitud);

            if (payment.getState().equals("approved")) {
                // ✅ ACTUALIZAR el estado de la solicitud ANTES de retornar
                SolicitudRenta solicitud = solicitudRentaRepository.findById(idSolicitud)
                        .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

                solicitud.setEstado("PAGADA");
                solicitudRentaRepository.save(solicitud);

                System.out.println("✅ Estado actualizado a PAGADA para solicitud #" + idSolicitud);

                // Preparar respuesta
                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Pago completado exitosamente");
                respuesta.put("paymentId", payment.getId());
                respuesta.put("estado", payment.getState());
                respuesta.put("monto", payment.getTransactions().get(0).getAmount().getTotal());
                respuesta.put("moneda", payment.getTransactions().get(0).getAmount().getCurrency());

                return ResponseEntity.ok(respuesta);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El pago no fue aprobado"));
            }

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al capturar pago: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado: " + e.getMessage()));
        }
    }
}