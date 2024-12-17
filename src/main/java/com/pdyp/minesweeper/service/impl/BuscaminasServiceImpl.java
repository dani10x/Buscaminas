package com.pdyp.minesweeper.service.impl;

import com.pdyp.minesweeper.game.Casilla;
import com.pdyp.minesweeper.game.Juego;
import com.pdyp.minesweeper.message.request.NuevoJuego;
import com.pdyp.minesweeper.message.response.CasillaResponse;
import com.pdyp.minesweeper.message.response.MensajeResponse;
import com.pdyp.minesweeper.service.BuscaminasService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BuscaminasServiceImpl implements BuscaminasService {

    private final Map<String, Juego> juegos = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public MensajeResponse crearJuego(String idJugador, NuevoJuego nuevoJuego) {
        try {
            validarInicioJuego(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), nuevoJuego.getMinas());
        } catch (Exception e) {
            return MensajeResponse.builder().error(true).respuesta(e.getMessage()).build();
        }
        juegos.put(idJugador, nuevoJuego(nuevoJuego, idJugador));
        return MensajeResponse.builder().respuesta("Juego creado").error(false).build();
    }

    @Override
    public List<List<Casilla>> getJuego(String idJugador) {
        Juego juego = juegos.get(idJugador);
        if(juego.finDelJuego()) {
            return List.of();
        }
        juego.setCasillasRevelar(0);
        List<List<Casilla>> tablero = juego.getTablero();
        if(tablero == null || tablero.isEmpty()) {
            return List.of();
        }
        return tablero;
    }

    @Override
    public List<CasillaResponse> revelarCasillas(int x, int y, String idJugador) {
        Juego juego = buscarJuego(idJugador);
        if(juego.finDelJuego()) {
            enviarMensaje(idJugador, "El juego ya no se encuentra disponible");
            return null;
        }
        if(validarCordenadaTablero(x, y, juego) || juego.getTablero().get(x).get(y).getDescubierto()) {
            enviarMensaje(idJugador, "Posición inválida");
            return null;
        }
        if(juego.getTablero().get(x).get(y).getMina()) {
            enviarMensaje(idJugador, "Haz perdido");
            juego.setCasillasRevelar(0);
            return List.of(new CasillaResponse(0, x, y, true));
        }
        else {
            return calcularCasillas(x, y, buscarJuego(idJugador), idJugador);
        }
    }

    @Override
    public MensajeResponse reiniciarJuego(String idJugString) {
        juegos.remove(idJugString);
        return MensajeResponse.builder().error(false).respuesta("Juego reiniciado").build();
    }

    private List<CasillaResponse> calcularCasillas(int x, int y, Juego juego, String idJugador) {
        List<CasillaResponse> casillas = new ArrayList<>();
        revelarCasilla(x, y, juego, casillas);
        if(juego.finDelJuego()) {
            enviarMensaje(idJugador, "¡Felicidades, haz ganado!");
        }
        return casillas;
    }

    private void revelarCasilla(int x, int y, Juego juego, List<CasillaResponse> casillas) {
        if (validarCordenadaTablero(x, y, juego)) {
            return;
        }

        Casilla casilla = juego.getTablero().get(x).get(y);

        if (casilla.getDescubierto()) {
            return;
        }

        casilla.setDescubierto(true);
        CasillaResponse response = new CasillaResponse(casilla.getMinaCercana(), x, y, false);
        casillas.add(response);
        juego.casillaRevelada();

        if (casilla.getMinaCercana() > 0) {
            return;
        }

        int[] dx = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] dy = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i = 0; i < dx.length; i++) {
            revelarCasilla(x + dx[i], y + dy[i], juego, casillas);
        }
    }

    private boolean validarCordenadaTablero(int x, int y, Juego juego) {
        return x < 0 || x >= juego.getFilas() || y < 0 || y >= juego.getColumnas();
    }

    private Juego buscarJuego(String idJugador) {
        return juegos.getOrDefault(idJugador, new Juego(new ArrayList<>(), 0, 0, 0));
    }

    private Juego nuevoJuego(NuevoJuego nuevoJuego, String session) {
        List<List<Casilla>> tablero = new ArrayList<>();
        enviarMensaje(session, "Iniciando tablero");
        iniciarTablero(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), tablero);
        enviarMensaje(session, "Plantando minas");
        plantarMinas(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), nuevoJuego.getMinas(), tablero);
        enviarMensaje(session, "Preparando juego");
        calcularMinasCercanas(nuevoJuego.getFilas(), nuevoJuego.getColumnas(), tablero);
        return new Juego(tablero, nuevoJuego.getFilas(), nuevoJuego.getColumnas(),
                (nuevoJuego.getFilas()*nuevoJuego.getColumnas()-nuevoJuego.getMinas()));
    }

    private void enviarMensaje(String idJugador, String mensaje) {
        String destino = "/topic/" + idJugador + "/queue/notificaciones";
        simpMessagingTemplate.convertAndSend(destino, MensajeResponse.builder().error(false).respuesta(mensaje).build());
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
