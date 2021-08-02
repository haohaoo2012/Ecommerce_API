package com.ecommerce.nashtech.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data

public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    @SequenceGenerator(name = "order_item_seq", sequenceName = "order_item_seq", initialValue = 12, allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "amount")
    private Long amount;
    @Column(name = "quantity")
    private Long quantity;

    @OneToOne
    private Perfume perfume;
}
