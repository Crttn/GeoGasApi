package es.crttn.geogas_api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "estaciones_servicio")
@Data
@NoArgsConstructor
public class EstacionServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_ministerio", unique = true, nullable = false)
    private Integer idMinisterio;

    @Column(nullable = false, length = 100)
    private String rotulo;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String municipio;

    // Relación con la tabla Provincias
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id")
    private Provincia provincia;

    //  Coordenadas en el sistema estándar GPS (WGS84 -> SRID 4326)
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point ubicacion;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;
}
