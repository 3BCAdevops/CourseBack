package course_enrollment.controller;

import course_enrollment.model.Course;
import course_enrollment.repository.CourseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        if (course.getEnrolledCount() == null) {
            course.setEnrolledCount(0);
        }
        return courseRepository.save(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course updated) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setName(updated.getName());
                    course.setDescription(updated.getDescription());
                    if (updated.getEnrolledCount() != null) {
                        course.setEnrolledCount(updated.getEnrolledCount());
                    }
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }
}
