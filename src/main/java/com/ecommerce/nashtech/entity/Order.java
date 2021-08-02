package com.ecommerce.nashtech.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders",indexes = {
        @Index(name = "orders_pkey", columnList = "id,date,firstName,lastName,email")
})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", initialValue = 6, allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "totalPrice")
    private Double totalPrice;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "firstName")
    private String firstName;
    @Column(name = "lastName")
    private String lastName;
    @Column(name = "city")
    private String city;
    @Column(name = "address")
    private String address;
    @Column(name = "email")
    private String email;
    @Column(name = "phoneNumber")
    private String phoneNumber;
    @Column(name = "postIndex")
    private Integer postIndex;

    @OneToMany(fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    public Order() {
        this.date = LocalDate.now();
        this.orderItems = new ArrayList<>();
    }
}
