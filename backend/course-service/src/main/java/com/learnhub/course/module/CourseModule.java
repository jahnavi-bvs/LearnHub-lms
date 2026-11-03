package com.learnhub.course.module;

import com.learnhub.course.course.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * One unit of a course: a YouTube video plus lesson notes.
 * (Named CourseModule because java.lang.Module already exists.)
 */
@Entity
@Table(name = "course_modules",
        // The database itself guarantees a course can't contain the same module twice
        uniqueConstraints = @UniqueConstraint(name = "uk_module_course_title", columnNames = {"course_id", "title"}))
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Same database as courses, so this is a real foreign key
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;

    // 1, 2, 3... within this course; every course numbers its own modules from 1
    @Column(nullable = false)
    private int position;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    // The 11-character YouTube id, or null when a video still needs instructor input
    @Column(length = 11)
    private String youtubeVideoId;

    // Markdown text. TEXT holds up to 65,535 bytes, more than ModuleRequest allows
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected CourseModule() {
    }

    public CourseModule(Course course, int position, String title, String description, String youtubeVideoId,
                        String notes) {
        this.course = course;
        this.position = position;
        update(title, description, youtubeVideoId, notes);
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public final void update(String title, String description, String youtubeVideoId, String notes) {
        this.title = title;
        this.description = description;
        this.youtubeVideoId = youtubeVideoId;
        this.notes = notes;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getYoutubeVideoId() {
        return youtubeVideoId;
    }

    public String getNotes() {
        return notes;
    }
}
