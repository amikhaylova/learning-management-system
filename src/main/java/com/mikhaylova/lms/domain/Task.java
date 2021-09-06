package com.mikhaylova.lms.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String type;

    @Lob
    @Column
    private String content;

    @ManyToOne
    private Lesson lesson;

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

    public Task(Long id, String name, String description, String type, String content, Lesson lesson) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.content = content;
        this.lesson = lesson;
    }
}
