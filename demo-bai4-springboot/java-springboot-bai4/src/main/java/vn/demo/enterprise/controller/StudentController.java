package vn.demo.enterprise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import vn.demo.enterprise.model.StudentForm;
import vn.demo.enterprise.service.StudentService;

@Controller
@RequestMapping("/students")
public class StudentController {

	private final StudentService studentService;

	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("students", studentService.findAll());
		return "enterprise/students/list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("student", studentService.findById(id));
		return "enterprise/students/detail";
	}

	@GetMapping("/new")
	public String showForm(Model model) {
		model.addAttribute("form", new StudentForm());
		return "enterprise/students/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("form") StudentForm form, BindingResult result) {
		if (result.hasErrors()) {
			return "enterprise/students/form";
		}
		studentService.save(form);
		return "redirect:/students";
	}

}
