package com.learnhub.course.course;

import com.learnhub.course.module.CourseModuleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;

    public CourseService(CourseRepository courseRepository, CourseModuleRepository moduleRepository) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> listPublished() {
        return courseRepository.findByPublishedTrueOrderByCreatedAtDesc().stream()
                .map(CourseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> listMine(CurrentUser user) {
        return courseRepository.findByInstructorIdOrderByCreatedAtDesc(user.id()).stream()
                .map(CourseResponse::from)
                .toList();
    }

    /** Admin only (see SecurityConfig): every instructor's courses, drafts included. */
    @Transactional(readOnly = true)
    public List<CourseResponse> listAll() {
        return courseRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(CourseResponse::from)
                .toList();
    }

    /** Admin only (see SecurityConfig). Takes the course out of the catalog without deleting anything. */
    @Transactional
    public CourseResponse unpublish(Long id) {
        Course course = findOrThrow(id);
        course.unpublish();
        return CourseResponse.from(course);
    }

    /**
     * Published courses are public. A draft is visible only to its owner or an admin;
     * everyone else gets 404 (not 403), so drafts don't reveal that they exist.
     * @param user null when the caller sent no token
     */
    @Transactional(readOnly = true)
    public CourseResponse get(Long id, CurrentUser user) {
        Course course = findOrThrow(id);
        if (!course.isPublished() && !canManage(course, user)) {
            throw new CourseNotFoundException(id);
        }
        return CourseResponse.from(course);
    }

    @Transactional
    public CourseResponse create(CourseRequest request, CurrentUser user) {
        Course course = new Course(request.title().trim(), request.description(), request.category(), request.level(),
                request.isPublishedOrDefault(), user.id(), user.name());
        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse update(Long id, CourseRequest request, CurrentUser user) {
        Course course = findOrThrow(id);
        requireCanManage(course, user);
        // No save() needed: inside a transaction, Hibernate notices the changed fields
        // ("dirty checking") and issues the UPDATE when the transaction commits.
        course.update(request.title().trim(), request.description(), request.category(), request.level(),
                request.isPublishedOrDefault());
        return CourseResponse.from(course);
    }

    @Transactional
    public void delete(Long id, CurrentUser user) {
        Course course = findOrThrow(id);
        requireCanManage(course, user);
        // Children first: modules reference the course through a foreign key
        moduleRepository.deleteAll(moduleRepository.findByCourseIdOrderByPositionAsc(id));
        courseRepository.delete(course);
    }

    /** The course, if this user may change it: used by CourseModuleService. */
    public Course requireManageable(Long id, CurrentUser user) {
        Course course = findOrThrow(id);
        requireCanManage(course, user);
        return course;
    }

    private Course findOrThrow(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
    }

    private boolean canManage(Course course, CurrentUser user) {
        return user != null && (user.isAdmin() || course.getInstructorId().equals(user.id()));
    }

    private void requireCanManage(Course course, CurrentUser user) {
        if (!canManage(course, user)) {
            throw new AccessDeniedException("You can only modify your own courses");
        }
    }
}
