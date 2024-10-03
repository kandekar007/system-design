package org.example.lld.chess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chess game: Player - white/black, Board, Pieces r1: Players will have choices to move pieces r2:
 * Pieces should be moved to eligible places r3: Print positions of grid
 */
class ChessGame {
  public static void main(String[] args) {
    System.out.println("Chess game running");
  }

  enum ChessPieceType {
    SOLDIER,
    KNIGHT,
    BISHOP,
    QUEEN,
    KING,
    ROOK,
    ;
  }

  enum ChessColor {
    WHITE,
    BLACK,
  }

  enum ChessBoardRow {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H;
  }

  enum ChessBoardColumn {
    Z1,
    Z2,
    Z3,
    Z4,
    Z5,
    Z6,
    Z7,
    Z8;
  }

  class ChessPosition {
    ChessBoardRow row;
    ChessBoardColumn column;
    ChessColor color;

    public ChessPosition(ChessBoardRow row, ChessBoardColumn column, ChessColor color) {
      this.row = row;
      this.column = column;
      this.color = color;
    }
  }

  class ChessPiece {
    ChessPieceType type;
    Boolean isAlive;
    ChessColor color;
    ChessPosition position;

    public ChessPiece(ChessPieceType type, ChessColor color, ChessPosition position) {
      this.type = type;
      this.isAlive = true;
      this.color = color;
      this.position = position;
    }
  }

  class Board {
    Integer rowSize = 8;
    Integer colSize = 8;
    Map<List<Integer>, ChessPiece> positions = new HashMap<>();
  }
}
