package org.example.lld.chess;

import java.util.*;

/**
 * Chess game: Player - white/black, Board, Pieces r1: Players will have choices to move pieces r2:
 * Pieces should be moved to eligible places r3: Print positions of grid
 */
class ChessGame {
  public static void main(String[] args) {
    System.out.println("Chess game running");

    Board board = new Board(8, 8);
    board.print();

    board.getPieceAt(0, 1).makeMove(board.getPosition(1, 1));
    board.print();

    board.getPieceAt(1, 1).makeMove(board.getPosition(0, 1)); // Invalid move for Soldier
    board.print();
  }
}

enum ChessPieceType {
  SOLDIER,
  KNIGHT,
  BISHOP,
  QUEEN,
  KING,
  ROOK,
}

enum ChessColor {
  WHITE,
  BLACK,
}

class ChessPosition {
  final int row;
  final int column;

  public ChessPosition(int row, int column) {
    this.row = row;
    this.column = column;
  }

  @Override
  public String toString() {
    return "(" + row + ", " + column + ")";
  }
}

class RuleFacade {
  private final RuleEngine ruleEngine;

  public RuleFacade(ChessPiece chessPiece) {
    switch (chessPiece.type) {
      case KNIGHT:
        this.ruleEngine = new KnightRule();
        break;
      case ROOK:
        this.ruleEngine = new RookRule();
        break;
      case BISHOP:
        this.ruleEngine = new BishopRule();
        break;
      case SOLDIER:
        this.ruleEngine = new SoldierRule();
        break;
      default:
        throw new UnsupportedOperationException("Rule not defined for this piece");
    }
  }

  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition, Board board) {
    this.ruleEngine.validateMove(chessPiece, newPosition, board);
  }
}

interface RuleEngine {
  void validateMove(ChessPiece chessPiece, ChessPosition newPosition, Board board);
}

class KnightRule implements RuleEngine {
  @Override
  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition, Board board) {
    // Implement Knight-specific validation logic
  }
}

class RookRule implements RuleEngine {
  @Override
  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition, Board board) {
    // Implement Rook-specific validation logic
  }
}

class BishopRule implements RuleEngine {
  @Override
  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition, Board board) {
    // Implement Bishop-specific validation logic
  }
}

class SoldierRule implements RuleEngine {
  @Override
  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition, Board board) {
    if (chessPiece.color == ChessColor.WHITE && newPosition.row - chessPiece.position.row != 1
        || chessPiece.color == ChessColor.BLACK && newPosition.row - chessPiece.position.row != -1
        || newPosition.column != chessPiece.position.column) {
      throw new RuntimeException("Invalid move for Soldier!");
    }
  }
}

abstract class ChessPiece {
  ChessPieceType type;
  boolean isAlive;
  ChessColor color;
  ChessPosition position;

  public ChessPiece(ChessPieceType type, ChessColor color, ChessPosition position) {
    this.isAlive = true;
    this.type = type;
    this.color = color;
    this.position = position;
  }

  public void makeMove(ChessPosition newPosition) {
    RuleFacade ruleFacade = new RuleFacade(this);
    Board board = Board.getInstance(); // Singleton board access
    ruleFacade.validateMove(this, newPosition, board);

    ChessPiece targetPiece = board.getPieceAt(newPosition.row, newPosition.column);

    if (targetPiece != null && targetPiece.color == this.color) {
      throw new RuntimeException("Cannot move to a position occupied by your own piece!");
    }

    if (targetPiece != null) {
      capture(targetPiece);
    }

    // Move the piece
    board.updatePiecePosition(this, newPosition);
    this.position = newPosition;
  }

  private void capture(ChessPiece targetPiece) {
    targetPiece.isAlive = false;
    System.out.println("Captured " + targetPiece.type + " at position " + targetPiece.position);
  }

  @Override
  public String toString() {
    return this.type + " (" + this.color + ")";
  }
}

