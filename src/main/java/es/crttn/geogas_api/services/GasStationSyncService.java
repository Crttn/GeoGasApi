package es.crttn.geogas_api.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.crttn.geogas_api.models.EstacionServicio;
import es.crttn.geogas_api.models.PrecioCombustible;
import es.crttn.geogas_api.models.Provincia;
import es.crttn.geogas_api.repository.EstacionServicioRepository;
import es.crttn.geogas_api.repository.PrecioCombustibleRepository;
import es.crttn.geogas_api.repository.ProvinciaRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class GasStationSyncService {

    private final EstacionServicioRepository estacionRepository;
    private final ProvinciaRepository provinciaRepository;
    private final PrecioCombustibleRepository precioRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private static final String API_URL = "https://energia.serviciosmin.gob.es/ServiciosRestCarburantes/PreciosCarburantes/EstacionesTerrestres/";

    public GasStationSyncService(EstacionServicioRepository estacionRepository,
                                 ProvinciaRepository provinciaRepository,
                                 PrecioCombustibleRepository precioRepository) {
        this.estacionRepository = estacionRepository;
        this.provinciaRepository = provinciaRepository;
        this.precioRepository = precioRepository;
    }

    public void syncData() {
        System.out.println("Iniciando extracción masiva usando cURL nativo...");

        try {

            ProcessBuilder pb = new ProcessBuilder(
                    "curl",
                    "-s",
                    "--compressed",
                    API_URL
            );

            Process p = pb.start();

            String jsonResult = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: cURL falló con el código de salida " + exitCode);
                return;
            }

            if (jsonResult.isEmpty()) {
                System.out.println("Error: cURL no devolvió ningún dato.");
                return;
            }

            // Parsear el sting a json
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(jsonResult, new TypeReference<Map<String, Object>>(){});

            if (jsonMap == null || !jsonMap.containsKey("ListaEESSPrecio")) {
                System.out.println("El JSON devuelto no tiene el formato esperado.");
                return;
            }

            List<Map<String, String>> estaciones = (List<Map<String, String>>) jsonMap.get("ListaEESSPrecio");
            int guardadas = 0;

            System.out.println("¡Descarga exitosa! Se van a procesar " + estaciones.size() + " gasolineras...");

            for (Map<String, String> datos : estaciones) {
                try {
                    String idProvincia = datos.get("IDProvincia");
                    String nombreProvincia = datos.get("Provincia");
                    Provincia provincia = provinciaRepository.findById(idProvincia)
                            .orElseGet(() -> {
                                Provincia nuevaProv = new Provincia();
                                nuevaProv.setId(idProvincia);
                                nuevaProv.setNombre(nombreProvincia);
                                return provinciaRepository.save(nuevaProv);
                            });

                    Integer idMinisterio = Integer.parseInt(datos.get("IDEESS"));
                    EstacionServicio estacion = estacionRepository.findByIdMinisterio(idMinisterio)
                            .orElse(new EstacionServicio());

                    double lat = Double.parseDouble(datos.get("Latitud").replace(",", ".").trim());
                    double lon = Double.parseDouble(datos.get("Longitud (WGS84)").replace(",", ".").trim());
                    Point ubicacion = geometryFactory.createPoint(new Coordinate(lon, lat));

                    estacion.setIdMinisterio(idMinisterio);
                    estacion.setRotulo(datos.get("Rótulo"));
                    estacion.setDireccion(datos.get("Dirección"));
                    estacion.setMunicipio(datos.get("Municipio"));
                    estacion.setProvincia(provincia);
                    estacion.setUbicacion(ubicacion);
                    estacion.setUltimaActualizacion(LocalDateTime.now());

                    estacion = estacionRepository.save(estacion);

                    guardarPrecioSiExiste(datos, "Precio Gasolina 95 E5", "Gasolina 95 E5", estacion);
                    guardarPrecioSiExiste(datos, "Precio Gasoleo A", "Gasóleo A", estacion);
                    guardarPrecioSiExiste(datos, "Precio Gasolina 98 E5", "Gasolina 98 E5", estacion);
                    guardarPrecioSiExiste(datos, "Precio Gasoleo Premium", "Gasóleo Premium", estacion);

                    guardadas++;

                } catch (Exception e) {
                    // Ignoramos errores puntuales
                }
            }

            System.out.println("Sincronización finalizada con éxito. Gasolineras procesadas: " + guardadas);

        } catch (Exception e) {
            System.err.println("Error crítico ejecutando el proceso cURL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void guardarPrecioSiExiste(Map<String, String> datos, String claveJson, String tipoCombustible, EstacionServicio estacion) {
        String precioStr = datos.get(claveJson);

        if (precioStr != null && !precioStr.trim().isEmpty()) {
            Double valorPrecio = Double.parseDouble(precioStr.replace(",", "."));

            PrecioCombustible precioObj = precioRepository
                    .findByEstacionServicioIdAndTipoCombustible(estacion.getId(), tipoCombustible)
                    .orElse(new PrecioCombustible());

            precioObj.setEstacionServicio(estacion);
            precioObj.setTipoCombustible(tipoCombustible);
            precioObj.setPrecio(valorPrecio);
            precioObj.setFechaRegistro(LocalDateTime.now());

            precioRepository.save(precioObj);
        }
    }
}