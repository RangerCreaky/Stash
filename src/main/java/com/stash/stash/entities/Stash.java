package com.stash.stash.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "stash")
public class Stash {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stash_id")
    private Long id;

    @Column(name = "name")
    @NotNull(message = "Stash must have a unique name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "private")
    private Boolean isPrivate;

    @Column(name = "created_by")
    private Long createdBy;

    @ManyToMany(mappedBy = "stashes")
    @JsonBackReference
    private Set<User> users;

    @JsonIgnore
    @OneToMany(mappedBy = "stash")
    private Set<Link> links;

    @OneToOne(mappedBy = "stash")
    private ShareStash shareStash;

    @Override
    public String toString() {
        return "Stash{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
