package org.example.gol_spring;

import org.springframework.stereotype.Service;

@Service
public class GolSpringAppService {
    static final CoordRow ROWS = new CoordRow(25);
    static final CoordCol COLS = new CoordCol(35);

    public GolDTO initGameGliderGun() {
        GameOfLife game = new GameOfLife(GolDTO.gliderGun.rows, new Counter());
        return GolDTO.create(game.state(), game.getCounter());
    }

    public GolDTO initGameGliders() {
        GameOfLife game = new GameOfLife(ROWS, COLS);
        game.populateGlider(new CoordRow(5),new CoordCol(3));
        game.populateGlider(new CoordRow(10), new CoordCol(8));
        game.populateGlider(new CoordRow(12), new CoordCol(20));
        String state = game.state();
        return GolDTO.create(state, game.getCounter());
    }

    public GolDTO cycleGame(GolDTO state) {
        GameOfLife game = new GameOfLife(state.rows, new Counter(state.counter));
        game.cycle();
        return GolDTO.create(game.state(), game.getCounter());
    }
}
