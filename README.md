# FoodMarket — Plataforma de Delivery con Microservicios

> **DSY1103 Desarrollo FullStack I · Evaluación Parcial 3**

Plataforma de pedidos de comida a domicilio construida sobre una arquitectura de microservicios con Spring Boot. Conecta clientes, restaurantes y repartidores a través de 10 servicios independientes que se comunican de forma síncrona (OpenFeign) y asíncrona (Apache Kafka).

---

## Integrantes

| Integrante | Servicios a cargo |
|---|---|
| **Felipe Echeverría** | eureka-server, api-gateway, auth-service, user-service, restaurant-service, order-service |
| **Daniel Parada** | payment-service, delivery-service, notification-service, review-service, search-service, report-service |

---

## Stack tecnológico

| Tecnología | Uso |
|---|---|
| Spring Boot 3 | Framework base de cada microservicio |
| Spring Cloud Gateway | API Gateway con validación JWT |
| Netflix Eureka | Registro y descubrimiento de servicios |
| OpenFeign | Comunicación síncrona entre servicios |
| Apache Kafka | Mensajería asíncrona (eventos de dominio) |
| MySQL 8 | Base de datos por microservicio |
| Docker / Docker Compose | Contenedorización y orquestación |
| JWT | Autenticación sin estado |

---

## Arquitectura general

```
Cliente (Postman / App)
         │
         ▼
  ┌─────────────────┐
  │   API Gateway   │  :9090  — valida JWT, enruta requests
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │  Eureka Server  │  :9761  — registro y descubrimiento de servicios
  └────────┬────────┘
           │
  ┌────────┴──────────────────────────────────────────┐
  │                   MICROSERVICIOS                  │
  │                                                   │
  │  auth-service         :9081   user-service  :9082 │
  │  restaurant-service   :9083   order-service :9084 │
  │  payment-service      :9085   delivery      :9086 │
  │  notification-service :9087   review        :9088 │
  │  search-service       :9089   report        :9095 │
  └───────────────────────────────────────────────────┘
           │
  ┌────────▼────────┐
  │  Apache Kafka   │  — mensajería asíncrona entre servicios
  └─────────────────┘
           │
  ┌────────▼────────┐
  │    MySQL :3307  │  — una base de datos por microservicio
  └─────────────────┘
```

---

## Documentación Swagger / OpenAPI

Cada microservicio expone su documentación en `/swagger-ui.html` (accesible por el puerto del servicio):

| Servicio | Swagger UI |
|---|---|
| auth-service | http://localhost:9081/swagger-ui.html |
| restaurant-service | http://localhost:9083/swagger-ui.html |
| order-service | http://localhost:9084/swagger-ui.html |
| delivery-service | http://localhost:9086/swagger-ui.html |
| notification-service | http://localhost:9087/swagger-ui.html |
| review-service | http://localhost:9088/swagger-ui.html |
| report-service | http://localhost:9095/swagger-ui.html |



---

## Pruebas unitarias

En IntelliJ IDEA, navegamos en el panel de proyecto hasta el archivo de test de cada microservicio y haz click derecho → Run:

ms-auth
ms-auth → src → test → java → com.foodmarket.auth.service → AuthServiceTest.java

ms-restaurant
ms-restaurant → src → test → java → com.foodmarket.restaurant.service → RestaurantServiceTest.java

ms-order
ms-order → src → test → java → com.foodmarket.order.service → OrderServiceTest.java

