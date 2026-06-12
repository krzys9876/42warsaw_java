int COLS = 25;
int ROWS = 15;
boolean [][] board = new boolean[ROWS][COLS];
long cycle = 0;

void populateBoard(int row, int col) {
    board[row][col+1]=true;
    board[row+1][col+2]=true;
    board[row+2][col]=true;
    board[row+2][col+1]=true;
    board[row+2][col+2]=true;
}

void drawBoard() {
    IO.println("\033[H\033[2J");

    for(int r=0; r<ROWS; r++) {
	for(int c=0; c<COLS; c++) {
	    if(board[r][c]) {
		IO.print("X ");
	    } else {
		IO.print(". ");
	    }
	}
	IO.println();
    }
    IO.println(cycle);
}

boolean nextGeneration(int r, int c, int neighbours) {
    if(board[r][c]) {
        if(neighbours<2) return false; // rule 1
        else if(neighbours==2 || neighbours==3) return true; // rule 2
        else if(neighbours>3) return false; // rule 3
    } else {
        if(neighbours==3) return true; // rule 4
    }
    return false;
} 

void cycle() {
    int [][] neighbours = new int[ROWS][COLS];
    for(int r=0; r<ROWS; r++) {
        for(int c=0; c<COLS; c++) {
            for(int nr=-1; nr<=1; nr++) {
                for(int nc=-1; nc<=1; nc++) {
                    if(nr!=0 || nc!=0) {
                        if(board[(r+nr+ROWS) % ROWS][(c+nc+COLS) % COLS]) neighbours[r][c]++;
                    }
                }
            }
            IO.print(neighbours[r][c]);
            IO.print(" ");
        }
        IO.println();
    }

    for(int r=0; r<ROWS; r++) {
        for(int c=0; c<COLS; c++) {
            board[r][c]=nextGeneration(r,c,neighbours[r][c]);
        }
    }
}

void main() throws InterruptedException {
    populateBoard(5,2);
    populateBoard(8,14);
    while(true) {
	drawBoard();
        cycle();
	cycle++;
	Thread.sleep(250);
    }
}

