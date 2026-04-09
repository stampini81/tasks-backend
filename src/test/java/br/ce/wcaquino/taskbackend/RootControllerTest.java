package br.ce.wcaquino.taskbackend.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RootControllerTest {

    @Test
    public void deveRetornarHelloWorld() {
        RootController controller = new RootController();

        assertEquals("Hello World!", controller.hello());
    }
}
