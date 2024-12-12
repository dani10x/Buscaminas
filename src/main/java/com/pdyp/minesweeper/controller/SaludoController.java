package com.pdyp.minesweeper.controller;

import com.pdyp.minesweeper.message.request.Saludo;
import com.pdyp.minesweeper.message.response.SaludoDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SaludoController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public SaludoDTO greeting(Saludo saludo) throws Exception {
        Thread.sleep(1000);
        return new SaludoDTO("Hola, " + saludo.getNombre() + "!");
    }
}
