package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day10 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day10.class);

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day10.txt");
    int row = strList.size();
    int col = strList.get(0).length();
    int[][] map = new int[row][col];
    for (int i = 0; i < row; i++) {
      String str = strList.get(i);
      for (int j = 0; j < col; j++) {
        map[i][j] = Character.getNumericValue(str.charAt(j));
      }
    }
    AtomicInteger count = new AtomicInteger();
    List<TrailHead> solvedList = new ArrayList<>();
    LOGGER.debug("start");
    IntStream.rangeClosed(0, row * col - 1).boxed().forEach(index -> {
      int posY = index / row;
      int posX = index % col;
      int val = map[posY][posX];
      int result = 0;
      if (val == 0) {
        // 1: up, 2: right, 3: down, 4: left
        result += day10Solve(map, posX, posY - 1, val, 1, posX, posY, solvedList, true);
        result += day10Solve(map, posX + 1, posY, val, 1, posX, posY, solvedList, true);
        result += day10Solve(map, posX, posY + 1, val, 1, posX, posY, solvedList, true);
        result += day10Solve(map, posX - 1, posY, val, 1, posX, posY, solvedList, true);
        count.addAndGet(result);
      }
    });
    LOGGER.debug("part1 {}", count.get());
    solvedList.clear();
    count.set(0);
    IntStream.rangeClosed(0, row * col - 1).boxed().forEach(index -> {
      int posY = index / row;
      int posX = index % col;
      int val = map[posY][posX];
      int result = 0;
      if (val == 0) {
        // 1: up, 2: right, 3: down, 4: left
        result += day10Solve(map, posX, posY - 1, val, 1, posX, posY, solvedList, false);
        result += day10Solve(map, posX + 1, posY, val, 1, posX, posY, solvedList, false);
        result += day10Solve(map, posX, posY + 1, val, 1, posX, posY, solvedList, false);
        result += day10Solve(map, posX - 1, posY, val, 1, posX, posY, solvedList, false);
        count.addAndGet(result);
      }
    });
    LOGGER.debug("part2 {}", count.get());
  }

  private static record TrailHead(int startX, int startY, int endX, int endY) {
  }
  
  private static int day10Solve(int[][] map, int posX, int posY, int prevVal, int validCount,
      int startX, int startY, List<TrailHead> solvedList, boolean isPart1) {
    int row = map.length;
    int col = map[0].length;
    int result = 0;
    if (posY >= 0 && posY < row && posX >= 0 && posX < col) {
      int val = map[posY][posX];
      if (val - 1 == prevVal) {
        validCount++;
        if (validCount == 10) {
          // Directly return 1 for part 2
          if (!isPart1) {
            return 1;
          }

          boolean solved = solvedList.stream().noneMatch(trailHead -> {
            return trailHead.startX == startX && trailHead.startY == startY
                && trailHead.endX == posX && trailHead.endY == posY;
          });
          if (solved) {
            solvedList.add(new TrailHead(startX, startY, posX, posY));
            return 1;
          }
          return 0;
        }
        result +=
            day10Solve(map, posX, posY - 1, val, validCount, startX, startY, solvedList, isPart1);
        result +=
            day10Solve(map, posX + 1, posY, val, validCount, startX, startY, solvedList, isPart1);
        result +=
            day10Solve(map, posX, posY + 1, val, validCount, startX, startY, solvedList, isPart1);
        result +=
            day10Solve(map, posX - 1, posY, val, validCount, startX, startY, solvedList, isPart1);
        return result;
      }
    }
    return 0;
  }

}
