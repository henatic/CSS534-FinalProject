#include <array>
#include <iostream>
#include <vector>

using namespace std;

const int kSubgridSize = 3;
const int kGridSize = 9;

using Board = vector<vector<char>>;

// Builds a sample Sudoku board to demonstrate solver usage.
Board MakeSampleBoard()
{
    return {{'5', '3', '.', '.', '7', '.', '.', '.', '.'},
            {'6', '.', '.', '1', '9', '5', '.', '.', '.'},
            {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
            {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
            {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
            {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
            {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
            {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
            {'.', '.', '.', '.', '8', '.', '.', '7', '9'}};
}

void PrintBoard(const Board &board)
{
    for (int row = 0; row < kGridSize; ++row)
    {
        if (row % kSubgridSize == 0 && row != 0)
            cout << "------+-------+------\n";

        for (int col = 0; col < kGridSize; ++col)
        {
            if (col % kSubgridSize == 0 && col != 0)
                cout << " |";

            cout << ' ' << board[row][col];
        }
        cout << '\n';
    }
}

class SudokuSolver
{
public:
    bool Solve(Board &board)
    {
        ResetState();

        board_ = &board;
        for (int row = 0; row < kGridSize; ++row)
        {
            for (int col = 0; col < kGridSize; ++col)
            {
                if (board[row][col] != '.')
                {
                    const int value = board[row][col] - '0';
                    PlaceNumber(value, row, col);
                }
            }
        }

        Backtrack(0, 0);
        return solved_;
    }

private:
    void ResetState()
    {
        for (auto &row : rows_)
            row.fill(0);
        for (auto &col : cols_)
            col.fill(0);
        for (auto &box : boxes_)
            box.fill(0);

        board_ = nullptr;
        solved_ = false;
    }

    bool CouldPlace(int value, int row, int col) const
    {
        const int boxIndex = (row / kSubgridSize) * kSubgridSize + col / kSubgridSize;
        return rows_[row][value] == 0 && cols_[col][value] == 0 && boxes_[boxIndex][value] == 0;
    }

    void PlaceNumber(int value, int row, int col)
    {
        const int boxIndex = (row / kSubgridSize) * kSubgridSize + col / kSubgridSize;
        ++rows_[row][value];
        ++cols_[col][value];
        ++boxes_[boxIndex][value];
        (*board_)[row][col] = static_cast<char>('0' + value);
    }

    void RemoveNumber(int value, int row, int col)
    {
        const int boxIndex = (row / kSubgridSize) * kSubgridSize + col / kSubgridSize;
        --rows_[row][value];
        --cols_[col][value];
        --boxes_[boxIndex][value];
        (*board_)[row][col] = '.';
    }

    void PlaceNextNumbers(int row, int col)
    {
        if (row == kGridSize - 1 && col == kGridSize - 1)
        {
            solved_ = true;
            return;
        }

        if (col == kGridSize - 1)
            Backtrack(row + 1, 0);
        else
            Backtrack(row, col + 1);
    }

    void Backtrack(int row, int col)
    {
        if (solved_)
            return;

        if ((*board_)[row][col] == '.')
        {
            for (int value = 1; value <= kGridSize; ++value)
            {
                if (CouldPlace(value, row, col))
                {
                    PlaceNumber(value, row, col);
                    PlaceNextNumbers(row, col);
                    if (!solved_)
                        RemoveNumber(value, row, col);
                }
            }
        }
        else
        {
            PlaceNextNumbers(row, col);
        }
    }

    array<array<int, kGridSize + 1>, kGridSize> rows_{};
    array<array<int, kGridSize + 1>, kGridSize> cols_{};
    array<array<int, kGridSize + 1>, kGridSize> boxes_{};
    Board *board_ = nullptr;
    bool solved_ = false;
};

int main()
{
    Board board = MakeSampleBoard();

    SudokuSolver solver;
    if (solver.Solve(board))
        PrintBoard(board);
    else
        cout << "No solution found.\n";

    return 0;
}
