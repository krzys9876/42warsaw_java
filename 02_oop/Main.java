import java.util.ArrayList;
import java.util.stream.IntStream;

public class Main {
    void main() {
        GameOfLife game = new GameOfLife(new CoordRow(15),new CoordCol(25));
        try {
            game.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

class GameOfLife {
    private Counter counter;
    private Board board;

    GameOfLife(CoordRow rows, CoordCol cols) {
        board = new Board(rows, cols);
        counter = new Counter();
    }

    void run() throws InterruptedException{
        populateBoard(new CoordRow(5),new CoordCol(2));
        populateBoard(new CoordRow(8),new CoordCol(14));
        while(true) {
	        drawBoard(counter);
            board.cycle();
	        counter.inc();
            Thread.sleep(250);
        }
    }

    void drawBoard(Counter cntr) {
        IO.println("\033[H\033[2J");
        IO.println(board.getText());
	IO.println(cntr.toString());
    }

    void populateBoard(CoordRow row, CoordCol col) {
        board.getAt(row,col.plus(1)).set();
        board.getAt(row.plus(1),col.plus(2)).set();
        board.getAt(row.plus(2),col).set();
        board.getAt(row.plus(2),col.plus(1)).set();
        board.getAt(row.plus(2),col.plus(2)).set();
    }
}

class Counter {
    private int value;
    Counter() { value = 0; }
    public String toString() { return String.valueOf(value); }
    void reset() { value = 0; }
    void inc() { value++; }
}

class Cell {
    private boolean value = false;
    private Neighbours neighbours = new Neighbours();
    private CoordRow row;
    private CoordCol col;

    Cell(CoordRow row, CoordCol col) {
        this.row = row;
        this.col = col;
    }
    void set() { value = true; } 
    void reset() { value = false; }
    boolean isSet() { return value; }
    public String toString() { if(value) return "X "; else return ". "; }

    void addNeighbour() { neighbours.inc(); }

    void nextGeneration() {
	if(isSet()) {
	    if(neighbours.getValue()<2) reset(); // rule 1
	    else if(neighbours.getValue()==2 || neighbours.getValue()==3) set(); // rule 2
	    else if(neighbours.getValue()>3) reset(); // rule 3
	} else {
	    if(neighbours.getValue()==3) set(); // rule 4
	}
        neighbours.reset();
    }

    Neighbours getNeighbours() { return neighbours; }

    CoordRow row() { return row; }
    CoordCol col() { return col; }
}

class Neighbours {
    private int value = 0;
    Neighbours() {}
    void inc() { value++; }
    void reset() { value = 0; }
    int getValue() { return value; }
}

class Coord {
    private int value = 0;
    Coord() {}
    Coord(int value) { this.value = value; }
   
    void inc() { value++; }
    int get() { return value; }
    Coord plus(int addValue) { return new Coord(value + addValue); }
    Coord plus(Coord addValue) { return plus(addValue.get()); }
    Coord mod(int modValue) { return new Coord((value + modValue) % modValue); } // prevent negative modulo value
    Coord mod(Coord modValue) { return mod(modValue.get()); }
}

class CoordRow extends Coord {
    CoordRow() { super(); }
    CoordRow(int value) { super(value); }
    private CoordRow(Coord coord) { this(coord.get()); }
    CoordRow plus(int addValue) { return new CoordRow(super.plus(addValue)); }
    CoordRow plus(Coord addValue) { return new CoordRow(super.plus(addValue)); }
    CoordRow mod(Coord modValue) { return new CoordRow(super.mod(modValue)); }
}

class CoordCol extends Coord {
    CoordCol() { super(); }
    CoordCol(int value) { super(value); }
    private CoordCol(Coord coord) { this(coord.get()); }
    CoordCol plus(int addValue) { return new CoordCol(super.plus(addValue)); }
    CoordCol plus(Coord addValue) { return new CoordCol(super.plus(addValue)); }
    CoordCol mod(Coord modValue) { return new CoordCol(super.mod(modValue)); }
}

class Coords {
    public final CoordRow row;
    public final CoordCol col;
    Coords(CoordRow r, CoordCol c) {
        row=r;
        col=c;
    }
}

class Board {
    public CoordRow ROWS;
    public CoordCol COLS;
    public ArrayList<Cell> board;
    private ArrayList<Coords> neighbourhood;
    
    Board(CoordRow rows, CoordCol cols) {
        ROWS = rows;
        COLS = cols;
        board = new ArrayList<>();
        IntStream.range(0, ROWS.get() * COLS.get()).forEach( i -> board.add(new Cell(new CoordRow(i / COLS.get()), new CoordCol(i % COLS.get()))));
        neighbourhood = new ArrayList<>();
        for(int nr=-1; nr<=1; nr++) 
            for(int nc=-1; nc<=1; nc++) 
                if(!(nr==0 && nc==0)) neighbourhood.add(new Coords(new CoordRow(nr),new CoordCol(nc)));
    }

    String getText() {
        return board.stream().reduce(new String(), (t, cell) -> t + cell.toString() + ((cell.col().get()==COLS.get()-1) ? "\n" : ""), String::concat);
    }

    void cycle() {
        board.forEach(cell -> {
            neighbourhood.forEach(neighbour -> { 
                if(getAt(cell.row().plus(neighbour.row).mod(ROWS),cell.col().plus(neighbour.col).mod(COLS)).isSet()) cell.addNeighbour();
            });
        });
	board.stream().forEach(Cell::nextGeneration);
    }

    private Cell getAt(int r, int c) {
        return board.get(r*COLS.get()+c);
    }

    public Cell getAt(CoordRow r, CoordCol c) {
        return getAt(r.get(), c.get());
    }
}
