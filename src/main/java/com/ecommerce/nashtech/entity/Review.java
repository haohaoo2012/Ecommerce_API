package com.ecommerce.nashtech.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "review",indexes = {
        @Index(name = "review_pkey", columnList = "id,author,message,rating,date")
})
public class Review {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "author")
    private String author;
    @Column(name = "message")
    private String message;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "date")
    private LocalDate date;

    public Review() {
        this.date = LocalDate.now();
    }
}
