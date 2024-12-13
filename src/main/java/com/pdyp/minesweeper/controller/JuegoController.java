package com.pdyp.minesweeper.controller;

import com.pdyp.minesweeper.game.Casilla;
import com.pdyp.minesweeper.message.request.NuevoJuego;
import com.pdyp.minesweeper.message.response.MensajeResponse;
import com.pdyp.minesweeper.service.BuscaminasService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class JuegoController {

    private final BuscaminasService buscaminasService;

    @MessageMapping("/nuevo")
    @SendToUser("/queue/buscaminas")
    public MensajeResponse crearJuego(@Payload NuevoJuego nuevoJuego, SimpMessageHeaderAccessor headerAccessor) {
        String idUser = (String) headerAccessor.getSessionAttributes().get("sessionId");
        return buscaminasService.crearJuego(idUser, nuevoJuego);
    }

    @MessageMapping("/get")
    @SendToUser("/queue/buscaminas")
    public List<List<Casilla>> getJuego(SimpMessageHeaderAccessor headerAccessor) {
        String idUser = (String) headerAccessor.getSessionAttributes().get("sessionId");
        return buscaminasService.getJuego(idUser);
    }
}
