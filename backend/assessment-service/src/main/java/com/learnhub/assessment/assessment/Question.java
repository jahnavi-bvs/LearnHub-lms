package com.learnhub.assessment.assessment;

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
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/** One multiple-choice question. */
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The owning side of the relationship: this column holds the foreign key
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @Column(nullable = false, length = 1000)
    private String text;

    /**
     * A list of plain strings stored in its own table: question_options(question_id, position, option_text).
     * @OrderColumn saves each option's position, so A/B/C/D always come back in the same order.
     */
    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "position")
    @Column(name = "option_text", nullable = false, length = 500)
    private List<String> options = new ArrayList<>();

    // 0-based index into options
    @Column(nullable = false)
    private int correctOptionIndex;

    protected Question() {
    }

    public Question(String text, List<String> options, int correctOptionIndex) {
        this.text = text;
        this.options = new ArrayList<>(options);
        this.correctOptionIndex = correctOptionIndex;
    }

    void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}
