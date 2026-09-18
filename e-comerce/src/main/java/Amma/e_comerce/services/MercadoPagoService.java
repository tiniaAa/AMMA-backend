package Amma.e_comerce.services;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

import Amma.e_comerce.model.DetalleOrden;
import Amma.e_comerce.model.Orden;
import jakarta.annotation.PostConstruct;

@Service
public class MercadoPagoService {
	@Value("${mercadopago.access-token}")
    private String accessToken;

	
	@PostConstruct
    public void init() {
		System.out.println("TOKEN QUE ESTÁ LEYENDO JAVA: " + accessToken);
        MercadoPagoConfig.setAccessToken(accessToken);
    }
	
		public String crearPreferencia(Orden orden) {
	        try {
	            List<PreferenceItemRequest> items = new ArrayList<>();
	
	            // 1. Mapeamos los productos de tu orden al formato que pide Mercado Pago
	            for (DetalleOrden detalle : orden.getDetalles()) {
	                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
	                        .title(detalle.getNombreProducto())
	                        .quantity(detalle.getCantidad())
	                        .unitPrice(detalle.getPrecioUnitario())
	                        .currencyId("ARS") // Moneda: Pesos Argentinos
	                        .build();
	                items.add(itemRequest);
	            }
	
	            // 2. Configuramos a dónde vuelve el cliente después de pagar
	            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
	                    .success("http://localhost:5173/check?estado=exito")
	                    .pending("http://localhost:5173/check?estado=pendiente")
	                    .failure("http://localhost:5173/check?estado=fallo")
	                    .build();
	
	            // 3. Armamos la preferencia completa
	            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
	                    .items(items)
	                    .backUrls(backUrls)
	                    .autoReturn("approved") // Vuelve automático si se aprueba rápido
	                    .externalReference(orden.getId().toString()) // Guardamos el ID de tu orden en MP
	                    .build();
	
	            // 4. Enviamos a Mercado Pago y obtenemos la respuesta
	            PreferenceClient client = new PreferenceClient();
	            Preference preference = client.create(preferenceRequest);
	
	            // 5. Retornamos el link de pago seguro (Checkout Pro)
	            return preference.getInitPoint();
	
	        } catch (MPApiException apiException) {
	            System.out.println("=== ERROR EXACTO DE MERCADO PAGO ===");
	            System.out.println(apiException.getApiResponse().getContent());
	            System.out.println("====================================");
	            throw new RuntimeException("Mercado Pago rechazó la petición. Mirá la consola de Eclipse.");
	        } catch (MPException ex) {
	            ex.printStackTrace();
	            throw new RuntimeException("Error interno del SDK de Mercado Pago");
	        }
		}      
}
