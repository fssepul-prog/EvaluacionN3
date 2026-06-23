package com.foodmarket.restaurant.service;

import com.foodmarket.restaurant.dto.MenuItemDTO;
import com.foodmarket.restaurant.dto.RestaurantDTO;
import com.foodmarket.restaurant.exception.ResourceNotFoundException;
import com.foodmarket.restaurant.model.MenuItem;
import com.foodmarket.restaurant.model.Restaurant;
import com.foodmarket.restaurant.model.RestaurantStatus;
import com.foodmarket.restaurant.repository.MenuItemRepository;
import com.foodmarket.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestaurantService - Pruebas unitarias")
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepo;

    @Mock
    private MenuItemRepository menuItemRepo;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;
    private MenuItem menuItem;
    private RestaurantDTO restaurantDTO;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .id(1L)
                .ownerId(10L)
                .name("Pizza Express")
                .category("ITALIANA")
                .zone("PROVIDENCIA")
                .status(RestaurantStatus.OPEN)
                .build();

        menuItem = MenuItem.builder()
                .id(1L)
                .restaurant(restaurant)
                .name("Pizza Margherita")
                .price(new BigDecimal("9990"))
                .stock(10)
                .available(true)
                .build();

        restaurantDTO = RestaurantDTO.builder()
                .name("Pizza Express")
                .category("ITALIANA")
                .zone("PROVIDENCIA")
                .openTime(LocalTime.of(8, 0))
                .closeTime(LocalTime.of(22, 0))
                .build();
    }

    // ────────── create ──────────

    @Test
    @DisplayName("crear restaurante - se guarda con estado OPEN por defecto")
    void create_guardaRestauranteConEstadoOpen() {
        // Given
        when(restaurantRepo.save(any(Restaurant.class))).thenReturn(restaurant);

        // When
        RestaurantDTO result = restaurantService.create(restaurantDTO, 10L);

        // Then
        assertNotNull(result);
        assertEquals("Pizza Express", result.getName());
        assertEquals(RestaurantStatus.OPEN, result.getStatus());
        verify(restaurantRepo, times(1)).save(any(Restaurant.class));
    }

    // ────────── getById ──────────

    @Test
    @DisplayName("getById - restaurante existente retorna DTO")
    void getById_restauranteExistente_retornaDTO() {
        // Given
        when(restaurantRepo.findById(1L)).thenReturn(Optional.of(restaurant));

        // When
        RestaurantDTO result = restaurantService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Pizza Express", result.getName());
        assertEquals("PROVIDENCIA", result.getZone());
    }

    @Test
    @DisplayName("getById - restaurante no encontrado lanza ResourceNotFoundException")
    void getById_restauranteNoEncontrado_lanzaResourceNotFoundException() {
        // Given
        when(restaurantRepo.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> restaurantService.getById(99L));
    }

    // ────────── getByZone ──────────

    @Test
    @DisplayName("getByZone - retorna solo restaurantes OPEN de la zona")
    void getByZone_retornaSoloAbiertosEnZona() {
        // Given
        when(restaurantRepo.findByZoneAndStatus("PROVIDENCIA", RestaurantStatus.OPEN))
                .thenReturn(List.of(restaurant));

        // When
        List<RestaurantDTO> result = restaurantService.getByZone("PROVIDENCIA");

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(RestaurantStatus.OPEN, result.get(0).getStatus());
    }

    // ────────── addMenuItem ──────────

    @Test
    @DisplayName("addMenuItem - item con stock > 0 se crea disponible")
    void addMenuItem_conStockPositivo_seGuardaDisponible() {
        // Given
        MenuItemDTO itemDTO = MenuItemDTO.builder()
                .name("Pizza Margherita")
                .price(new BigDecimal("9990"))
                .stock(10)
                .build();
        when(restaurantRepo.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepo.save(any(MenuItem.class))).thenReturn(menuItem);

        // When
        MenuItemDTO result = restaurantService.addMenuItem(1L, itemDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.isAvailable());
        verify(menuItemRepo, times(1)).save(any(MenuItem.class));
    }

    @Test
    @DisplayName("addMenuItem - item con stock 0 se crea no disponible")
    void addMenuItem_conStockCero_seGuardaNoDisponible() {
        // Given
        MenuItemDTO itemDTO = MenuItemDTO.builder()
                .name("Pizza Agotada")
                .price(new BigDecimal("9990"))
                .stock(0)
                .build();
        MenuItem itemSinStock = MenuItem.builder()
                .id(2L).restaurant(restaurant).name("Pizza Agotada")
                .price(new BigDecimal("9990")).stock(0).available(false).build();
        when(restaurantRepo.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepo.save(any(MenuItem.class))).thenReturn(itemSinStock);

        // When
        MenuItemDTO result = restaurantService.addMenuItem(1L, itemDTO);

        // Then
        assertFalse(result.isAvailable());
        assertEquals(0, result.getStock());
    }

    // ────────── updateStock ──────────

    @Test
    @DisplayName("updateStock - stock > 0 mantiene item disponible")
    void updateStock_conStockPositivo_mantienDisponible() {
        // Given
        when(menuItemRepo.findById(1L)).thenReturn(Optional.of(menuItem));
        when(menuItemRepo.save(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        MenuItemDTO result = restaurantService.updateStock(1L, 5);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getStock());
        assertTrue(result.isAvailable());
    }

    @Test
    @DisplayName("updateStock - stock 0 marca item no disponible")
    void updateStock_conStockCero_marcaNoDisponible() {
        // Given
        when(menuItemRepo.findById(1L)).thenReturn(Optional.of(menuItem));
        when(menuItemRepo.save(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        MenuItemDTO result = restaurantService.updateStock(1L, 0);

        // Then
        assertEquals(0, result.getStock());
        assertFalse(result.isAvailable());
    }

    @Test
    @DisplayName("updateStock - item no encontrado lanza ResourceNotFoundException")
    void updateStock_itemNoEncontrado_lanzaResourceNotFoundException() {
        // Given
        when(menuItemRepo.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> restaurantService.updateStock(99L, 5));
    }

    // ────────── updateStatus ──────────

    @Test
    @DisplayName("updateStatus - cambia estado del restaurante correctamente")
    void updateStatus_cambiaEstadoCorrectamente() {
        // Given
        when(restaurantRepo.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepo.save(any(Restaurant.class))).thenReturn(restaurant);

        // When
        restaurantService.updateStatus(1L, RestaurantStatus.CLOSED);

        // Then
        verify(restaurantRepo).save(argThat(r -> r.getStatus() == RestaurantStatus.CLOSED));
    }
}
