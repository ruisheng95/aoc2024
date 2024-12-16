package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.record.Node;
import com.aoc2024.utils.CommonUtils;

public class Day14 implements AocPuzzle {
  private static final Logger LOGGER = LoggerFactory.getLogger(Day14.class);

  private static class Robot {
    private Node pos;
    private Node velocity;
    private static final int GRID_WIDTH = 101;
    private static final int GRID_HEIGHT = 103;

    public Robot(int x1, int y1, int x2, int y2) {
      pos = new Node(x1, y1);
      velocity = new Node(x2, y2);
    }

    public Node getPos() {
      return pos;
    }

    public void move() {
      int newX = pos.x() + velocity.x();
      int newY = pos.y() + velocity.y();
      if (newX < 0) {
        newX += GRID_WIDTH;
      } else if (newX >= GRID_WIDTH) {
        newX -= GRID_WIDTH;
      }
      if (newY < 0) {
        newY += GRID_HEIGHT;
      } else if (newY >= GRID_HEIGHT) {
        newY -= GRID_HEIGHT;
      }
      pos = new Node(newX, newY);
    }

    public static String[][] get2dMapFromRobotList(List<Robot> robotList) {
      String[][] result = new String[GRID_HEIGHT][GRID_WIDTH];
      for (var robot : robotList) {
        String currentStr = result[robot.getPos().y()][robot.getPos().x()];
        if (NumberUtils.isDigits(currentStr)) {
          int newVal = NumberUtils.toInt(currentStr) + 1;
          result[robot.getPos().y()][robot.getPos().x()] = Integer.toString(newVal);
        } else {
          result[robot.getPos().y()][robot.getPos().x()] = "1";
        }
      }
      for (int i = 0; i < result.length; i++) {
        for (int j = 0; j < result[0].length; j++) {
          if (StringUtils.isBlank(result[i][j])) {
            result[i][j] = ".";
          }
        }
      }
      return result;
    }

    public static int getSafetyFactor(String[][] map) {

      int quadrant1 = 0;
      for (int i = 0; i < GRID_HEIGHT / 2; i++) {
        for (int j = 0; j < GRID_WIDTH / 2; j++) {
          if (NumberUtils.isDigits(map[i][j])) {
            quadrant1 += NumberUtils.toInt(map[i][j]);
          }
        }
      }

      int quadrant2 = 0;
      for (int i = 0; i < GRID_HEIGHT / 2; i++) {
        for (int j = GRID_WIDTH / 2 + 1; j < GRID_WIDTH; j++) {
          if (NumberUtils.isDigits(map[i][j])) {
            quadrant2 += NumberUtils.toInt(map[i][j]);
          }
        }
      }

      int quadrant3 = 0;
      for (int i = GRID_HEIGHT / 2 + 1; i < GRID_HEIGHT; i++) {
        for (int j = 0; j < GRID_WIDTH / 2; j++) {
          if (NumberUtils.isDigits(map[i][j])) {
            quadrant3 += NumberUtils.toInt(map[i][j]);
          }
        }
      }

      int quadrant4 = 0;
      for (int i = GRID_HEIGHT / 2 + 1; i < GRID_HEIGHT; i++) {
        for (int j = GRID_WIDTH / 2 + 1; j < GRID_WIDTH; j++) {
          if (NumberUtils.isDigits(map[i][j])) {
            quadrant4 += NumberUtils.toInt(map[i][j]);
          }
        }
      }

      return quadrant1 * quadrant2 * quadrant3 * quadrant4;
    }

    private static int floodFill(String[][] map, int x, int y) {
      if (x == -1 || x == GRID_WIDTH || y < 0 || y == GRID_HEIGHT || NumberUtils.isDigits(map[y][x])
          || "@".equals(map[y][x])) {
        return 0;
      }
      map[y][x] = "@";
      int count = 1;
      count += floodFill(map, x, y - 1);
      count += floodFill(map, x + 1, y);
      count += floodFill(map, x, y + 1);
      count += floodFill(map, x - 1, y);
      return count;
    }

    public static int floodFillCount(String[][] map) {
      return floodFill(map, GRID_WIDTH / 2 - 10, GRID_HEIGHT / 2 - 10);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Robot [pos=").append(pos).append(", velocity=").append(velocity).append("]");
      return builder.toString();
    }
  }

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day14.txt");
    List<Robot> robotList = new ArrayList<>();

    for (var str : strList) {
      String[] posArr = StringUtils.substringAfter(str.split(" ")[0], "=").split(",");
      String[] velocityArr = StringUtils.substringAfter(str.split(" ")[1], "=").split(",");
      Robot robot = new Robot(NumberUtils.toInt(posArr[0]), NumberUtils.toInt(posArr[1]),
          NumberUtils.toInt(velocityArr[0]), NumberUtils.toInt(velocityArr[1]));
      robotList.add(robot);
    }

    for (int i = 0; i < 100; i++) {
      for (var robot : robotList) {
        robot.move();
      }
    }

    String[][] map = Robot.get2dMapFromRobotList(robotList);
    // CommonUtils.print2dString(map);

    LOGGER.debug("part1 {}", Robot.getSafetyFactor(map));

    robotList.clear();
    for (var str : strList) {
      String[] posArr = StringUtils.substringAfter(str.split(" ")[0], "=").split(",");
      String[] velocityArr = StringUtils.substringAfter(str.split(" ")[1], "=").split(",");
      Robot robot = new Robot(NumberUtils.toInt(posArr[0]), NumberUtils.toInt(posArr[1]),
          NumberUtils.toInt(velocityArr[0]), NumberUtils.toInt(velocityArr[1]));
      robotList.add(robot);
    }
    List<Integer> countList = new ArrayList<>();
    for (int i = 0; i < 10000; i++) {
      for (var robot : robotList) {
        robot.move();
      }
      String[][] map1 = Robot.get2dMapFromRobotList(robotList);
      int count = Robot.floodFillCount(map1);
      if (count != 0) {
        if (count == 670) {
          LOGGER.debug("part2 christmas tree! {}", i + 1);
          break;
          // CommonUtils.print2dString(Robot.get2dMapFromRobotList(robotList));
        }
        countList.add(count);
      }
      // CommonUtils.print2dString(map1);
    }
    // Collections.sort(countList);

    // LOGGER.debug("part2 {}", countList.subList(0, 10));
  }

}
