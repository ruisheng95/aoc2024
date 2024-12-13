package com.aoc2024.solver;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day6 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day6.class);

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day6.txt");
    int row = strList.size();
    int col = strList.get(0).length();
    {
      char[][] map = new char[strList.size()][strList.get(0).length()];
      for (int i = 0; i < row; i++) {
        String str = strList.get(i);
        for (int j = 0; j < col; j++) {
          map[i][j] = str.charAt(j);
        }
      }
      int posX = 0;
      int posY = 0;
      int dir = 0; // 0: up, 1: right, 2: down, 3: left
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          if (map[i][j] == '^') {
            posY = i;
            posX = j;
          }
        }
      }
      LOGGER.debug("start {} {} {} {}", posY, posX, row, col);

      while (true) {
        if (dir == 0) {
          if (posY == 0) {
            break;
          }
          if (map[posY - 1][posX] == '#') {
            dir = 1;
            // LOGGER.debug("{} {} {} {}", posY, posX, map[posY][posX], turnCount);
          } else {
            map[posY][posX] = 'X';
            map[posY - 1][posX] = '^';
            posY--;
          }
        } else if (dir == 1) {
          if (posX == col - 1) {
            break;
          }
          if (map[posY][posX + 1] == '#') {
            dir = 2;
          } else {
            map[posY][posX] = 'X';
            map[posY][posX + 1] = '>';
            posX++;
          }
        } else if (dir == 2) {
          if (posY == row - 1) {
            break;
          }
          if (map[posY + 1][posX] == '#') {
            dir = 3;
          } else {
            map[posY][posX] = 'X';
            map[posY + 1][posX] = 'V';
            posY++;
          }
        } else if (dir == 3) {
          if (posX == 0) {
            break;
          }
          if (map[posY][posX - 1] == '#') {
            dir = 0;
          } else {
            map[posY][posX] = 'X';
            map[posY][posX - 1] = '<';
            posX--;
          }
        }
      }

      int count = 0;
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          if (map[i][j] == 'X') {
            count++;
          }
        }
      }
      LOGGER.debug("part1 {}", count + 1);
    }

    AtomicInteger count = new AtomicInteger();
    IntStream.rangeClosed(0, row * col - 1).boxed().parallel().forEach(index -> {
      int tryY = index / row;
      int tryX = index % col;

      char[][] map = new char[strList.size()][strList.get(0).length()];
      for (int i = 0; i < row; i++) {
        String str = strList.get(i);
        for (int j = 0; j < col; j++) {
          map[i][j] = str.charAt(j);
        }
      }
      int posX = 0;
      int posY = 0;
      int dir = 0; // 0: up, 1: right, 2: down, 3: left
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          if (map[i][j] == '^') {
            posY = i;
            posX = j;
          }
        }
      }

      if (map[tryY][tryX] != '^') {
        map[tryY][tryX] = '#';
      }
      // LOGGER.debug("start {} {} {} {}", posY, posX, row, col);

      int iteration = 0;
      final int LIMIT = 6000;
      while (true && iteration < LIMIT) {
        if (dir == 0) {
          if (posY == 0) {
            break;
          }
          if (map[posY - 1][posX] == '#') {
            dir = 1;
            // LOGGER.debug("{} {} {} {}", posY, posX, map[posY][posX], turnCount);
          } else {
            map[posY][posX] = 'X';
            map[posY - 1][posX] = '^';
            posY--;
          }
        } else if (dir == 1) {
          if (posX == col - 1) {
            break;
          }
          if (map[posY][posX + 1] == '#') {
            dir = 2;
          } else {
            map[posY][posX] = 'X';
            map[posY][posX + 1] = '>';
            posX++;
          }
        } else if (dir == 2) {
          if (posY == row - 1) {
            break;
          }
          if (map[posY + 1][posX] == '#') {
            dir = 3;
          } else {
            map[posY][posX] = 'X';
            map[posY + 1][posX] = 'V';
            posY++;
          }
        } else if (dir == 3) {
          if (posX == 0) {
            break;
          }
          if (map[posY][posX - 1] == '#') {
            dir = 0;
          } else {
            map[posY][posX] = 'X';
            map[posY][posX - 1] = '<';
            posX--;
          }
        }
        iteration++;
      }

      if (iteration == LIMIT) {
        // LOGGER.debug("@@ {} {}", tryY, tryX);
        count.incrementAndGet();
      }
    });
    LOGGER.debug("part2 {}", count.get());
  }
}
