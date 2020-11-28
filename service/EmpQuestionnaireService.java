package com.miniProject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miniProject.models.EmpQuestionnaire;
import com.miniProject.models.Employee;
import com.miniProject.models.Questionnaire;
import com.miniProject.repository.EmpQuestionnaireRepo;
import com.miniProject.repository.EmployeeRepository;
import com.miniProject.repository.QuestionnaireRepository;

@Service
public class EmpQuestionnaireService {

	@Autowired
	EmpQuestionnaireRepo repository;
	
	@Autowired
	QuestionnaireRepository questionnaireRepository;
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	private EntityManager em;

	public void setData(int questId, String empId, int status) {
		EmpQuestionnaire emp_ques = new EmpQuestionnaire(questId, empId, status);
		repository.save(emp_ques);
	}

	public void setStatus(int questId, String empId, int status) {

		Query q = em.createNativeQuery("select map_id from MiniProject.emp_questionnaire where quest_id =?1 and emp_id=?2");
		
		q.setParameter(1, questId);
		q.setParameter(2, empId);
		int map_id = (int) q.getSingleResult();
		
		EmpQuestionnaire emp_ques = repository.getOne(map_id);
		emp_ques.setStatus(status);
		repository.save(emp_ques);
	}
	
	public List<String> getQuestionnaireStatus(String empId, int status) {

		Query q = em.createNativeQuery("select quest_id from MiniProject.emp_questionnaire where emp_id=?1 and status_value=?2");
		q.setParameter(1, empId);
		q.setParameter(2, status);
		List<String> questionnaires = new ArrayList<String>();
		List<Integer> results = q.getResultList();

		for(int i=0;i<results.size();i++)
		{
			int id=results.get(i);
			Questionnaire quest= questionnaireRepository.getOne(id);
			String actions = "Questionnaire "+quest.getQuestionnaireId()+ "  Tiltle: "+quest.getTitle();
			questionnaires.add(actions);
		}
		return questionnaires;	
	}

	public List<EmpQuestionnaire> findByQuestionnaireId(int questId) {
		return repository.findByQuestionnaireId(questId);
	}
}
