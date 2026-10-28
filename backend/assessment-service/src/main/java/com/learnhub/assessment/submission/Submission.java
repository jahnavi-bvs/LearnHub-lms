package com.learnhub.assessment.submission;

import com.learnhub.assessment.assessment.Assessment;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** One student's graded attempt at one assessment. */
@Entity
@Table(name = "submissions",
        // One attempt per student per assessment, guaranteed by the database
        uniqueConstraints = @UniqueConstraint(name = "uk_submission_assessment_student",
                columnNames = {"assessment_id", "student_id"}))
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Same database as assessments, so a real foreign key. One-directional:
    // Assessment has no "submissions" list because we never navigate that way.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 100)
    private String studentName;

    // The chosen option index for each question, in question order
    @ElementCollection
    @CollectionTable(name = "submission_answers", joinColumns = @JoinColumn(name = "submission_id"))
    @OrderColumn(name = "position")
    @Column(name = "selected_option_index", nullable = false)
    private List<Integer> answers = new ArrayList<>();

    // Stored, not recalculated: the grade stays fixed even if the quiz is edited later
    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int maxScore;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt;

    protected Submission() {
    }

    public Submission(Assessment assessment, Long studentId, String studentName, List<Integer> answers,
                      int score, int maxScore) {
        this.assessment = assessment;
        this.studentId = studentId;
        this.studentName = studentName;
        this.answers = new ArrayList<>(answers);
        this.score = score;
        this.maxScore = maxScore;
    }

    @PrePersist
    void onCreate() {
        submittedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public int getScore() {
        return score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    /** Rounded to one decimal place, e.g. 66.7. */
    public double getPercentage() {
        return maxScore == 0 ? 0 : Math.round(score * 1000.0 / maxScore) / 10.0;
    }
}