class Knight extends ChessPiece {
  Knight(ChessColor color, ChessPosition position) {
    super(ChessPieceType.KNIGHT, color, position);
  }
}

class Rook extends ChessPiece {
  Rook(ChessColor color, ChessPosition position) {
    super(ChessPieceType.ROOK, color, position);
  }
}

class Bishop extends ChessPiece {
  Bishop(ChessColor color, ChessPosition position) {
    super(ChessPieceType.BISHOP, color, position);
  }
}

class Soldier extends ChessPiece {
  Soldier(ChessColor color, ChessPosition position) {
    super(ChessPieceType.SOLDIER, color, position);
  }
}

class Board {
  private static Board instance;
  private int rowSize, colSize;
  private ChessPiece[][] boardGrid;

  public static Board getInstance() {
    if (instance == null) {
      instance = new Board(8, 8);
    }
    return instance;
  }

  public Board(int rowSize, int colSize) {
    this.rowSize = rowSize;
    this.colSize = colSize;
    this.boardGrid = new ChessPiece[rowSize][colSize];

    // Initialize the board with pieces
    initializePieces();
  }

  private void initializePieces() {
    boardGrid[0][1] = new Soldier(ChessColor.WHITE, new ChessPosition(0, 1));
    boardGrid[1][1] = new Soldier(ChessColor.BLACK, new ChessPosition(1, 1));
    // Initialize more pieces as needed
  }

  public ChessPosition getPosition(int row, int col) {
    return new ChessPosition(row, col);
  }

  public ChessPiece getPieceAt(int row, int col) {
    return boardGrid[row][col];
  }

  public void updatePiecePosition(ChessPiece piece, ChessPosition newPosition) {
    boardGrid[piece.position.row][piece.position.column] = null; // Clear old position
    boardGrid[newPosition.row][newPosition.column] = piece; // Update new position
  }

  public void print() {
    for (int i = 0; i < rowSize; i++) {
      for (int j = 0; j < colSize; j++) {
        if (boardGrid[i][j] != null) {
          System.out.print(boardGrid[i][j] + " ");
        } else {
          System.out.print("Empty ");
        }
      }
      System.out.println();
    }
    System.out.println();
  }
}

