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

}