import java.util.Arrays;

public final class SudokuSequential {

    private static final int SUBGRID_SIZE = 3;
    private static final int GRID_SIZE = 9;

    private SudokuSequential() {
    }

    private static char[][] makeSampleBoard() {
        return new char[][] {
                { '5', '3', '.', '.', '7', '.', '.', '.', '.' },
                { '6', '.', '.', '1', '9', '5', '.', '.', '.' },
                { '.', '9', '8', '.', '.', '.', '.', '6', '.' },
                { '8', '.', '.', '.', '6', '.', '.', '.', '3' },
                { '4', '.', '.', '8', '.', '3', '.', '.', '1' },
                { '7', '.', '.', '.', '2', '.', '.', '.', '6' },
                { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
                { '.', '.', '.', '4', '1', '9', '.', '.', '5' },
                { '.', '.', '.', '.', '8', '.', '.', '7', '9' }
        };
    }

    private static void printBoard(char[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            if (row % SUBGRID_SIZE == 0 && row != 0) {
                System.out.println("------+-------+------");
            }
            for (int col = 0; col < GRID_SIZE; col++) {
                if (col % SUBGRID_SIZE == 0 && col != 0) {
                    System.out.print(" |");
                }
                System.out.print(' ');
                System.out.print(board[row][col]);
            }
            System.out.println();
        }
    }

    private static final class SudokuSolver {
        private final int[][] rows = new int[GRID_SIZE][GRID_SIZE + 1];
        private final int[][] columns = new int[GRID_SIZE][GRID_SIZE + 1];
        private final int[][] boxes = new int[GRID_SIZE][GRID_SIZE + 1];
        private char[][] board;
        private boolean solved;

        boolean solve(char[][] board) {
            resetState();
            this.board = board;

            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (board[row][col] != '.') {
                        placeNumber(board[row][col] - '0', row, col);
                    }
                }
            }

            backtrack(0, 0);
            return solved;
        }

        private void resetState() {
            for (int row = 0; row < GRID_SIZE; row++) {
                Arrays.fill(rows[row], 0);
                Arrays.fill(columns[row], 0);
                Arrays.fill(boxes[row], 0);
            }
            board = null;
            solved = false;
        }

        private boolean couldPlace(int value, int row, int col) {
            int boxIndex = (row / SUBGRID_SIZE) * SUBGRID_SIZE + col / SUBGRID_SIZE;
            return rows[row][value] == 0 && columns[col][value] == 0 && boxes[boxIndex][value] == 0;
        }

        private void placeNumber(int value, int row, int col) {
            int boxIndex = (row / SUBGRID_SIZE) * SUBGRID_SIZE + col / SUBGRID_SIZE;
            rows[row][value]++;
            columns[col][value]++;
            boxes[boxIndex][value]++;
            board[row][col] = (char) ('0' + value);
        }

        private void removeNumber(int value, int row, int col) {
            int boxIndex = (row / SUBGRID_SIZE) * SUBGRID_SIZE + col / SUBGRID_SIZE;
            rows[row][value]--;
            columns[col][value]--;
            boxes[boxIndex][value]--;
            board[row][col] = '.';
        }

        private void placeNextNumbers(int row, int col) {
            if (row == GRID_SIZE - 1 && col == GRID_SIZE - 1) {
                solved = true;
                return;
            }

            if (col == GRID_SIZE - 1) {
                backtrack(row + 1, 0);
            } else {
                backtrack(row, col + 1);
            }
        }

        private void backtrack(int row, int col) {
            if (solved) {
                return;
            }

            if (board[row][col] == '.') {
                for (int value = 1; value <= GRID_SIZE; value++) {
                    if (couldPlace(value, row, col)) {
                        placeNumber(value, row, col);
                        placeNextNumbers(row, col);
                        if (!solved) {
                            removeNumber(value, row, col);
                        }
                    }
                }
            } else {
                placeNextNumbers(row, col);
            }
        }
    }

    public static void main(String[] args) {
        char[][] board = makeSampleBoard();
        SudokuSolver solver = new SudokuSolver();
        if (solver.solve(board)) {
            printBoard(board);
        } else {
            System.out.println("No solution found.");
        }
    }
}