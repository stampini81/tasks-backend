package br.ce.wcaquino.taskbackend.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class TaskTest {

    @Test
    public void deveManterDadosDaTarefa() {
        Task task = new Task();
        LocalDate dueDate = LocalDate.of(2030, 1, 1);

        task.setId(1L);
        task.setTask("Descricao");
        task.setDueDate(dueDate);

        assertEquals(Long.valueOf(1L), task.getId());
        assertEquals("Descricao", task.getTask());
        assertEquals(dueDate, task.getDueDate());
    }
}
