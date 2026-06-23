package com.foodmarket.order.service;

import com.foodmarket.order.client.RestaurantFeignClient;
import com.foodmarket.order.dto.*;
import com.foodmarket.order.event.OrderEvent;
import com.foodmarket.order.exception.BusinessException;
import com.foodmarket.order.exception.ResourceNotFoundException;
import com.foodmarket.order.kafka.OrderEventProducer;
import com.foodmarket.order.model.Order;
import com.foodmarket.order.model.OrderStatus;
import com.foodmarket.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService - Pruebas unitarias")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private RestaurantFeignClient restaurantClient;

    @Mock
    private OrderEventProducer eventProducer;

    @InjectMocks
    private OrderService orderService;

    private RestaurantClientDTO openRestaurant;
    private RestaurantClientDTO closedRestaurant;
    private MenuItemClientDTO availableItem;
    private MenuItemClientDTO unavailableItem;
    private Order savedOrder;
    private CreateOrderDTO createOrderDTO;

    @BeforeEach
    void setUp() {
        openRestaurant = new RestaurantClientDTO();
        openRestaurant.setId(1L);
        openRestaurant.setStatus("OPEN");
        openRestaurant.setZone("PROVIDENCIA");

        closedRestaurant = new RestaurantClientDTO();
        closedRestaurant.setId(2L);
        closedRestaurant.setStatus("CLOSED");
        closedRestaurant.setZone("PROVIDENCIA");

        availableItem = new MenuItemClientDTO();
        availableItem.setId(1L);
        availableItem.setName("Pizza Margherita");
        availableItem.setPrice(new BigDecimal("9990"));
        availableItem.setStock(5);
        availableItem.setAvailable(true);

        unavailableItem = new MenuItemClientDTO();
        unavailableItem.setId(2L);
        unavailableItem.setName("Pizza Agotada");
        unavailableItem.setPrice(new BigDecimal("9990"));
        unavailableItem.setStock(0);
        unavailableItem.setAvailable(false);

        CreateOrderDTO.OrderItemDTO itemDTO = new CreateOrderDTO.OrderItemDTO();
        itemDTO.setMenuItemId(1L);
        itemDTO.setQuantity(2);

        createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setRestaurantId(1L);
        createOrderDTO.setDeliveryAddress("Av. Providencia 1234");
        createOrderDTO.setDeliveryZone("PROVIDENCIA");
        createOrderDTO.setItems(List.of(itemDTO));

        savedOrder = Order.builder()
                .id(100L)
                .customerId(1L)
                .restaurantId(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("21970"))
                .deliveryFee(new BigDecimal("1990"))
                .deliveryAddress("Av. Providencia 1234")
                .deliveryZone("PROVIDENCIA")
                .items(new ArrayList<>())
                .build();
    }

    // ────────── createOrder ──────────

    @Test
    @DisplayName("createOrder - pedido válido se crea exitosamente y publica evento Kafka")
    void createOrder_pedidoValido_seCrearYPublicaEventoKafka() {
        // Given
        when(restaurantClient.getRestaurant(1L)).thenReturn(openRestaurant);
        when(restaurantClient.getMenuItem(1L, 1L)).thenReturn(availableItem);
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        // When
        OrderResponseDTO result = orderService.createOrder(createOrderDTO, 1L);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(eventProducer, times(1)).publishOrderCreated(any(OrderEvent.class));
    }

    @Test
    @DisplayName("createOrder - restaurante CERRADO lanza BusinessException")
    void createOrder_restauranteCerrado_lanzaBusinessException() {
        // Given
        when(restaurantClient.getRestaurant(1L)).thenReturn(closedRestaurant);

        // When / Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(createOrderDTO, 1L));
        assertTrue(ex.getMessage().contains("cerrado"));
        verify(orderRepo, never()).save(any());
        verify(eventProducer, never()).publishOrderCreated(any());
    }

    @Test
    @DisplayName("createOrder - zona diferente al restaurante lanza BusinessException")
    void createOrder_zonaDistintaAlRestaurante_lanzaBusinessException() {
        // Given
        createOrderDTO.setDeliveryZone("LAS_CONDES");
        when(restaurantClient.getRestaurant(1L)).thenReturn(openRestaurant);

        // When / Then
        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(createOrderDTO, 1L));
        assertTrue(ex.getMessage().contains("zona"));
        verify(orderRepo, never()).save(any());
    }

    @Test
    @DisplayName("createOrder - item sin stock lanza BusinessException")
    void createOrder_itemSinStock_lanzaBusinessException() {
        // Given
        when(restaurantClient.getRestaurant(1L)).thenReturn(openRestaurant);
        when(restaurantClient.getMenuItem(1L, 1L)).thenReturn(unavailableItem);

        // When / Then
        assertThrows(BusinessException.class, () -> orderService.createOrder(createOrderDTO, 1L));
        verify(orderRepo, never()).save(any());
    }

    @Test
    @DisplayName("createOrder - el total incluye fee de delivery de $1990")
    void createOrder_elTotalIncluyeFeeDelivery() {
        // Given
        when(restaurantClient.getRestaurant(1L)).thenReturn(openRestaurant);
        when(restaurantClient.getMenuItem(1L, 1L)).thenReturn(availableItem);
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        // When
        OrderResponseDTO result = orderService.createOrder(createOrderDTO, 1L);

        // Then
        assertEquals(new BigDecimal("1990"), result.getDeliveryFee());
    }

    // ────────── updateStatus ──────────

    @Test
    @DisplayName("updateStatus - PENDING a CONFIRMED es transicion valida")
    void updateStatus_pendingAConfirmed_esValido() {
        // Given
        savedOrder.setStatus(OrderStatus.PENDING);
        when(orderRepo.findById(100L)).thenReturn(Optional.of(savedOrder));
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        // When
        OrderResponseDTO result = orderService.updateStatus(100L, OrderStatus.CONFIRMED, "ADMIN");

        // Then
        assertNotNull(result);
        verify(eventProducer, times(1)).publishStatusChanged(any(OrderEvent.class));
    }

    @Test
    @DisplayName("updateStatus - PREPARING a CANCELLED solo ADMIN puede cancelar")
    void updateStatus_preparingACancelled_soloAdminPuedeCancelar() {
        // Given
        savedOrder.setStatus(OrderStatus.PREPARING);
        when(orderRepo.findById(100L)).thenReturn(Optional.of(savedOrder));

        // When / Then
        assertThrows(BusinessException.class,
                () -> orderService.updateStatus(100L, OrderStatus.CANCELLED, "CUSTOMER"));
    }

    @Test
    @DisplayName("updateStatus - DELIVERED publica evento ORDER_DELIVERED en Kafka")
    void updateStatus_delivered_publicaEventoDelivered() {
        // Given
        savedOrder.setStatus(OrderStatus.IN_DELIVERY);
        when(orderRepo.findById(100L)).thenReturn(Optional.of(savedOrder));
        when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

        // When
        orderService.updateStatus(100L, OrderStatus.DELIVERED, "ADMIN");

        // Then
        verify(eventProducer, times(1)).publishOrderDelivered(any(OrderEvent.class));
        verify(eventProducer, never()).publishStatusChanged(any());
    }

    @Test
    @DisplayName("updateStatus - transicion invalida lanza BusinessException")
    void updateStatus_transicionInvalida_lanzaBusinessException() {
        // Given
        savedOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepo.findById(100L)).thenReturn(Optional.of(savedOrder));

        // When / Then
        assertThrows(BusinessException.class,
                () -> orderService.updateStatus(100L, OrderStatus.PENDING, "ADMIN"));
    }

    @Test
    @DisplayName("getById - pedido no encontrado lanza ResourceNotFoundException")
    void getById_pedidoNoEncontrado_lanzaResourceNotFoundException() {
        // Given
        when(orderRepo.findById(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(999L));
    }
}
