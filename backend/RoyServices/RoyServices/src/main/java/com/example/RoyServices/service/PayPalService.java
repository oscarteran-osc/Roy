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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {

    private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);

    @Autowired
    private APIContext apiContext;

    @Autowired
    private TransaccionRepository transaccionRepository;

    public Payment crearPago(
            Double total,
            String moneda,
            String descripcion,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(moneda);
        amount.setTotal(String.format("%.2f", total));

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

        return payment.create(apiContext);
    }

    public Payment ejecutarPago(String paymentId, String payerId, Integer idSolicitud) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        Payment executedPayment = payment.execute(apiContext, paymentExecution);

        // Guardar en base de datos
        if (executedPayment.getState().equals("approved")) {
            guardarTransaccion(executedPayment, idSolicitud);
        }

        return executedPayment;
    }

    private void guardarTransaccion(Payment payment, Integer idSolicitud) {
        try {
            Transaccion transaccion = new Transaccion();

            // Obtener el monto
            String amountStr = payment.getTransactions().get(0).getAmount().getTotal();
            transaccion.setMontoTotal(new BigDecimal(amountStr));

            transaccion.setFechaPago(LocalDate.now());
            transaccion.setMetodoPago("PayPal");
            transaccion.setEstatusPago("Pagado");
            transaccion.setIdSolicitud(idSolicitud);

            transaccionRepository.save(transaccion);

            logger.info("✅ Transacción guardada en BD - ID Solicitud: {}, Monto: {}",
                    idSolicitud, amountStr);

        } catch (Exception e) {
            logger.error("❌ Error al guardar transacción: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}