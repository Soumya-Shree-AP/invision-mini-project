package com.miniProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miniProject.models.EmpQuestionnaire;

@Repository
public interface EmpQuestionnaireRepo extends JpaRepository<EmpQuestionnaire, Integer> {

}
