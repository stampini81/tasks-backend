package br.ce.wcaquino.taskbackend.apitest;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.ce.wcaquino.taskbackend.controller.RootController;
import br.ce.wcaquino.taskbackend.controller.TaskController;
import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class APITest {

    @Mock
    private TaskRepo taskRepo;

    private TaskController taskController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        taskController = new TaskController();
        ReflectionTestUtils.setField(taskController, "todoRepo", taskRepo);
    }

    @Test
    public void deveRetornarHelloWorldNaRaiz() {
        RestAssuredMockMvc.standaloneSetup(new RootController());

        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .body(equalTo("Hello World!"));
    }

    @Test
    public void deveSalvarTarefaComSucessoViaApi() {
        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTask("Estudar Rest Assured");
        savedTask.setDueDate(LocalDate.now().plusDays(1));

        when(taskRepo.save(org.mockito.ArgumentMatchers.any(Task.class))).thenReturn(savedTask);

        RestAssuredMockMvc.mockMvc(
            MockMvcBuilders.standaloneSetup(taskController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build());

        Map<String, Object> todo = new HashMap<>();
        todo.put("task", "Estudar Rest Assured");
        todo.put("dueDate", LocalDate.now().plusDays(1).toString());

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(todo)
        .when()
            .post("/todo")
        .then()
            .statusCode(201)
            .body("task", equalTo("Estudar Rest Assured"));
    }

    @Test
    public void deveRetornarErroAoSalvarTarefaSemDescricaoViaApi() {
        RestAssuredMockMvc.mockMvc(
            MockMvcBuilders.standaloneSetup(taskController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build());

        Map<String, Object> todo = new HashMap<>();
        todo.put("dueDate", LocalDate.now().plusDays(1).toString());

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(todo)
        .when()
            .post("/todo")
        .then()
            .statusCode(400);
    }

    @Test
    public void deveListarTarefasSalvasViaApi() {
        Task firstTask = new Task();
        firstTask.setId(1L);
        firstTask.setTask("Primeira tarefa");
        firstTask.setDueDate(LocalDate.now().plusDays(1));

        Task secondTask = new Task();
        secondTask.setId(2L);
        secondTask.setTask("Segunda tarefa");
        secondTask.setDueDate(LocalDate.now().plusDays(2));

        when(taskRepo.findAll()).thenReturn(Arrays.asList(firstTask, secondTask));

        RestAssuredMockMvc.mockMvc(
            MockMvcBuilders.standaloneSetup(taskController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build());

        given()
        .when()
            .get("/todo")
        .then()
            .statusCode(200)
            .body("$", hasSize(2));
    }
}
