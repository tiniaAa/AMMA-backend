package Amma.e_comerce.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdenVencimientoScheduler {

    private final OrdenService ordenService;

    // Se ejecuta cada 1 minuto (60000 ms). 
    // Si en properties tenés tienda.revision-vencidas-ms, usa ese valor.
    @Scheduled(fixedRateString = "${tienda.revision-vencidas-ms:60000}")
    public void revisarOrdenesVencidas() {
        try {
            // Le pasamos 30 minutos de tiempo de vida a las reservas
            ordenService.liberarReservasVencidas(30);
        } catch (Exception e) {
            log.error("Fallo al ejecutar el scheduler de vencimientos", e);
        }
    }
}