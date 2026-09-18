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

    @Column(name = "total_pagar", nullable = false)
    private BigDecimal totalPagar;

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

    // --- RELACIÓN BIDIRECCIONAL CON DETALLE ORDEN ---
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrden> detalles = new ArrayList<>();
}
