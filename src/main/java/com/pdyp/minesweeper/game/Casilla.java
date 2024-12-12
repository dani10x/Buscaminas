package com.pdyp.minesweeper.game;

import lombok.*;

@Builder
@Getter
@Setter
public class Casilla {

    private Boolean mina;
    private Integer minaCercana;
    private Boolean descubierto;
}
