package ua.edu.ukma.orders.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ua.edu.ukma.orders.entity.Order;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrdersRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(UUID userId);

}