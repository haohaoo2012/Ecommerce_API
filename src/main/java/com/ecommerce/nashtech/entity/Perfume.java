package com.ecommerce.nashtech.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "perfume",indexes = {
        @Index(name = "perfume_pkey", columnList = "id,perfumeTitle,perfumer,price")
})



public class Perfume {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfume_id_seq")
    @SequenceGenerator(name = "perfume_id_seq", sequenceName = "perfume_id_seq", initialValue = 109, allocationSize = 1)
    @Column(name = "id")
    private Long id;
    @Column(name = "perfumeTitle")
    private String perfumeTitle;
    @Column(name = "perfumer")
    private String perfumer;
    @Column(name = "year")
    private Integer year;
    @Column(name = "country")
    private String country;
    @Column(name = "perfumeGender")
    private String perfumeGender;
    @Column(name = "fragranceTopNotes")
    private String fragranceTopNotes;
    @Column(name = "fragranceMiddleNotes")
    private String fragranceMiddleNotes;
    @Column(name = "fragranceBaseNotes")
    private String fragranceBaseNotes;
    @Column(name = "description")
    private String description;
    @Column(name = "filename")
    private String filename;
    @Column(name = "price")
    private Integer price;
    @Column(name = "volume")
    private String volume;
    @Column(name = "type")
    private String type;
    @Column(name = "perfumeRating")
    private Double perfumeRating;

    @OneToMany
    private List<Review> reviews;
}
