package com.pdyp.minesweeper.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Juego {

    private List<List<Casilla>> tablero;
    private int filas;
    private int columnas;
    private int casillasRevelar;

    public void casillaRevelada() {
        casillasRevelar--;
    }

    public boolean finDelJuego() {
        return casillasRevelar == 0;
    }
}
