package Amma.e_comerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Total final a cobrar: productos + envío
    @Column(name = "total_pagar", nullable = false)
    private BigDecimal totalPagar;

    // Costo de envío que se aplicó a esta orden (0 si es retiro en el local).
    // Nullable para no romper las órdenes que ya existen en la base.
    @Column(name = "costo_envio")
    private BigDecimal costoEnvio;

    @Column(name = "tipo_envio", nullable = false)
    private String tipoEnvio;

    @Column(name = "fecha_compra", updatable = false)
    private LocalDateTime fechaCompra = LocalDateTime.now();

    // --- Datos del Comprador Invitado (Guest Checkout) ---
    @Column(name = "comprador_nombre", nullable = false)
    private String compradorNombre;

    @Column(name = "comprador_apellido", nullable = false)
    private String compradorApellido;

    @Column(name = "comprador_email", nullable = false)
    private String compradorEmail;

    @Column(name = "comprador_direccion", nullable = false)
    private String compradorDireccion;

    @Column(name = "comprador_ciudad", nullable = false)
    private String compradorCiudad;

    @Column(name = "comprador_cp", nullable = false)
    private String compradorCp;

    @Column(name = "comprador_provincia", nullable = false)
    private String compradorProvincia;

    @Column(name = "comprador_telefono"/*, nullable = false*/)
    private String compradorTelefono;

    // --- Pago con Mercado Pago ---
    @Column(name = "estado_pago")
    private String estadoPago;   // null = PENDIENTE

    @Column(name = "payment_id")
    private String paymentId;
    
    // Nuevo campo: "efectivo" o "mercadopago"
    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;


    // --- RELACIÓN BIDIRECCIONAL CON DETALLE ORDEN ---
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();
}
