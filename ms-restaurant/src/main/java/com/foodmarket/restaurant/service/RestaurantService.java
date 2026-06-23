package com.foodmarket.restaurant.service;

import com.foodmarket.restaurant.dto.*;
import com.foodmarket.restaurant.exception.*;
import com.foodmarket.restaurant.model.*;
import com.foodmarket.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepo;
    private final MenuItemRepository menuItemRepo;

    public RestaurantDTO create(RestaurantDTO dto, Long ownerId) {
        Restaurant r = Restaurant.builder().ownerId(ownerId).name(dto.getName())
                .category(dto.getCategory()).zone(dto.getZone())
                .status(RestaurantStatus.OPEN)
                .openTime(dto.getOpenTime()).closeTime(dto.getCloseTime()).build();
        restaurantRepo.save(r);
        log.info("Restaurante creado: {} en zona {}", r.getName(), r.getZone());
        return toDTO(r);
    }

    public RestaurantDTO getById(Long id) {
        return toDTO(restaurantRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado: " + id)));
    }

    public List<RestaurantDTO> getByZone(String zone) {
        return restaurantRepo.findByZoneAndStatus(zone, RestaurantStatus.OPEN)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MenuItemDTO addMenuItem(Long restaurantId, MenuItemDTO dto) {
        Restaurant r = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));
        MenuItem item = MenuItem.builder().restaurant(r).name(dto.getName()).price(dto.getPrice())
                .stock(dto.getStock()).available(dto.getStock() > 0)
                .imageUrl(dto.getImageUrl()).description(dto.getDescription()).build();
        menuItemRepo.save(item);
        log.info("Item agregado: {} (stock: {})", item.getName(), item.getStock());
        return toItemDTO(item);
    }

    public MenuItemDTO updateStock(Long itemId, int newStock) {
        MenuItem item = menuItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado: " + itemId));
        item.setStock(newStock);
        item.setAvailable(newStock > 0);
        menuItemRepo.save(item);
        if (newStock <= 3 && newStock > 0) {
            log.warn("ALERTA STOCK BAJO: item {} tiene {} unidades", itemId, newStock);
        }
        log.info("Stock actualizado: item {} -> {} unidades", itemId, newStock);
        return toItemDTO(item);
    }

    public void updateStatus(Long restaurantId, RestaurantStatus status) {
        Restaurant r = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));
        r.setStatus(status);
        restaurantRepo.save(r);
        log.info("Estado restaurante {} -> {}", restaurantId, status);
    }

    public List<MenuItemDTO> getAvailableMenu(Long restaurantId) {
        return menuItemRepo.findByRestaurantIdAndAvailableTrue(restaurantId)
                .stream().map(this::toItemDTO).collect(Collectors.toList());
    }

    public MenuItemDTO getMenuItemById(Long restaurantId, Long itemId) {
        MenuItem item = menuItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado: " + itemId));
        return toItemDTO(item);
    }

    private RestaurantDTO toDTO(Restaurant r) {
        return RestaurantDTO.builder().id(r.getId()).name(r.getName())
                .category(r.getCategory()).zone(r.getZone()).status(r.getStatus())
                .openTime(r.getOpenTime()).closeTime(r.getCloseTime()).build();
    }

    private MenuItemDTO toItemDTO(MenuItem i) {
        return MenuItemDTO.builder().id(i.getId()).name(i.getName()).price(i.getPrice())
                .stock(i.getStock()).available(i.isAvailable())
                .imageUrl(i.getImageUrl()).description(i.getDescription()).build();
    }
}
