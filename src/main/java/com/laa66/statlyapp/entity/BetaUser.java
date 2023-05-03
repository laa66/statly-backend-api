package com.laa66.statlyapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "beta_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BetaUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "full_name")
    private String fullName;

    private String email;
    private LocalDateTime date;
}
