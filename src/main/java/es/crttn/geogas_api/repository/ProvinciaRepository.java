package es.crttn.geogas_api.repository;

import es.crttn.geogas_api.models.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, String> {
}
