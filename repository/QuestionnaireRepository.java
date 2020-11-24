package com.miniProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miniProject.models.Questionnaire;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Integer> {
	
}