package com.pdyp.minesweeper.service.impl;

import com.pdyp.minesweeper.game.Casilla;
import com.pdyp.minesweeper.game.Juego;
import com.pdyp.minesweeper.message.request.NuevoJuego;
import com.pdyp.minesweeper.message.response.MensajeResponse;
import com.pdyp.minesweeper.service.BuscaminasService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BuscaminasServiceImpl implements BuscaminasService {

    private final Map<String, Juego> juegos = new ConcurrentHashMap<>();

    @Override
    public MensajeResponse crearJuego(String idJugador, NuevoJuego nuevoJuego) {
        try {
            validarInicioJuego(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), nuevoJuego.getMinas());
        } catch (Exception e) {
            return MensajeResponse.builder().error(true).respuesta(e.getMessage()).build();
        }
        juegos.put(idJugador, nuevoJuego(nuevoJuego));
        return MensajeResponse.builder().respuesta("Juego creado").error(false).build();
    }

    @Override
    public List<List<Casilla>> getJuego(String idJugador) {
        List<List<Casilla>> tablero = buscarJuego(idJugador);
        if(tablero == null || tablero.isEmpty()) {
            return List.of();
        }
        return tablero;
    }

    private List<List<Casilla>> buscarJuego(String idJugador) {
        return juegos.getOrDefault(idJugador, new Juego(new ArrayList<>())).getTablero();
    }

    private Juego nuevoJuego(NuevoJuego nuevoJuego) {
        List<List<Casilla>> tablero = new ArrayList<>();
        iniciarTablero(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), tablero);
        plantarMinas(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), nuevoJuego.getMinas(), tablero);
        calcularMinasCercanas(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), tablero);
        return new Juego(tablero);
    }

    private void validarInicioJuego(int filas, int columnas, int minas) {
        if(columnas < 1 || filas < 1 || minas < 1) {
            throw new IllegalArgumentException("No se puede iniciar el juego con los valores enviados");
        }
        if(filas * columnas <= minas) {
            throw new IllegalArgumentException("El número de minas no puede ser mayor o igual a la cantidad de casillas");
        }
    }

    private void iniciarTablero(int filas, int columnas, List<List<Casilla>> tablero) {
        for (int i = 0; i < filas; i++) {
            tablero.add(new ArrayList<>());
            for (int j = 0; j < columnas; j++) {
                tablero.get(i).add(Casilla.builder().descubierto(false).minaCercana(0).mina(false).build());
            }
        }
    }

    private void plantarMinas(int filas, int columnas, int minas, List<List<Casilla>> tablero) {
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

    private void calcularMinasCercanas(int filas, int columnas, List<List<Casilla>> tablero) {
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
