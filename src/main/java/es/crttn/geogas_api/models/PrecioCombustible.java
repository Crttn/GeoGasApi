package es.crttn.geogas_api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "precios_combustible")
@Data
@NoArgsConstructor
public class PrecioCombustible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estacion_id", nullable = false)
    private EstacionServicio estacionServicio;

    @Column(name = "tipo_combustible", nullable = false, length = 50)
    private String tipoCombustible; // Ej: "Gasolina 95 E5", "Gasóleo A"

    @Column(nullable = false)
    private Double precio;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
}
