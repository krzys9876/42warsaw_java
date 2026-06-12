final int ROWS = 15;
final int COLS = 25;
int counter = 0;

boolean [][] board = new boolean[ROWS][COLS];
int [][] neighbours = new int[ROWS][COLS];

void drawBoard() {
    IO.println("\033[H\033[2J");
    for(int r=0; r<ROWS; r++) {
        for(int c=0; c<COLS; c++)
            if(board[r][c]) IO.print("X ");
            else IO. print(". ");
        IO.println();
    }
    IO.println(counter);
}

void makeGlider(int r, int c) {
    board[r][c+1]=true;
    board[r+1][c+2]=true;
    board[r+2][c]=true;
    board[r+2][c+1]=true;
    board[r+2][c+2]=true;
}

void delay(int ms) {
    try {
        Thread.sleep(ms);
    } catch(InterruptedException e) {
    }
}

void nextGeneration() {
    neighbours=new int[ROWS][COLS];

    for(int r=0;r<ROWS;r++)
        for(int c=0;c<COLS;c++)
            for(int rn=-1;rn<=1;rn++)
                for(int cn=-1;cn<=1;cn++)
                    if(board[(r+rn+ROWS) % ROWS][(c+cn+COLS) % COLS] && !(rn==0 && cn==0)) neighbours[r][c]++;

    for(int r=0;r<ROWS;r++) {
        for(int c=0;c<COLS;c++) {
            //IO.print(String.valueOf(neighbours[r][c])+" ");
            switch(neighbours[r][c]) {
                case 2: break;
                case 3: board[r][c]=true; break;
                default: board[r][c]=false; break;
            }
        }
        //IO.println();
    }     
}

void main() {
    makeGlider(5,4);
    makeGlider(1,15);
    while(true) {
        drawBoard();
        nextGeneration();
        delay(250);
        counter++;
    }
}
