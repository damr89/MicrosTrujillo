# 🚌 MicrosTrujillo - Documentación Técnica y Arquitectura

## 1. Propósito de la Aplicación
**MicrosTrujillo** es una aplicación móvil nativa para Android diseñada para revolucionar la forma en que los ciudadanos de Trujillo (Perú) se movilizan en transporte público. Su objetivo principal es calcular y recomendar la mejor ruta de microbús ("micro") entre un origen y un destino, optimizando tanto el tiempo de caminata como el tiempo de viaje en el vehículo.

A diferencia de las aplicaciones tradicionales, MicrosTrujillo está diseñada para funcionar de forma **ultrarrápida y offline** en el cálculo de rutas, respetando estrictamente el trazado físico real de las avenidas y calles de la ciudad sin depender de costosas APIs externas en tiempo de ejecución.

---

## 2. Stack Tecnológico
El proyecto está construido bajo los estándares más modernos de desarrollo Android:

* **Lenguaje:** Kotlin.
* **Interfaz de Usuario (UI):** Jetpack Compose (Material Design 3) para una UI reactiva y declarativa.
* **Arquitectura:** MVVM (Model-View-ViewModel) impulsado por Kotlin Coroutines y `StateFlow`.
* **Mapas y Renderizado:** OSMDroid (OpenStreetMap) usando el proveedor de mapas `Mapnik/HOT`.
* **Base de Datos Local:** Room Database (SQLite) para el cacheo persistente de la red vial.
* **Serialización de Datos:** Gson (para lectura e ingesta masiva de archivos `.json`).
* **Servicios de Ubicación:** APIs nativas de `android.location`.

---

## 3. Metodología de Ruteo (El "Secreto" Core)
El mayor desafío de las apps de transporte es que los motores de ruteo vehiculares (como OSRM o Google Maps Directions) evalúan las calles con reglas de tránsito para autos privados, lo cual genera "bucles" o desvíos cuando se aplican a autobuses con rutas concesionadas. 

Para solucionar esto, implementamos la **Metodología de Geometría Densa Pre-calculada (Estilo SmartBus/Moovit):**

### 3.1. Trazos Densos Estáticos (`shapePoints`)
* En lugar de calcular rutas en tiempo real, la aplicación pre-carga la geometría curva exacta de las calles de Trujillo desde archivos `.json` locales ubicados en `assets/routes/`.
* Estos archivos fueron generados consultando la base de datos mundial de Overpass API (OpenStreetMap) usando un perfil *Foot* (Peatonal), lo que garantiza que la línea azul se amolde milimétricamente al asfalto y a los óvalos sin saltar por encima de las casas.

### 3.2. El Algoritmo de Decisión (`TransitEngine.kt` + `RoutingEngine.kt`)
Cuando el usuario toca el mapa (Origen y Destino), la app no llama a internet. En su lugar, el algoritmo interno:
1. **Fórmula Haversine:** Calcula la distancia a pie desde el Origen y el Destino hacia **todos** los nodos de la ruta simultáneamente (miles de cálculos en menos de 2 milisegundos).
2. **Matriz de Intersección:** Filtra los nodos que están a una distancia caminable razonable (< 1.5 km).
3. **Validación Direccional:** Comprueba la "Regla de Oro": El índice del nodo de subida DEBE ser estrictamente anterior al índice del nodo de bajada (`o < d`), garantizando que el usuario no vaya en sentido contrario al micro.
4. **Smart Time Cost Scoring (Puntuación de Tiempo Real):** El algoritmo evalúa todas las combinaciones posibles de subida y bajada aplicando una fórmula que imita el cansancio humano:
   * *Velocidad a pie:* ~1.2 m/s.
   * *Velocidad en micro:* ~5.0 m/s.
   * *Fórmula:* `(DistanciaCaminando * 4.5) + DistanciaEnMicro`.
   * **Resultado:** La app castiga severamente a las rutas que te obligan a caminar mucho, prefiriendo siempre dejarte en la puerta de tu destino, aunque el micro tome una ruta un poco más larga.

---

## 4. Estructura de Módulos
* `com.example.microstrujillo.data`: Contiene el acceso a datos.
  * `AppDatabase.kt`, `RouteDao.kt`, `Converters.kt`: Configuración de Room SQLite.
  * `NuevoCaliforniaData.kt`: Clase encargada de ingerir los JSONs y alimentar SQLite.
* `com.example.microstrujillo.domain`: Lógica de negocio y motores matemáticos.
  * `TransitEngine.kt`: Motor de cálculo geométrico y distancias.
  * `RoutingEngine.kt`: Motor de toma de decisiones y puntajes.
  * `SmartBusEngine.kt`: Parser que lee la carpeta `assets/routes/` usando Gson.
* `com.example.microstrujillo.presentation`: Toda la interfaz visual.
  * `Pantallas.kt`: Contiene el Scaffold con Compose, los marcadores y polilíneas de OSMDroid.
  * `TransportViewModel.kt`: Administra el estado global de la app, reaccionando automáticamente a los toques en el mapa.
* `com.example.microstrujillo.model`: Clases y Entidades de datos puras (`RouteEntity`, `Stop`).

---

## 5. Flujo de Experiencia del Usuario (UX)
1. **Inicio:** Al abrir la app, un `LaunchedEffect` revisa si la base de datos está vacía. Si lo está, inyecta todos los JSONs de la carpeta assets en Room.
2. **Radar (Mapa):** El usuario toca la pantalla.
   * *Primer toque:* Marca el Origen (🟢).
   * *Segundo toque:* Marca el Destino (📍).
3. **Reacción Inmediata:** El `StateFlow` del ViewModel detecta ambos toques y lanza el `RoutingEngine`.
4. **Renderizado:** El mapa dibuja 3 componentes visuales:
   * Línea punteada gris oscura: Caminata desde el origen al paradero.
   * **Línea azul gruesa:** El tramo exacto en micro amoldado perfectamente a la vía.
   * Línea punteada gris oscura: Caminata desde el paradero de bajada hasta el destino final.

---

## 6. Guía de Escalabilidad (Cómo agregar más líneas)
El proyecto ha sido diseñado para ser un motor genérico y 100% escalable. Para añadir una nueva empresa de micros (ej. *Salaverry Express*, *El Cortijo*, *Huanchaco*), **NO ES NECESARIO MODIFICAR EL CÓDIGO KOTLIN**.

**Pasos para añadir nuevas rutas:**
1. Generar el archivo `.json` de la ruta deseada con la geometría (array de coordenadas `[lng, lat]`).
2. Nombrar el archivo (ej. `el_cortijo_a_ida.json`).
3. Soltar el archivo dentro de la carpeta `app/src/main/assets/routes/`.
4. Añadir el nombre de ese archivo al arreglo `archivosRutas` dentro de `SmartBusEngine.kt`.
5. Incrementar en +1 el `version` dentro de `AppDatabase.kt`.
6. ¡Listo! Al abrir la app, la nueva ruta será considerada para los cálculos matemáticos instantáneamente.
