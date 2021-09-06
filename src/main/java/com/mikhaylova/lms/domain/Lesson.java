package com.mikhaylova.lms.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    String title;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column
    String text;

    @ManyToOne
    private Module module;

    @ManyToOne
    private Course course;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_author")
    private User createdUser;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_author")
    private User updatedUser;

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    public Lesson(Long id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public Lesson(Long id, String title, String text, Module module) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.module = module;
    }

    public Lesson(Long id, String title, String text, Course course) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.course = course;
    }
}