// package org.example.lld.chess;
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// **
// * Chess game: Player - white/black, Board, Pieces r1: Players will have choices to move pieces
// r2:
// * Pieces should be moved to eligible places r3: Print positions of grid
// */
/* class ChessGame {
//  public static void main(String[] args) {
//    System.out.println("Chess game running");
//
//    Board board = new Board(8, 8);
//    board.print();
//    board.pieces.get(0).makeMove(board.positions.get(1));
//    board.print();
//    board.pieces.get(1).makeMove(board.positions.get(0));
//    board.print();
//    board.pieces.get(0).makeMove(board.positions.get(68));
//    board.print();
//  }
//}
//
//enum ChessPieceType {
//  SOLDIER,
//  KNIGHT,
//  BISHOP,
//  QUEEN,
//  KING,
//  ROOK,
//  ;
//}
//
//
//enum ChessColor {
//  WHITE,
//  BLACK,
//}
//
//class ChessPosition {
//  Integer row;
//  Integer column;
//  ChessPiece chessPiece;
//
//  public ChessPosition(int row, int column) {
//    this.row = row;
//    this.column = column;
//  }
//}
//
//class RuleFacade {
//  RuleEngine ruleEngine;
//
//  public RuleFacade(ChessPiece chessPiece) {
//      if (chessPiece.equals(Knight.class)) {
//          this.ruleEngine = new KnightRule();
//      } else if (chessPiece.equals(Rook.class)) {
//          this.ruleEngine = new RookRule();
//      } else if (chessPiece.equals(Bishop.class)) {
//          this.ruleEngine = new BishopRule();
//      } else if (chessPiece.equals(Soldier.class)) {
//        this.ruleEngine = new SoldierRule();
//      }
//  }
//}
//
//interface RuleEngine {
//  void validateMove(ChessPiece chessPiece, ChessPosition newPosition);
//}
//
//class KnightRule implements RuleEngine {
//  @Override
//  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition) {}
//}
//
//class RookRule implements RuleEngine {
//  @Override
//  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition) {}
//}
//
//class BishopRule implements RuleEngine {
//  @Override
//  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition) {}
//}
//
//class SoldierRule implements RuleEngine {
//  @Override
//  public void validateMove(ChessPiece chessPiece, ChessPosition newPosition) {
//    if (newPosition.row - chessPiece.position.row != 1 || newPosition.column != chessPiece.position.column) {
//      throw new RuntimeException("Invalid move!");
//    }
//  }
//}
//
//
//abstract class ChessPiece {
//  ChessPieceType type;
//  Boolean isAlive;
//  ChessColor color;
//  ChessPosition position;
//  Integer defaultCount;
//
//  public ChessPiece(ChessPieceType type, ChessColor color, ChessPosition position, Integer defaultCount) {
//    this.isAlive = true;
//    this.color = color;
//    this.position = position;
//    this.defaultCount = defaultCount;
//  }
//
//  void kill(ChessPiece chessPiece) {
//    if (!this.isAlive) {
//      throw new IllegalStateException("dead piece cannot be moved");
//    }
//
//    if (this.color.equals(chessPiece.color)) {
//      throw new IllegalStateException("chess piece cannot be moved here");
//    }
//
//    chessPiece.isAlive = false;
//  }
//
//  void makeMove(ChessPosition newPosition) {
//    RuleFacade ruleFacade = new RuleFacade(this);
//    RuleEngine ruleEngine = ruleFacade.ruleEngine;
//    ruleEngine.validateMove(this, newPosition);
//
//    this.position = newPosition;
//    this.kill(newPosition.chessPiece);
//
//    this.position = newPosition;
//    newPosition.chessPiece = this;
//  }
//}
//
//class Knight extends ChessPiece {
//  Knight(ChessColor color, ChessPosition position) {
//    super(ChessPieceType.KNIGHT, color, position, 2);
//  }
//}
//
//class Rook extends ChessPiece {
//  Rook(ChessColor color, ChessPosition position) {
//    super(ChessPieceType.ROOK, color, position, 2);
//  }
//}
//
//class Bishop extends ChessPiece {
//  Bishop(ChessColor color, ChessPosition position) {
//    super(ChessPieceType.BISHOP, color, position, 2);
//  }
//}
//
//class Soldier extends ChessPiece {
//  Soldier(ChessColor color, ChessPosition position) {
//    super(ChessPieceType.SOLDIER, color, position, 8);
//  }
//}
//
//class Board {
//  Integer rowSize = 8;
//  Integer colSize = 8;
//  List<ChessPosition> positions = new ArrayList<>();
//  List<ChessPiece> pieces = new ArrayList<>();
////  Map<ChessPosition, ChessPiece> positions = new HashMap<>(); // used to print board
//
//  Board (Integer rowSize, Integer colSize) {
//    this.rowSize = rowSize;
//    this.colSize = colSize;
//
//    for (int i = 0; i < rowSize; i++) {
//      for (int j = 0; j < colSize; j++) {
//        positions.add(new ChessPosition(i, j));
//      }
//    }
//
//    ChessPiece blackSoldier = new Soldier(ChessColor.BLACK, positions.get(0));
//    ChessPiece whiteSoldier = new Soldier(ChessColor.WHITE, positions.get(1));
//    positions.get(0).chessPiece = blackSoldier;
//    positions.get(1).chessPiece = whiteSoldier;
//  }
//
//  public void print() {
//      for (ChessPosition position : positions) {
//          System.out.println(position.row + " " + position.column + " " + position.chessPiece.type);
//      }
//  }
//}

*/
