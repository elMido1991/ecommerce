package com.alten.business.db.repositories;

import com.alten.business.db.entities.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    Optional<Basket> findBasketByUserId(Long userId);
}