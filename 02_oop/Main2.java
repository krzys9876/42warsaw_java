import java.util.ArrayList;
import java.util.stream.IntStream;

public class Main2 {
    void main() {
        GameOfLife game = new GameOfLife(new Coord<Row>(15),new Coord<Col>(25));
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

    GameOfLife(Coord<Row> rows, Coord<Col> cols) {
        board = new Board(rows, cols);
        counter = new Counter();
    }

    void run() throws InterruptedException{
        populateBoard(new Coord<Row>(5),new Coord<Col>(2));
        populateBoard(new Coord<Row>(8),new Coord<Col>(14));
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
    
    void populateBoard(Coord<Row> row, Coord<Col> col) {
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
    private Coord<Row> row;
    private Coord<Col> col;

    Cell(Coord<Row> row, Coord<Col> col) {
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

    Coord<Row> row() { return row; }
    Coord<Col> col() { return col; }
}

class Neighbours {
    private int value = 0;
    Neighbours() {}
    void inc() { value++; }
    void reset() { value = 0; }
    int getValue() { return value; }
}

//NOTE: these are empty types, used only to tag coords, not to bring any functionality
interface Row {}
interface Col {}

class Coord<T> {
    private int value = 0;
    Coord() {}
    Coord(int value) { this.value = value; }
   
    void inc() { value++; }
    int get() { return value; }
    Coord<T> plus(int addValue) { return new Coord<>(value + addValue); }
    Coord<T> plus(Coord addValue) { return plus(addValue.get()); }
    Coord<T> mod(int modValue) { return new Coord<>((value + modValue) % modValue); } // prevent negative modulo value
    Coord<T> mod(Coord modValue) { return mod(modValue.get()); }
}

class Coords {
    public final Coord<Row> row;
    public final Coord<Col> col;
    Coords(Coord<Row> r, Coord<Col> c) {
        row=r;
        col=c;
    }
}

class Board {
    public Coord<Row> ROWS;
    public Coord<Col> COLS;
    public ArrayList<Cell> board;
    private ArrayList<Coords> neighbourhood;
    
    Board(Coord<Row> rows, Coord<Col> cols) {
        ROWS = rows;
        COLS = cols;
        board = new ArrayList<>();
        IntStream.range(0, ROWS.get() * COLS.get()).forEach( i -> board.add(new Cell(new Coord<Row>(i / COLS.get()), new Coord<Col>(i % COLS.get()))));
        neighbourhood = new ArrayList<>();
        for(int nr=-1; nr<=1; nr++) 
            for(int nc=-1; nc<=1; nc++) 
                if(!(nr==0 && nc==0)) neighbourhood.add(new Coords(new Coord<Row>(nr),new Coord<Col>(nc)));
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

    public Cell getAt(Coord<Row> r, Coord<Col> c) {
        return getAt(r.get(), c.get());
    }
}
