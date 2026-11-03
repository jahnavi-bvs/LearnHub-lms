package com.learnhub.enrollment.progress;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleCompletionRepository extends JpaRepository<ModuleCompletion, Long> {

    List<ModuleCompletion> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentIdAndModuleId(Long enrollmentId, Long moduleId);

    void deleteByEnrollmentIdAndModuleId(Long enrollmentId, Long moduleId);

    void deleteByEnrollmentId(Long enrollmentId);
}
