package com.miniProject.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.miniProject.mailService.MailService;
import com.miniProject.models.Employee;
import com.miniProject.models.Questionnaire;
import com.miniProject.models.Role;
import com.miniProject.payload.response.UploadFileResponse;
import com.miniProject.repository.EmployeeRepository;
import com.miniProject.repository.QuestionnaireRepository;
import com.miniProject.repository.RoleRepository;
import com.miniProject.service.EmpQuestionnaireService;
import com.miniProject.service.EmployeeService;
import com.miniProject.service.FileStorageService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

	@Autowired
	private MailService notificationService;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	QuestionnaireRepository questionnaireRepository;

	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	EmpQuestionnaireService service;

	@Autowired
	PasswordEncoder encoder;

	@PostMapping("/uploadContentFile/{questionnaire_id}")
	@PreAuthorize("hasRole('SUPERADMIN')or hasRole('ADMIN')")
	public UploadFileResponse uploadContentFile(@RequestParam("file") MultipartFile file,
			@PathVariable Integer questionnaire_id) {
		String fileName = fileStorageService.storeFile(file);

		Optional<Questionnaire> questionnaire = questionnaireRepository.findById(questionnaire_id);
		Questionnaire quest = questionnaire.get();

		String fileDownloadUri = ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/questionnaire")
				.path("/downloadFile/").path(fileName).toUriString();

		quest.setPptUpload(fileDownloadUri);
		questionnaireRepository.save(quest);

		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping("/uploadParticipantFile/{questionnaire_id}")
	@PreAuthorize("hasRole('SUPERADMIN')or hasRole('ADMIN')")
	public UploadFileResponse uploadParticipantFile(@RequestParam("file") MultipartFile file,
			@PathVariable Integer questionnaire_id) {
		String fileName = fileStorageService.storeFile(file);

		Optional<Questionnaire> questionnaire = questionnaireRepository.findById(questionnaire_id);
		Questionnaire quest = questionnaire.get();

		String fileDownloadUri = ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/questionnaire")
				.path("/downloadFile/").path(fileName).toUriString();

		quest.setParticipantList(fileDownloadUri);
		questionnaireRepository.save(quest);

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = fileStorageService.loadFileAsResource(fileName);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			System.out.print("Could not determine file type.");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	
	@PutMapping("/save/{questionnaire_id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public Questionnaire saveQuestionnaire(@RequestBody Questionnaire quest, @PathVariable Integer questionnaire_id) {
		quest.setQuestionnaireId(questionnaire_id);
		questionnaireRepository.save(quest);
		return quest;

	}

	@PostMapping("/publish/{questionnaire_id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
	public String publish(@RequestBody List<Employee> employeeList, @PathVariable Integer questionnaire_id) {

		try {
			
			Optional<Questionnaire> questionnaire = questionnaireRepository.findById(questionnaire_id);
			Questionnaire quest = questionnaire.get();
			for (Employee employee : employeeList) {
				Set<Role> role = new HashSet<>();
				Role userRole = roleRepository.findByName("ROLE_USER")
						.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
				role.add(userRole);
				employee.setRoles(role);

				String password = employee.generatePassword(8);
				employee.setPassword(encoder.encode(password));
				employeeRepository.save(employee);
				
				service.setData(quest.getQuestionnaireId(), employee.getId(), 0);

				String subject = "Questionnaire " + quest.getQuestionnaireId();
				String text = quest.getMailBody() + "\nPassword: " + password + "\nUsername: " + employee.getUsername();
				notificationService.sendEmail(employee, subject, text);
			}

			return "Mail sent successfully";
		} catch (MailException mailException) {
			System.out.println(mailException);
		}
		return null;
	}

	@GetMapping("/generateReport/{questionnaire_id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public List<Object> generateReport(@PathVariable int questionnaire_id) {
		List<EmpQuestionnaire> list = service.findByQuestionnaireId(questionnaire_id);
		List<Object> reports= new ArrayList<Object>();
		for(EmpQuestionnaire emp_quest:list) {
			String emp_id=emp_quest.getEmpId();
			Optional<Employee> employee= employeeRepository.findById(emp_id);
			String name = employee.get().getUsername();
			int status=emp_quest.getStatus();
			reports.add("Employee Id: "+emp_id+"  Username: "+name+"  Questionnaire Status: "+status);
			 
		}
		return reports;
		
	@PostMapping("/remind/{questionnaire_id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')") 
	public String reminder(@PathVariable Integer questionnaire_id) {

		try {
			Optional<Questionnaire> questionnaire = questionnaireRepository.findById(questionnaire_id);
			Questionnaire quest = questionnaire.get();
			List<String> list=repository.getEmployee(questionnaire_id);
			for (int i=0;i<list.size();i++) {
				Optional<Employee> employee = employeeRepository.findById(list.get(i));
				Employee emp=employee.get();
				

				String password = emp.generatePassword(8);
				emp.setPassword(encoder.encode(password));
				employeeRepository.save(emp);
				
				String subject = "Pending to accept Questionnaire " + quest.getQuestionnaireId();
				String text = quest.getMailBody() + "\nPassword: " + password + "\nUsername: " + emp.getUsername();
				notificationService.sendEmail(emp, subject, text);
			}

			return "Mail sent successfully";
		} catch (MailException mailException) {
			System.out.println(mailException);
		}
		return null;
	}

	@GetMapping("/displayQuestionnaire/{questionnaire_id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public Questionnaire display(@PathVariable Integer questionnaire_id) {
		Optional<Questionnaire> questionnaire = questionnaireRepository.findById(questionnaire_id);
		Questionnaire quest = questionnaire.get();	
		return quest;
	}
	
	@PutMapping("/accept/{questionnaire_id}/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('USER')")
	@ResponseStatus(HttpStatus.CREATED)
	public String agree(@PathVariable Integer questionnaire_id, @PathVariable String id) {
		Optional<Employee> employee = employeeRepository.findById(id);
		Employee emp = employee.get();
		
		Optional<Questionnaire> questionnaire = questionnaireRepository.findById(questionnaire_id);
		Questionnaire quest = questionnaire.get();

		service.setStatus(quest.getQuestionnaireId(), emp.getId(), 1);
		
		return "Thank you for accepting Terms and Conditions";

	}
	
	@GetMapping("/completedQuestionnaire/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('USER')")
	@ResponseStatus(HttpStatus.CREATED)
	public List<String> completedQuestionnaire(@PathVariable String id) {
		return service.getQuestionnaireStatus(id, 1);
	}
	
	@GetMapping("/pendingQuestionnaire/{id}")
	@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN') or hasRole('USER')")
	@ResponseStatus(HttpStatus.CREATED)
	public List<String> pendingQuestionnaire(@PathVariable String id) {
		return service.getQuestionnaireStatus(id, 0);
	}

}


	

}