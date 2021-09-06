package com.mikhaylova.lms.domain;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String username;

    @Column
    String password;

    @Column
    String firstName;

    @Column
    String lastName;

    @Column
    String email;

    @Column
    String phoneNumber;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date registerDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    Date modificationDate;

    @ManyToOne
    @JoinColumn(name = "modification_author_id")
    User modificationAuthor;

    @ManyToMany(mappedBy = "users")
    Set<Course> courses;

    @ManyToMany
    Set<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private AvatarImage avatarImage;

    public User(Long id, String username, Set<Course> courses, Set<Role> roles, String password) {
        this.id = id;
        this.username = username;
        this.courses = courses;
        this.roles = roles;
        this.password = password;
    }

    public User(String username, String password, String email, Date registerDate, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.registerDate = registerDate;
        this.roles = roles;
    }

    public User(String username, String password, Set<Role> roles) {
        this.username = username;
        this.roles = roles;
        this.password = password;
    }
}