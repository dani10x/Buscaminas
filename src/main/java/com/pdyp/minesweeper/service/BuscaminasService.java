package com.pdyp.minesweeper.service;

import com.pdyp.minesweeper.game.Casilla;
import com.pdyp.minesweeper.message.request.NuevoJuego;
import com.pdyp.minesweeper.message.response.CasillaResponse;
import com.pdyp.minesweeper.message.response.MensajeResponse;

import java.util.List;

public interface BuscaminasService {

    MensajeResponse crearJuego(String idJugador, NuevoJuego nuevoJuego);

    List<List<Casilla>> getJuego(String idJugador);

    List<CasillaResponse> revelarCasillas(int x, int y, String idJugador);

    MensajeResponse reiniciarJuego(String idJugString);

}
