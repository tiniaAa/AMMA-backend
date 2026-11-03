package Amma.e_comerce.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import Amma.e_comerce.dto.DetalleOrdenResponseDto;
import Amma.e_comerce.dto.OrdenResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void enviarCorreosDeCompra(OrdenResponseDto orden) {
        try {
            // ==================== 1. CORREO CLIENTE ====================
            MimeMessage mensajeCliente = mailSender.createMimeMessage();
            MimeMessageHelper helperCliente = new MimeMessageHelper(mensajeCliente, true, "UTF-8");
            
            helperCliente.setFrom("ammatiendaa123@gmail.com");
            helperCliente.setTo(orden.compradorEmail());
            helperCliente.setSubject("Gracias por tu compra en AMMA - Orden #" + orden.id());
            
            String htmlCliente = buildClientHtmlTemplate(orden);
            helperCliente.setText(htmlCliente, true); 
            
            mailSender.send(mensajeCliente);
            
            // ==================== 2. CORREO ADMINISTRADOR ====================
            MimeMessage mensajeAdmin = mailSender.createMimeMessage();
            MimeMessageHelper helperAdmin = new MimeMessageHelper(mensajeAdmin, true, "UTF-8");
            
            helperAdmin.setFrom("ammatiendaa123@gmail.com");
            helperAdmin.setTo("ammatiendaa123@gmail.com"); // Te llega a vos
            helperAdmin.setSubject("🚀 NUEVA VENTA - Orden #" + orden.id());
            
            String htmlAdmin = buildAdminHtmlTemplate(orden);
            helperAdmin.setText(htmlAdmin, true); 
            
            mailSender.send(mensajeAdmin);
            
        } catch (Exception e) {
            System.err.println("Error al enviar correos: " + e.getMessage());
        }
    }
    
    // =========================================================================
    // PLANTILLA HTML PARA EL CLIENTE (Diseño Beige Estético)
    // =========================================================================
    private String buildClientHtmlTemplate(OrdenResponseDto orden) {
        StringBuilder html = new StringBuilder();
        
        html.append("<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #FAF6F0; padding: 20px;\">");
        html.append("<h2 style=\"color: #333; text-align: center;\">¡Gracias por tu compra, ").append(orden.compradorNombre()).append("!</h2>");
        html.append("<p style=\"text-align: center; color: #555;\">Tu orden <b>#").append(orden.id()).append("</b> ha sido confirmada y procesada.</p>");
        
        html.append("<div style=\"background-color: #fff; padding: 20px; border-radius: 8px; margin-top: 20px;\">");
        html.append("<h3 style=\"margin-top: 0; color: #333; font-size: 20px;\">Detalle de la orden</h3>");
        
        html.append("<table style=\"width: 100%; border-collapse: collapse; margin-top: 20px;\">");
        for (DetalleOrdenResponseDto detalle : orden.detalles()) {
            html.append("<tr>");
            
            html.append("<td style=\"padding: 15px 0; border-bottom: 1px solid #eee; width: 75px;\">");
            html.append("<img src=\"").append(detalle.imagenUrl())
                .append("\" style=\"width: 65px; height: 65px; border-radius: 50%; object-fit: cover;\" alt=\"producto\"/>");
            html.append("</td>");
            
            html.append("<td style=\"padding: 15px 10px; border-bottom: 1px solid #eee;\">");
            html.append("<p style=\"margin: 0; font-weight: bold; color: #333; font-size: 15px;\">")
                .append(detalle.cantidad()).append("x ").append(detalle.nombreProducto()).append("</p>");
            html.append("<p style=\"margin: 5px 0 0 0; font-size: 13px; color: #777;\">Talle: ")
                .append(detalle.talle()).append(" | Color: ").append(detalle.color()).append("</p>");
            html.append("</td>");
            
            html.append("<td style=\"padding: 15px 0; border-bottom: 1px solid #eee; text-align: right; font-weight: bold; color: #333;\">");
            html.append("$").append(detalle.precioUnitario());
            html.append("</td>");
            
            html.append("</tr>");
        }
        html.append("</table>");
        
        html.append("<table style=\"width: 100%; margin-top: 15px; border-top: 2px solid #333; padding-top: 15px;\">");
        html.append("<tr>");
        html.append("<td style=\"padding: 10px 0; font-weight: bold; font-size: 18px; color: #333;\">Total</td>");
        html.append("<td style=\"padding: 10px 0; text-align: right; font-weight: bold; font-size: 18px; color: #333;\">$").append(orden.totalPagar()).append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</div>"); 
        
        html.append("<p style=\"text-align: center; color: #777; margin-top: 20px; font-size: 14px; line-height: 1.5;\">")
            .append("Modalidad de envío: <b>").append(orden.tipoEnvio()).append("</b><br>")
            .append("Dirección: <b>").append("local".equals(orden.tipoEnvio()) ? "Moreno 1722, Laguna Paiva" : orden.compradorDireccion()).append("</b><br><br>")
            .append("En breve nos comunicaremos con vos para acordar los detalles de la entrega.")
            .append("</p>");
            
        html.append("</div>"); 
        return html.toString();
    }

    // =========================================================================
    // PLANTILLA HTML PARA EL ADMINISTRADOR (Diseño Técnico/Práctico)
    // =========================================================================
    private String buildAdminHtmlTemplate(OrdenResponseDto orden) {
        StringBuilder html = new StringBuilder();
        
        // Fondo gris clarito
        html.append("<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #f4f4f9; padding: 20px;\">");
        
        html.append("<h2 style=\"color: #2c3e50; text-align: center;\">🎉 ¡NUEVA VENTA! 🎉</h2>");
        html.append("<p style=\"text-align: center; color: #555;\">Orden <b>#").append(orden.id()).append("</b> ingresada al sistema.</p>");
        
        // CAJA 1: Datos de Envío del Cliente (Borde azul para resaltar)
        html.append("<div style=\"background-color: #fff; padding: 20px; border-radius: 8px; margin-top: 20px; border-left: 5px solid #3498db; box-shadow: 0 2px 4px rgba(0,0,0,0.05);\">");
        html.append("<h3 style=\"margin-top: 0; color: #2c3e50; font-size: 18px;\">Datos de Entrega</h3>");
        
        // Si el comprador puso un apellido nulo, evitamos que diga "null"
        String apellido = orden.compradorApellido() != null ? orden.compradorApellido() : "";
        
        html.append("<p style=\"margin: 8px 0; color: #444; font-size: 14px;\"><b>Cliente:</b> ").append(orden.compradorNombre()).append(" ").append(apellido).append("</p>");
        html.append("<p style=\"margin: 8px 0; color: #444; font-size: 14px;\"><b>Email:</b> <a href=\"mailto:").append(orden.compradorEmail()).append("\">").append(orden.compradorEmail()).append("</a></p>");
        html.append("<p style=\"margin: 8px 0; color: #444; font-size: 14px;\"><b>Teléfono:</b> <a href=\"https://wa.me/").append(orden.compradorTelefono()).append("\">").append(orden.compradorTelefono()).append("</a></p>");
        html.append("<p style=\"margin: 8px 0; color: #444; font-size: 14px;\"><b>Dirección:</b> ").append("local".equals(orden.tipoEnvio()) ? "Retiro en local (Moreno 1722)" : orden.compradorDireccion());
        html.append("<p style=\"margin: 8px 0; color: #444; font-size: 14px;\"><b>Envío/Retiro:</b> <span style=\"background-color:#e8f4fd; padding:3px 8px; border-radius:4px; color:#2980b9; font-weight:bold;\">").append(orden.tipoEnvio()).append("</span></p>");
        html.append("</div>");

        // CAJA 2: Productos a preparar
        html.append("<div style=\"background-color: #fff; padding: 20px; border-radius: 8px; margin-top: 15px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);\">");
        html.append("<h3 style=\"margin-top: 0; color: #2c3e50; font-size: 18px;\">Productos a Preparar</h3>");
        
        html.append("<table style=\"width: 100%; border-collapse: collapse; margin-top: 15px;\">");
        for (DetalleOrdenResponseDto detalle : orden.detalles()) {
            html.append("<tr>");
            
            // Imagen cuadrada y más chica para el admin
            html.append("<td style=\"padding: 10px 0; border-bottom: 1px solid #eee; width: 60px;\">");
            html.append("<img src=\"").append(detalle.imagenUrl())
                .append("\" style=\"width: 50px; height: 50px; border-radius: 6px; object-fit: cover;\" alt=\"producto\"/>");
            html.append("</td>");
            
            html.append("<td style=\"padding: 10px; border-bottom: 1px solid #eee;\">");
            html.append("<p style=\"margin: 0; font-weight: bold; color: #333; font-size: 14px;\">")
                .append(detalle.cantidad()).append("x ").append(detalle.nombreProducto())
                .append("</p>");
            html.append("<p style=\"margin: 3px 0 0 0; font-size: 13px; color: #e67e22; font-weight: bold;\">Talle: ")
                .append(detalle.talle()).append(" | Color: ").append(detalle.color())
                .append("</p>");
            html.append("</td>");
            
            html.append("</tr>");
        }
        html.append("</table>");
        
        // Sección Dinero
        html.append("<table style=\"width: 100%; margin-top: 15px; border-top: 2px solid #2c3e50; padding-top: 15px;\">");
        html.append("<tr>");
        html.append("<td style=\"padding: 10px 0; font-weight: bold; font-size: 16px; color: #2c3e50;\">A cobrar (Subtotal + Envío)</td>");
        html.append("<td style=\"padding: 10px 0; text-align: right; font-weight: bold; font-size: 18px; color: #27ae60;\">$").append(orden.totalPagar()).append("</td>");
        html.append("</tr>");
        html.append("</table>");
        
        html.append("</div>"); 
        
        html.append("</div>"); 
        return html.toString();
    }
}
