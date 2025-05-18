package com.alten.business.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "baskets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "basket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BasketProduct> basketProducts = new ArrayList<>();


    public void addProduct(Product product, Integer quantity) {
        BasketProduct basketProduct = new BasketProduct();
        basketProduct.setBasket(this);
        basketProduct.setProduct(product);
        basketProduct.setQuantity(quantity);
        basketProducts.add(basketProduct);
    }

}