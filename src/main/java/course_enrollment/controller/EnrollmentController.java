package course_enrollment.controller;

import course_enrollment.model.Course;
import course_enrollment.model.Enrollment;
import course_enrollment.repository.CourseRepository;
import course_enrollment.repository.EnrollmentRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "http://localhost:3000")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    // GET /api/enrollments -> list all enrollments
    @GetMapping
    public List<Enrollment> getEnrollments() {
        return enrollmentRepository.findAll();
    }

    // POST /api/enrollments -> enroll a student in a course
    @PostMapping
    public Enrollment enroll(@RequestBody Enrollment request) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(request.getStudentId());
        enrollment.setCourseId(request.getCourseId());
        enrollment.setEnrolledAt(LocalDateTime.now());

        Enrollment saved = enrollmentRepository.save(enrollment);

        // increment course enrolledCount
        courseRepository.findById(request.getCourseId()).ifPresent(course -> {
            Integer count = course.getEnrolledCount();
            if (count == null) count = 0;
            course.setEnrolledCount(count + 1);
            courseRepository.save(course);
        });

        return saved;
    }

    // GET /api/enrollments/by-course/{courseId}
    @GetMapping("/by-course/{courseId}")
    public List<Enrollment> getByCourse(@PathVariable Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    // GET /api/enrollments/by-student/{studentId}
    @GetMapping("/by-student/{studentId}")
    public List<Enrollment> getByStudent(@PathVariable Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    // DELETE /api/enrollments/{id} -> drop enrollment and reduce count
    @DeleteMapping("/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentRepository.findById(id).ifPresent(enrollment -> {
            Long courseId = enrollment.getCourseId();

            // delete enrollment
            enrollmentRepository.deleteById(id);

            // decrement enrolledCount
            courseRepository.findById(courseId).ifPresent(course -> {
                Integer count = course.getEnrolledCount();
                if (count == null) count = 0;
                if (count > 0) count--;
                course.setEnrolledCount(count);
                courseRepository.save(course);
            });
        });
    }
}
