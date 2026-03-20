package com.example.RoyServices.service;

import com.example.RoyServices.model.Transaccion;
import com.example.RoyServices.repository.TransaccionRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PayPalService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);

    @Autowired
    private APIContext apiContext;

    @Autowired
    private TransaccionRepository transaccionRepository;

    // Helper: PayPal casi siempre espera "123.45" (punto y 2 decimales)
    private String toPayPalTotal(Double total) {
        if (total == null) throw new IllegalArgumentException("total is null");

        BigDecimal bd = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);

        // Forzamos punto decimal aunque tu PC esté en español
        String formatted = String.format(Locale.US, "%.2f", bd);

        // Validación simple (te ayuda a detectar rápido si algo raro se coló)
        if (!formatted.matches("^\\d+(\\.\\d{2})$")) {
            throw new IllegalArgumentException("Invalid PayPal total format: " + formatted);
        }

        return formatted;
    }

    public Payment crearPago(
            Double total,
            String moneda,
            String descripcion,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(moneda); // ej "MXN" o "USD"
        amount.setTotal(toPayPalTotal(total));

        Transaction transaction = new Transaction();
        transaction.setDescription(descripcion);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        // Log útil para ver EXACTO qué monto está saliendo
        logger.info("🧾 PayPal crearPago -> currency={}, total={}", moneda, amount.getTotal());

        return payment.create(apiContext);
    }

    public Payment ejecutarPago(String paymentId, String payerId, Integer idSolicitud) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        if ("approved".equalsIgnoreCase(executedPayment.getState())) {
            guardarTransaccion(executedPayment, idSolicitud);
        }

        return executedPayment;
    }

    private void guardarTransaccion(Payment payment, Integer idSolicitud) {
        try {
            Transaccion transaccion = new Transaccion();

            String amountStr = payment.getTransactions().get(0).getAmount().getTotal();

            // Por si acaso viniera con coma (no debería, pero mejor blindado)
            amountStr = amountStr.replace(",", ".");

            transaccion.setMontoTotal(new BigDecimal(amountStr));
            transaccion.setFechaPago(LocalDate.now());
            transaccion.setMetodoPago("PayPal");
            transaccion.setEstatusPago("Pagado");
            transaccion.setIdSolicitud(idSolicitud);

            transaccionRepository.save(transaccion);

            logger.info("✅ Transacción guardada en BD - ID Solicitud: {}, Monto: {}",
                    idSolicitud, amountStr);

        } catch (Exception e) {
            logger.error("❌ Error al guardar transacción: {}", e.getMessage(), e);
        }
    }
}
