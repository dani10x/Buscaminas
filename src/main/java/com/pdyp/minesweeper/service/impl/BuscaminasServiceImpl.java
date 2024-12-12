package com.pdyp.minesweeper.service.impl;

import com.pdyp.minesweeper.game.Casilla;
import com.pdyp.minesweeper.message.request.NuevoJuego;
import com.pdyp.minesweeper.message.response.MensajeResponse;
import com.pdyp.minesweeper.service.BuscaminasService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BuscaminasServiceImpl implements BuscaminasService {

    private List<List<Casilla>> tablero;

    @Override
    public MensajeResponse crearJuego(NuevoJuego nuevoJuego) {
        try {
            validarInicioJuego(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), nuevoJuego.getMinas());
        } catch (Exception e) {
            return MensajeResponse.builder().error(true).respuesta(e.getMessage()).build();
        }
        tablero = new ArrayList<>();
        iniciarTablero(nuevoJuego.getFilas(), nuevoJuego.getColumnas());
        plantarMinas(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), nuevoJuego.getMinas());
        calcularMinasCercanas(nuevoJuego.getFilas(), nuevoJuego.getColumnas());
        return MensajeResponse.builder().respuesta("Juego creado").error(false).build();
    }

    @Override
    public List<List<Casilla>> getJuego() {
        if(tablero == null || tablero.isEmpty()) {
            return List.of();
        }
        return tablero;
    }

    private void validarInicioJuego(int filas, int columnas, int minas) {
        if(columnas < 1 || filas < 1 || minas < 1) {
            throw new IllegalArgumentException("No se puede iniciar el juego con los valores enviados");
        }
        if(filas * columnas <= minas) {
            throw new IllegalArgumentException("El número de minas no puede ser mayor o igual a la cantidad de casillas");
        }
    }

    private void iniciarTablero(int filas, int columnas) {
        for (int i = 0; i < filas; i++) {
            tablero.add(new ArrayList<>());
            for (int j = 0; j < columnas; j++) {
                tablero.get(i).add(Casilla.builder().descubierto(false).minaCercana(0).mina(false).build());
            }
        }
    }

    private void plantarMinas(int filas, int columnas, int minas) {
        Random random = new Random();
        int minasPlantadas = 0;
        while (minasPlantadas < minas) {
            int fila = random.nextInt(filas);
            int col = random.nextInt(columnas);

            if(!tablero.get(fila).get(col).getMina()) {
                tablero.get(fila).get(col).setMina(true);
                minasPlantadas++;
            }
        }
    }

    private void calcularMinasCercanas(int filas, int columnas) {
        //Posiciones de las celdas vecinas
        int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (tablero.get(i).get(j).getMina()) {
                    continue;
                }

                int minasCount = 0;
                // Verificar todas las celdas vecinas
                for (int k = 0; k < 8; k++) {
                    int vecinoi = i + dx[k];
                    int vecinoj = j + dy[k];

                    if (vecinoi >= 0 && vecinoi < filas && vecinoj >= 0 && vecinoj < columnas && tablero.get(vecinoi).get(vecinoj).getMina()) {
                        minasCount++;
                    }
                }
                tablero.get(i).get(j).setMinaCercana(minasCount);
            }
        }
    }
}
