package com.learnhub.assessment.assessment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** A quiz belonging to one course. */
@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ids from other services: plain numbers, not foreign keys
    @Column(nullable = false)
    private Long courseId;

    // Copied from course-service at creation, so later "is this the owner?" checks are local
    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Same database, so this IS a real foreign key (questions.assessment_id).
     * cascade ALL: saving/deleting the assessment saves/deletes its questions.
     * orphanRemoval: a question removed from this list is deleted from the table.
     * Questions are lazy-loaded; AssessmentRepository fetches them with @EntityGraph when needed.
     *
     * @OrderColumn stores each question's position (questions.position). Besides keeping the
     * instructor's order, it makes this an "indexed list": when a fetch join returns one row per
     * OPTION, Hibernate puts each question at its position once, instead of adding duplicates.
     */
    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "position")
    private List<Question> questions = new ArrayList<>();

    protected Assessment() {
    }

    public Assessment(Long courseId, Long instructorId, String title, String description) {
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.title = title;
        this.description = description;
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    /** Keeps both sides of the relationship in sync. */
    public void addQuestion(Question question) {
        question.setAssessment(this);
        questions.add(question);
    }

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
