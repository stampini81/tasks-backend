package br.ce.wcaquino.taskbackend.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Task {

	private Long id;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false)
	private LocalDate dueDate;
	
	public Task() {
		// JPA requires a no-args constructor to instantiate the entity.
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTask() {
		return description;
	}

	public void setTask(String task) {
		this.description = task;
	}
	
	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
}
