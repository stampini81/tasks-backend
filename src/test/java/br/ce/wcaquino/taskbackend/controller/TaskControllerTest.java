package br.ce.wcaquino.taskbackend.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import br.ce.wcaquino.taskbackend.utils.ValidationException;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

    @InjectMocks
    private TaskController controller;

    @Mock
    private TaskRepo taskRepo;

    @Test
    public void deveListarTodasAsTarefas() {
        Task task1 = new Task();
        task1.setTask("Descricao 1");

        Task task2 = new Task();
        task2.setTask("Descricao 2");

        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepo.findAll()).thenReturn(tasks);

        List<Task> result = controller.findAll();

        assertEquals(2, result.size());
        assertSame(tasks, result);
        verify(taskRepo).findAll();
    }

    @Test
    public void naoDeveSalvarTarefaSemDescricao() {
        Task todo = new Task();
        todo.setDueDate(LocalDate.now());

        try {
            controller.save(todo);
            org.junit.Assert.fail("Nao deveria chegar nesse ponto!");
        } catch (ValidationException e) {
            assertEquals("Fill the task description", e.getMessage());
        }
    }

    @Test
    public void naoDeveSalvarTarefaSemData() {
        Task todo = new Task();
        todo.setTask("Descricao");

        try {
            controller.save(todo);
            org.junit.Assert.fail("Nao deveria chegar nesse ponto!");
        } catch (ValidationException e) {
            assertEquals("Fill the due date", e.getMessage());
        }
    }

    @Test
    public void naoDeveSalvarTarefaComDataPassada() {
        Task todo = new Task();
        todo.setTask("Descricao");
        todo.setDueDate(LocalDate.of(2010, 1, 1));

        try {
            controller.save(todo);
            org.junit.Assert.fail("Nao deveria chegar nesse ponto!");
        } catch (ValidationException e) {
            assertEquals("Due date must not be in past", e.getMessage());
        }
    }

    @Test
    public void deveSalvarTarefaComSucesso() throws ValidationException {
        Task todo = new Task();
        todo.setTask("Descricao");
        todo.setDueDate(LocalDate.now());

        when(taskRepo.save(todo)).thenReturn(todo);

        ResponseEntity<Task> response = controller.save(todo);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(todo, response.getBody());
        verify(taskRepo).save(todo);
    }

    @Test
    public void deveRemoverTarefaComSucesso() {
        Long taskId = 1L;
        doNothing().when(taskRepo).deleteById(taskId);

        ResponseEntity<Void> response = controller.delete(taskId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskRepo).deleteById(taskId);
    }
}
