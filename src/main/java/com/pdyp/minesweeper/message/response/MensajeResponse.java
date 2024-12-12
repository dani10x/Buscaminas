package com.pdyp.minesweeper.message.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MensajeResponse {

    private String respuesta;
    private Boolean error;
}
