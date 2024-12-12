package com.pdyp.minesweeper.controller;

import com.pdyp.minesweeper.game.Casilla;
import com.pdyp.minesweeper.message.request.NuevoJuego;
import com.pdyp.minesweeper.message.response.MensajeResponse;
import com.pdyp.minesweeper.service.BuscaminasService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class JuegoController {

    private final BuscaminasService buscaminasService;

    @MessageMapping("/nuevo")
    @SendTo("/topic/buscaminas")
    public MensajeResponse crearJuego(NuevoJuego nuevoJuego) {
        return buscaminasService.crearJuego(nuevoJuego);
    }

    @MessageMapping("/get")
    @SendTo("/topic/buscaminas")
    public List<List<Casilla>> getJuego() {
        return buscaminasService.getJuego();
    }
}
