package com.vishwanath.budget_management_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // Primary Key

    @Column(nullable = false)
    private String username;


    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String category;

    @Column(length = 500)
    private String description;

}

