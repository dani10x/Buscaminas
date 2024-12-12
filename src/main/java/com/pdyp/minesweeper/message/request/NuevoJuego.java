package com.pdyp.minesweeper.message.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NuevoJuego {

    private Integer filas;
    private Integer columnas;
    private Integer minas;
}
