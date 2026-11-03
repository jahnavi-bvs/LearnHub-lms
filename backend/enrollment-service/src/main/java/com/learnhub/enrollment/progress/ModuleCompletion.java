package com.learnhub.enrollment.progress;

import com.learnhub.enrollment.enrollment.Enrollment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/** One module a student has marked as complete. moduleId belongs to course-service: not a foreign key. */
@Entity
@Table(name = "module_completions",
        uniqueConstraints = @UniqueConstraint(name = "uk_completion_enrollment_module",
                columnNames = {"enrollment_id", "module_id"}))
public class ModuleCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(nullable = false, updatable = false)
    private Instant completedAt;

    protected ModuleCompletion() {
    }

    public ModuleCompletion(Enrollment enrollment, Long moduleId) {
        this.enrollment = enrollment;
        this.moduleId = moduleId;
    }

    @PrePersist
    void onCreate() {
        completedAt = Instant.now();
    }

    public Long getModuleId() {
        return moduleId;
    }
}
