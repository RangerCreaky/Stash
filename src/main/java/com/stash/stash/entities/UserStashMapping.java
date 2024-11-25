package com.stash.stash.entities;

import com.stash.stash.constants.RoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_stash_mapping")
public class UserStashMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stash_id")
    private Stash stash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleEnum role;

}