Los resultados aparecen en el panel inferior mostrando cada test con ✅ si pasó o ❌ si falló.
```

Cobertura mínima: **80%** sobre la capa de servicio (lógica de negocio).

---

## Eventos Kafka implementados

| Tópico | Publicado por | Consumido por | Descripción |
|---|---|---|---|
| `order-events` | order-service | notification-service | Creación y cambios de estado de pedidos |
| `order-delivered` | order-service | notification-service, report-service | Pedido entregado exitosamente |

---

## Rutas del API Gateway

| Prefijo | Microservicio | Puerto directo |
|---|---|---|
| `/auth/**` | ms-auth | 9081 |
| `/users/**` | ms-user | 9082 |
| `/restaurants/**` | ms-restaurant | 9083 |
| `/orders/**` | ms-order | 9084 |
| `/payments/**` | ms-payment | 9085 |
| `/deliveries/**` | ms-delivery | 9086 |
| `/notifications/**` | ms-notification | 9087 |
| `/reviews/**` | ms-review | 9088 |
| `/search/**` | ms-search | 9089 |
| `/reports/**` | ms-report | 9095 |

---

## Requisitos previos

- **Docker Desktop** (con Docker Compose v2) — [descargar aquí](https://www.docker.com/products/docker-desktop/)
- **Java 21** y **Maven 3.8+** — solo si se quiere ejecutar algún servicio de forma local
- Puerto **3307** libre en el host (MySQL se expone en este puerto para no colisionar con instalaciones locales)
- Puerto **9092** libre en el host (Kafka)
- Puerto **2181** libre en el host (Zookeeper)

---

## Cómo ejecutar el proyecto

### Con Docker Compose (recomendado)

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd MicroServiciosFS

# 2. Levantar todos los servicios
docker compose up --build
```

El primer arranque puede demorar **2-3 minutos** porque Docker construye las imágenes y MySQL inicializa todas las bases de datos.

**Verificar que todo esté funcionando:**

| Servicio | URL |
|---|---|
| Eureka Dashboard | http://localhost:9761 |
| API Gateway | http://localhost:9090 |

> **Tip:** El API Gateway enruta todas las peticiones. Una vez que Eureka muestra los servicios como `UP`, el sistema está listo.

### Detener el proyecto

```bash
docker compose down
```

Para borrar también los datos persistentes (MySQL):

```bash
docker compose down -v
```

---

## Microservicios

### 1. auth-service — Puerto 9081

Gestiona la autenticación del sistema mediante JWT. Los usuarios de prueba se persisten en la base de datos `db_auth`. Genera tokens con roles (`CUSTOMER`, `RESTAURANT_OWNER`, `ADMIN`) que el API Gateway propaga a todos los servicios mediante los headers `X-User-Email` y `X-User-Role`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/auth/register` | Registrar cuenta nueva |
| `POST` | `/auth/login` | Iniciar sesión y obtener token JWT |

---

### 2. user-service — Puerto 9082 · BD: `db_user`

Gestiona los perfiles de usuario y sus direcciones de entrega. Implementa relación `OneToMany` entre perfil y direcciones, y valida que no existan perfiles duplicados para el mismo `userId`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/users/{userId}/profile` | Crear perfil de usuario |
| `GET` | `/users/{userId}/profile` | Obtener perfil |
| `POST` | `/users/{userId}/addresses` | Agregar dirección |
| `GET` | `/users/{userId}/addresses` | Listar direcciones activas |
| `DELETE` | `/users/{userId}/addresses/{addressId}` | Desactivar dirección |

---

### 3. restaurant-service — Puerto 9083 · BD: `db_restaurant`

Administra el catálogo de restaurantes y sus menús. Regla de negocio: cuando un ítem llega a `stock = 0`, se marca automáticamente como `available = false` y Kafka publica el evento `stock.low`. Implementa relación `ManyToOne` entre `MenuItem` y `Restaurant`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/restaurants` | Crear restaurante (rol: `RESTAURANT_OWNER` / `ADMIN`) |
| `GET` | `/restaurants/{id}` | Obtener restaurante por ID |
| `GET` | `/restaurants/zone/{zone}` | Listar restaurantes abiertos por zona |
| `POST` | `/restaurants/{id}/menu` | Agregar ítem al menú |
| `GET` | `/restaurants/{id}/menu` | Ver menú disponible |
| `PATCH` | `/restaurants/{id}/menu/{itemId}/stock` | Actualizar stock de ítem |
| `PATCH` | `/restaurants/{id}/status` | Abrir / cerrar restaurante |

---

### 4. order-service — Puerto 9084 · BD: `db_order`

Servicio central del flujo de negocio. Al crear un pedido, valida en tiempo real (vía OpenFeign) que el restaurante esté abierto, que la zona coincida y que los ítems tengan stock. Gestiona el ciclo de vida del pedido con transiciones validadas:

```
PENDING → CONFIRMED → PREPARING → READY → IN_DELIVERY → DELIVERED
```

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/orders` | Crear pedido |
| `GET` | `/orders/{id}` | Obtener pedido por ID |
| `GET` | `/orders/customer/{id}` | Historial de pedidos del cliente |
| `PATCH` | `/orders/{id}/status` | Cambiar estado del pedido |

---

### 5. payment-service — Puerto 9085 · BD: `db_payment`

Simula el procesamiento de pagos. Valida que no exista un pago completado para la misma orden. Publica `payment.completed` o `payment.failed` según el resultado. Solo el rol `ADMIN` puede emitir reembolsos.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/payments` | Procesar pago |
| `GET` | `/payments/order/{id}` | Obtener pago por orden |
| `GET` | `/payments/customer/{id}` | Historial de pagos del cliente |
| `POST` | `/payments/{id}/refund` | Emitir reembolso (solo `ADMIN`) |

---

### 6. delivery-service — Puerto 9086 · BD: `db_delivery`

Asigna automáticamente el primer repartidor disponible en la zona del pedido. Al completar la entrega (`DELIVERED`), libera al repartidor y publica los eventos `delivery.completed` y `order.delivered`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/deliveries/agents` | Registrar repartidor |
| `POST` | `/deliveries/assign` | Asignar repartidor a pedido |
| `PATCH` | `/deliveries/{id}/status` | Actualizar estado de la entrega |
| `GET` | `/deliveries/order/{id}` | Obtener entrega por pedido |

---

### 7. notification-service — Puerto 9087 · BD: `db_notification`

Consumer Kafka puro: escucha 6 tópicos y persiste las notificaciones en base de datos. No produce eventos, solo reacciona a los de otros servicios.

**Tópicos consumidos:**

| Tópico | Mensaje generado |
|---|---|
| `order.placed` | "Nuevo pedido recibido" |
| `order.confirmed` | "Pedido confirmado" |
| `payment.completed` | "Pago exitoso" |
| `payment.failed` | "Pago fallido, reintenta" |
| `order.delivered` | "Pedido entregado, califícalo" |
| `stock.low` | "Alerta de stock bajo" |

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/notifications/{userId}` | Todas las notificaciones del usuario |
| `GET` | `/notifications/{userId}/unread` | Solo las no leídas |
| `PATCH` | `/notifications/{id}/read` | Marcar como leída |

---

### 8. review-service — Puerto 9088 · BD: `db_review`

Permite calificar restaurantes y repartidores. Valida vía OpenFeign que el pedido esté en estado `DELIVERED` antes de permitir la reseña. Previene reseñas duplicadas del mismo tipo para el mismo pedido.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/reviews` | Crear reseña |
| `GET` | `/reviews/restaurant/{id}` | Reseñas de un restaurante |
| `GET` | `/reviews/agent/{id}` | Reseñas de un repartidor |
| `GET` | `/reviews/restaurant/{id}/average` | Rating promedio del restaurante |

---

### 9. search-service — Puerto 9089 · BD: `db_search`

Permite buscar restaurantes por nombre (búsqueda `LIKE`), zona o categoría. Mantiene un índice local (`RestaurantIndex`) ordenado por rating promedio. **Es la única ruta pública que no requiere token JWT.**

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/search/restaurants?name=X` | Buscar por nombre |
| `GET` | `/search/restaurants?zone=X` | Buscar por zona (orden por rating) |
| `GET` | `/search/restaurants?category=X` | Buscar por categoría |

---

### 10. report-service — Puerto 9095 · BD: `db_report`

Consume eventos Kafka de `order.placed` y `order.delivered` para registrar un historial de pedidos. Expone reportes de uso accesibles solo para el rol `ADMIN`.

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/reports/all` | Reporte global de pedidos (solo `ADMIN`) |
| `GET` | `/reports/restaurant/{id}` | Reporte por restaurante (solo `ADMIN`) |

---

## Flujo completo de un pedido

```
1. POST /auth/login
   └─ Obtener token JWT

2. GET  /search/restaurants?zone=X        (sin token requerido)
   └─ Buscar restaurante y ver menú

3. POST /orders
   ├─ Feign → verifica que el restaurante esté abierto
   ├─ Feign → verifica stock de cada ítem
   └─ Kafka → publica "order.placed"

4. POST /payments
   └─ Kafka → publica "payment.completed"

5. PATCH /orders/{id}/status              (CONFIRMED → PREPARING → READY)

6. POST /deliveries/assign
   └─ Asigna el primer repartidor disponible en la zona

7. PATCH /deliveries/{id}/status          (IN_DELIVERY → DELIVERED)
   └─ Kafka → publica "order.delivered"

8. POST /reviews                          (solo si el pedido está DELIVERED)
   └─ Feign → verifica estado del pedido
```

---

## Eventos Kafka publicados

| Tópico | Publicado por | Consumido por |
|---|---|---|
| `order.placed` | order-service | notification-service, report-service |
| `order.confirmed` | order-service | notification-service |
| `payment.completed` | payment-service | notification-service |
| `payment.failed` | payment-service | notification-service |
| `order.delivered` | delivery-service | notification-service, report-service |
| `stock.low` | restaurant-service | notification-service |
