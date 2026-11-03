package com.learnhub.course.course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 5000)
    private String description;

    // e.g. "Programming". Optional: older courses don't have one
    @Column(length = 100)
    private String category;

    // "Beginner", "Intermediate" or "Advanced" (checked by CourseRequest). Optional
    @Column(length = 20)
    private String level;

    // The user id from the instructor's JWT ("sub"). Not a foreign key:
    // the users table lives in a different database owned by auth-service.
    @Column(nullable = false)
    private Long instructorId;

    // Copied from the JWT "name" claim so the catalog never has to call auth-service
    @Column(nullable = false, length = 100)
    private String instructorName;

    // Drafts are hidden from the public catalog until the instructor publishes them
    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected Course() {
    }

    public Course(String title, String description, String category, String level, boolean published,
                  Long instructorId, String instructorName) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.published = published;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate // runs before each UPDATE
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public void update(String title, String description, String category, String level, boolean published) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.published = published;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLevel() {
        return level;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public boolean isPublished() {
        return published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
