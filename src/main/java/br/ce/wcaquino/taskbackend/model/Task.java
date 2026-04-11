package br.ce.wcaquino.taskbackend.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Task {

	private Long id;
	
	private String description;
	
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

	@Column(name = "description", nullable = false)
	public String getTask() {
		return description;
	}

	public void setTask(String task) {
		this.description = task;
	}
	
	@Column(nullable = false)
	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
}
