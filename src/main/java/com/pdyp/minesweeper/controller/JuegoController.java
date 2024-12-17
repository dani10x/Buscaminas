package com.pdyp.minesweeper.controller;

import com.pdyp.minesweeper.game.Casilla;
import com.pdyp.minesweeper.message.request.Coordenadas;
import com.pdyp.minesweeper.message.request.NuevoJuego;
import com.pdyp.minesweeper.message.response.CasillaResponse;
import com.pdyp.minesweeper.message.response.MensajeResponse;
import com.pdyp.minesweeper.message.response.SessionResponse;
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

    @MessageMapping("/iniciar")
    @SendToUser("/queue/buscaminas")
    public SessionResponse crearSession(SimpMessageHeaderAccessor headerAccessor) {
        String idUser = (String) headerAccessor.getSessionAttributes().get("idUsuario");
        return new SessionResponse(idUser);
    }

    @MessageMapping("/nuevo")
    @SendToUser("/queue/buscaminas")
    public MensajeResponse crearJuego(@Payload NuevoJuego nuevoJuego, SimpMessageHeaderAccessor headerAccessor) {
        String idUser = (String) headerAccessor.getSessionAttributes().get("idUsuario");
        return buscaminasService.crearJuego(idUser, nuevoJuego);
    }

    @MessageMapping("/reiniciar")
    @SendToUser("/queue/buscaminas")
    public MensajeResponse reiniciar(SimpMessageHeaderAccessor headerAccessor) {
        String idUser = (String) headerAccessor.getSessionAttributes().get("idUsuario");
        return  buscaminasService.reiniciarJuego(idUser);
    }

    @MessageMapping("/revelar")
    @SendToUser("/queue/buscaminas")
    public List<CasillaResponse> revelarCasilla(@Payload Coordenadas coordenadas, SimpMessageHeaderAccessor headerAccessor) {
        String idUser = (String) headerAccessor.getSessionAttributes().get("idUsuario");
        return buscaminasService.revelarCasillas(coordenadas.getX(), coordenadas.getY(), idUser);
    }

    @MessageMapping("/get")
    @SendToUser("/queue/buscaminas")
    public List<List<Casilla>> getJuego(SimpMessageHeaderAccessor headerAccessor) {
        String idUser = (String) headerAccessor.getSessionAttributes().get("idUsuario");
        return buscaminasService.getJuego(idUser);
    }
}
