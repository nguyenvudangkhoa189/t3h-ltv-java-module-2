package vn.demo.enterprise.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import vn.demo.enterprise.model.Student;
import vn.demo.enterprise.model.StudentForm;

@Service
public class StudentService {

	private final List<Student> students = new ArrayList<>();
	private final AtomicLong idSequence = new AtomicLong(1);

	public StudentService() {
		students.add(new Student(idSequence.getAndIncrement(), "Nguyễn Văn A", "nguyenvana@example.com"));
		students.add(new Student(idSequence.getAndIncrement(), "Trần Thị B", "tranthib@example.com"));
	}

	public List<Student> findAll() {
		return List.copyOf(students);
	}

	public Student findById(Long id) {
		return findOptionalById(id)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên id=" + id));
	}

	public Optional<Student> findOptionalById(Long id) {
		return students.stream()
				.filter(student -> student.getId().equals(id))
				.findFirst();
	}

	public Student save(StudentForm form) {
		Student student = new Student(idSequence.getAndIncrement(), form.getName(), form.getEmail());
		students.add(student);
		return student;
	}

}
