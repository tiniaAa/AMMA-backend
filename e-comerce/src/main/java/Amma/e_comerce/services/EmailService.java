package Amma.e_comerce.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import Amma.e_comerce.dto.DetalleOrdenResponseDto;
import Amma.e_comerce.dto.OrdenResponseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	
	public void enviarCorreosDeCompra(OrdenResponseDto orden) {
		
		//texto de listado de productos recorridos
		StringBuilder detalleProductos = new StringBuilder();
		for (DetalleOrdenResponseDto detalle : orden.detalles()) {
			detalleProductos.append("- ")
							.append(detalle.cantidad()).append("x ")
							.append(detalle.nombreProducto())
							.append(" ($").append(detalle.precioUnitario()).append(" c/u)\n");
		
		}
		//Correo Cliente
		
		SimpleMailMessage mensajeCliente = new SimpleMailMessage();
		mensajeCliente.setFrom("ammatiendaa123@gmail.com");
		mensajeCliente.setTo(orden.compradorEmail());
		mensajeCliente.setSubject("Gracias por tu compra en AMMA - Orden #" + orden.id());
		mensajeCliente.setText("Hola " + orden.compradorNombre() + ",\n\n"
                + "¡Tu orden ha sido confirmada con éxito!\n\n"
                + "RESUMEN DE TU COMPRA:\n"
                + detalleProductos.toString()
                + "\nTotal a pagar: $" + orden.totalPagar() + "\n"
                + "Tipo de envío: " + orden.tipoEnvio() + "\n"
                + "Dirección de entrega: " + orden.compradorDireccion() + "\n\n"
                + "En breve nos pondremos en contacto contigo para avanzar con el proceso de Mercado Pago y el envío.\n\n"
                + "¡Gracias por elegirnos!\nEl equipo de AMMA.");
		mailSender.send(mensajeCliente);
		
		//Correo para Admin
		SimpleMailMessage mensajeAdmin = new SimpleMailMessage();
        mensajeAdmin.setFrom("ammatiendaa123@gmail.com");
        mensajeAdmin.setTo("ammatiendaa123@gmail.com"); // Te lo enviás a tu propio correo
        mensajeAdmin.setSubject("NUEVA VENTA 🚀 - Orden #" + orden.id());
        
        mensajeAdmin.setText("¡Felicidades! Entró una nueva compra.\n\n"
                + "DATOS DEL CLIENTE:\n"
                + "Nombre: " + orden.compradorNombre() + "\n"
                + "Numero de telefono: " + orden.compradorTelefono() +"\n"
                + "Email: " + orden.compradorEmail() + "\n"
                + "Dirección: " + orden.compradorDireccion() + "\n"
                + "Modalidad: " + orden.tipoEnvio() + "\n\n"
                + "PRODUCTOS VENDIDOS:\n"
                + detalleProductos.toString()
                + "\nTOTAL DE LA OPERACIÓN: $" + orden.totalPagar() + "\n\n"
                + "¡A preparar ese paquete!");

        mailSender.send(mensajeAdmin);
	}
}
