package com.miniProject.models;

import javax.persistence.*;

@Entity
@Table(name = "questionnaire")		
public class Questionnaire {
	@Id
	@Column(name = "questionnaire_id", unique = true, nullable = false)
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int questionnaireId;

	private String title;

	private String description;

	private String buttonTitle;

	private String buttonText;
	
	private String checkBoxText;

	private String startDate;

	private String endDate;
	
	private int remindDays;
	

	private String pptUpload;


	private String participantList;

	private String mailBody;
	
	

	public Questionnaire() {

	}

	public int getQuestionnaireId() {
		return questionnaireId;
	}

	public void setQuestionnaireId(int questionnaire_id) {
		this.questionnaireId = questionnaire_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getButtonTitle() {
		return buttonTitle;
	}

	public void setButtonTitle(String buttonTitle) {
		this.buttonTitle = buttonTitle;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public String getCheckBoxText() {
		return checkBoxText;
	}

	public void setCheckBoxText(String checkBoxText) {
		this.checkBoxText = checkBoxText;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getRemindDays() {
		return remindDays;
	}

	public void setRemindDays(int reminderDays) {
		this.remindDays = reminderDays;
	}

	public String getPptUpload() {
		return pptUpload;
	}

	public void setPptUpload(String pptUpload) {
		this.pptUpload = pptUpload;
	}

	public String getParticipantList() {
		return participantList;
	}

	public void setParticipantList(String participantList) {
		this.participantList = participantList;
	}

	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}
	

}