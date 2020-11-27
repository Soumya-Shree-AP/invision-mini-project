package com.miniProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miniProject.models.EmpQuestionnaire;
import com.miniProject.repository.EmpQuestionnaireRepo;

@Service
public class EmpQuestionnaireService {
	
	@Autowired 
	EmpQuestionnaireRepo repository;
	
	public void getData(int questId, String empId, int status) {
		EmpQuestionnaire emp_ques= new EmpQuestionnaire(questId,empId,status);
		repository.save(emp_ques);
	}

	public List<EmpQuestionnaire> findByQuestionnaireId(int questId) {
		return repository.findByQuestionnaireId(questId);
	}


}
