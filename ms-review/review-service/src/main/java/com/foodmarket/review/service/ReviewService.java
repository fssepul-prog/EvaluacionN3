package com.foodmarket.review.service;
import com.foodmarket.review.client.OrderFeignClient; import com.foodmarket.review.dto.*; import com.foodmarket.review.exception.*; import com.foodmarket.review.model.*;
import com.foodmarket.review.repository.ReviewRepository; import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service; import java.util.List; import java.util.stream.Collectors;
@Service @RequiredArgsConstructor @Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepo; private final OrderFeignClient orderClient;
    public ReviewResponseDTO create(ReviewDTO dto) {
        OrderFeignClient.OrderStatusDTO order = orderClient.getOrder(dto.getOrderId());
        if (!"DELIVERED".equals(order.getStatus())) throw new BusinessException("Solo pedidos DELIVERED pueden ser resenados. Estado: "+order.getStatus());
        if (reviewRepo.existsByOrderIdAndCustomerIdAndTargetType(dto.getOrderId(),dto.getCustomerId(),dto.getTargetType())) throw new BusinessException("Ya existe resena de tipo "+dto.getTargetType()+" para este pedido");
        Review r = Review.builder().orderId(dto.getOrderId()).customerId(dto.getCustomerId()).targetId(dto.getTargetId()).targetType(dto.getTargetType()).rating(dto.getRating()).comment(dto.getComment()).build();
        reviewRepo.save(r); log.info("Resena creada: orden={} tipo={} rating={}", dto.getOrderId(), dto.getTargetType(), dto.getRating());
        return toDTO(r);
    }
    public List<ReviewResponseDTO> getByRestaurant(Long id) { return reviewRepo.findByTargetIdAndTargetType(id,TargetType.RESTAURANT).stream().map(this::toDTO).collect(Collectors.toList()); }
    public List<ReviewResponseDTO> getByAgent(Long id) { return reviewRepo.findByTargetIdAndTargetType(id,TargetType.AGENT).stream().map(this::toDTO).collect(Collectors.toList()); }
    public Double getAvgRating(Long targetId, TargetType type) { return reviewRepo.findAverageRating(targetId,type); }
    private ReviewResponseDTO toDTO(Review r) { return ReviewResponseDTO.builder().id(r.getId()).orderId(r.getOrderId()).customerId(r.getCustomerId()).targetId(r.getTargetId()).targetType(r.getTargetType()).rating(r.getRating()).comment(r.getComment()).createdAt(r.getCreatedAt()).build(); }
}
