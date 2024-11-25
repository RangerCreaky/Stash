package com.stash.stash.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email")
    @NotNull(message = "Email address cannot be null")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "bio")
    private String bio;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "user_stash_mapping",
            joinColumns = {
                @JoinColumn(name = "user_id")
            },
            inverseJoinColumns = {
                @JoinColumn(name = "stash_id")
            }
    )
    @JsonManagedReference
    private Set<Stash> stashes;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }

}
