package com.learnhub.course.module;

import com.learnhub.course.client.EnrollmentClient;
import com.learnhub.course.course.Course;
import com.learnhub.course.course.CourseResponse;
import com.learnhub.course.course.CourseService;
import com.learnhub.course.course.CurrentUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseModuleService {

    private final CourseModuleRepository moduleRepository;
    private final CourseService courseService;
    private final EnrollmentClient enrollmentClient;

    public CourseModuleService(CourseModuleRepository moduleRepository, CourseService courseService,
                               EnrollmentClient enrollmentClient) {
        this.moduleRepository = moduleRepository;
        this.courseService = courseService;
        this.enrollmentClient = enrollmentClient;
    }

    /** The outline is public, but a draft course's outline stays hidden like the draft itself. */
    @Transactional(readOnly = true)
    public List<ModuleSummary> list(Long courseId, CurrentUser user) {
        courseService.get(courseId, user); // 404 if missing or a draft this user can't see
        return moduleRepository.findByCourseIdOrderByPositionAsc(courseId).stream()
                .map(ModuleSummary::from)
                .toList();
    }

    /**
     * Video and notes. Deliberately NOT @Transactional: the enrollment check is an HTTP call,
     * and no database connection should be held open while waiting for it.
     */
    public ModuleResponse get(Long courseId, Long moduleId, CurrentUser user) {
        CourseResponse course = courseService.get(courseId, user);
        boolean manages = user.isAdmin() || course.instructorId().equals(user.id());
        if (!manages) {
            if (!user.isStudent() || !enrollmentClient.isEnrolled(courseId, user.tokenValue())) {
                throw new AccessDeniedException("Enroll in this course to view its lessons");
            }
        }
        return ModuleResponse.from(findOrThrow(courseId, moduleId));
    }

    @Transactional
    public ModuleResponse create(Long courseId, ModuleRequest request, CurrentUser user) {
        Course course = courseService.requireManageable(courseId, user);
        String title = request.title().trim();
        if (moduleRepository.existsByCourseIdAndTitle(courseId, title)) {
            throw new DuplicateModuleException(title);
        }
        int nextPosition = moduleRepository.countByCourseId(courseId) + 1;
        CourseModule module = new CourseModule(course, nextPosition, title, request.description(),
                request.videoId(), request.notes());
        return ModuleResponse.from(moduleRepository.save(module));
    }

    @Transactional
    public ModuleResponse update(Long courseId, Long moduleId, ModuleRequest request, CurrentUser user) {
        courseService.requireManageable(courseId, user);
        CourseModule module = findOrThrow(courseId, moduleId);
        String title = request.title().trim();
        if (moduleRepository.existsByCourseIdAndTitleAndIdNot(courseId, title, moduleId)) {
            throw new DuplicateModuleException(title);
        }
        module.update(title, request.description(), request.videoId(), request.notes());
        return ModuleResponse.from(module);
    }

    @Transactional
    public void delete(Long courseId, Long moduleId, CurrentUser user) {
        courseService.requireManageable(courseId, user);
        moduleRepository.delete(findOrThrow(courseId, moduleId));
        moduleRepository.flush();

        // Close the gap, so numbering stays 1, 2, 3...
        List<CourseModule> remaining = moduleRepository.findByCourseIdOrderByPositionAsc(courseId);
        for (int i = 0; i < remaining.size(); i++) {
            remaining.get(i).setPosition(i + 1);
        }
    }

    @Transactional
    public List<ModuleSummary> reorder(Long courseId, ReorderRequest request, CurrentUser user) {
        courseService.requireManageable(courseId, user);
        List<CourseModule> modules = moduleRepository.findByCourseIdOrderByPositionAsc(courseId);

        // Must be a true rearrangement: every module once, nothing extra, nothing missing
        boolean sameModules = request.moduleIds().size() == modules.size()
                && new HashSet<>(request.moduleIds()).equals(
                        modules.stream().map(CourseModule::getId).collect(Collectors.toSet()));
        if (!sameModules) {
            throw new InvalidModuleOrderException();
        }

        var byId = modules.stream().collect(Collectors.toMap(CourseModule::getId, Function.identity()));
        for (int i = 0; i < request.moduleIds().size(); i++) {
            byId.get(request.moduleIds().get(i)).setPosition(i + 1);
        }
        return request.moduleIds().stream().map(byId::get).map(ModuleSummary::from).toList();
    }

    private CourseModule findOrThrow(Long courseId, Long moduleId) {
        return moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ModuleNotFoundException(moduleId));
    }
}
