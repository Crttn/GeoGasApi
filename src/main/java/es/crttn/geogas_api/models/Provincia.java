package es.crttn.geogas_api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "provincias")
@Data
@NoArgsConstructor
public class Provincia {

    @Id
    @Column(length = 2)
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
}
