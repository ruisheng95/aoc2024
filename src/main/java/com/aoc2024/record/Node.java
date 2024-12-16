package com.aoc2024.record;

public record Node(int x, int y) {

  public boolean isWithinGrid(int row, int col) {
    if (x >= 0 && x < col && y >= 0 && y < row) {
      return true;
    }
    return false;
  }
}
