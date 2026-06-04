# GeoGas API ⛽🗺️

> ⚠️ **Aviso Importante:** Este proyecto se encuentra actualmente en una **fase temprana de desarrollo (WIP)**. La estructura de la base de datos, los endpoints y la lógica interna pueden sufrir modificaciones significativas en futuras actualizaciones.

GeoGas es una API REST construida con Spring Boot que sincroniza, almacena y sirve datos en tiempo real sobre las estaciones de servicio y los precios de los carburantes en España. Utiliza datos oficiales proporcionados por el Ministerio para la Transición Ecológica y el Reto Demográfico.

La característica principal de GeoGas es su integración con **PostGIS**, lo que permite realizar consultas espaciales de alto rendimiento para encontrar las gasolineras más baratas en un radio kilométrico específico.

---

## 🚀 Características Principales

* **Sincronización Automatizada:** Descarga y parsea masivamente el JSON gubernamental (más de 12.000 estaciones) utilizando el cliente nativo `curl` del sistema operativo para sortear bloqueos de seguridad institucionales.
* **Motor Geoespacial:** Almacenamiento de coordenadas mediante el estándar `geometry(Point,4326)` de PostGIS.
* **Búsqueda Radial:** Algoritmo de triangulación para devolver el "Top 10" de estaciones más baratas cerca de la ubicación GPS del usuario.
* **Histórico y Precios:** Relación estructurada entre provincias, municipios, estaciones y sus respectivos tipos de combustible actualizados al día.

---

## 🛠️ Tecnologías Utilizadas

* **Backend:** Java, Spring Boot, Spring Data JPA
* **Base de Datos:** PostgreSQL
* **Extensión Espacial:** PostGIS
* **Procesamiento JSON:** Jackson (`ObjectMapper`)
* **Gestor de Dependencias:** Maven

---

## ⚙️ Requisitos Previos

Para ejecutar este proyecto en tu entorno local, necesitarás tener instalado:

1.  **Java Development Kit (JDK)** 17 o superior.
2.  **PostgreSQL** con la extensión **PostGIS** habilitada.
3.  **cURL** (Viene instalado de forma nativa en macOS y en la mayoría de distribuciones Linux/Windows modernas).
