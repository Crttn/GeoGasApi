package es.crttn.geogas_api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_precios")
@Data
@NoArgsConstructor
public class HistoricoPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estacion_id", nullable = false)
    private Long estacionId;

    @Column(name = "tipo_combustible", nullable = false, length = 50)
    private String tipoCombustible;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
