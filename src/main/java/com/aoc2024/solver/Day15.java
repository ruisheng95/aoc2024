package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.record.Node;
import com.aoc2024.utils.CommonUtils;

public class Day15 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day15.class);

  private static void swapMapVal(char[][] map, int x1, int y1, int x2, int y2) {
    char temp = map[y1][x1];
    map[y1][x1] = map[y2][x2];
    map[y2][x2] = temp;
  }

  private static int getGPSSum(char[][] map) {
    int sum = 0;
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[i].length; j++) {
        if (map[i][j] == 'O') {
          sum += 100 * i + j;
        }
      }
    }
    return sum;
  }

  private static int getGPSSum2(char[][] map) {
    int sum = 0;
    for (int i = 0; i < map.length; i++) {
      for (int j = 0; j < map[i].length; j++) {
        if (map[i][j] == '[') {
          sum += 100 * i + j;
          // LOGGER.debug("{} {}", i, j);
        }
      }
    }
    return sum;
  }

  private static Node moveRobot(char[][] map, char move, Node currentPos) {
    Node newPos = switch (move) {
      case '^' -> new Node(currentPos.x(), currentPos.y() - 1);
      case '>' -> new Node(currentPos.x() + 1, currentPos.y());
      case 'v' -> new Node(currentPos.x(), currentPos.y() + 1);
      case '<' -> new Node(currentPos.x() - 1, currentPos.y());
      default -> null;
    };
    if (newPos == null) {
      return currentPos;
    }
    if (newPos.isWithinGrid(map.length, map[0].length) && map[newPos.y()][newPos.x()] != '#') {
      if (map[newPos.y()][newPos.x()] == '.') {
        swapMapVal(map, newPos.x(), newPos.y(), currentPos.x(), currentPos.y());
        return newPos;
      } else {
        Node tempNewPos = moveRobot(map, move, newPos);
        if (tempNewPos != newPos) {
          swapMapVal(map, currentPos.x(), currentPos.y(), newPos.x(), newPos.y());
          return newPos;
        }
        return currentPos;
      }
    }
    return currentPos;
  }

  private static Node getDoubleBox(char[][] map, Node currentPos) {
    if (map[currentPos.y()][currentPos.x()] == '[') {
      return currentPos;
    } else if (map[currentPos.y()][currentPos.x()] == ']') {
      return new Node(currentPos.x() - 1, currentPos.y());
    }
    return null;
  }

  private static Node moveRobot2(char[][] map, char move, Node currentPos) {
    Node newPos = switch (move) {
      case '^' -> new Node(currentPos.x(), currentPos.y() - 1);
      case '>' -> new Node(currentPos.x() + 1, currentPos.y());
      case 'v' -> new Node(currentPos.x(), currentPos.y() + 1);
      case '<' -> new Node(currentPos.x() - 1, currentPos.y());
      default -> null;
    };
    if (newPos == null) {
      return currentPos;
    }
    if (newPos.isWithinGrid(map.length, map[0].length) && map[newPos.y()][newPos.x()] != '#') {
      if (map[newPos.y()][newPos.x()] == '.') {
        swapMapVal(map, newPos.x(), newPos.y(), currentPos.x(), currentPos.y());
        return newPos;
      } else {
        if (move == '<' || move == '>') {
          Node tempNewPos = moveRobot(map, move, newPos);
          if (tempNewPos != newPos) {
            swapMapVal(map, currentPos.x(), currentPos.y(), newPos.x(), newPos.y());
            return newPos;
          }
        } else {
          if (tryMoveDoubleBox(map, move, getDoubleBox(map, new Node(newPos.x(), newPos.y())))) {
            // LOGGER.debug("doublebox {} {}", newPos);
            swapMapVal(map, currentPos.x(), currentPos.y(), newPos.x(), newPos.y());
            return newPos;
          }
        }
        return currentPos;
      }
    }
    return currentPos;
  }

  private static void floodFill(char[][] map, char move, Node currentPos) {
    if (currentPos == null || !currentPos.isWithinGrid(map.length, map[0].length)
        || map[currentPos.y()][currentPos.x()] == '#' || map[currentPos.y()][currentPos.x()] == '.'
        || map[currentPos.y()][currentPos.x()] == '$') {
      return;
    }

    map[currentPos.y()][currentPos.x()] = '$';
    map[currentPos.y()][currentPos.x() + 1] = '$';
    if (move == '^') {
      floodFill(map, move, getDoubleBox(map, new Node(currentPos.x(), currentPos.y() - 1)));
      floodFill(map, move, getDoubleBox(map, new Node(currentPos.x() + 1, currentPos.y() - 1)));
    }
    // floodFill(map, move, new Node(currentPos.x() + 1, currentPos.y()));
    if (move == 'v') {
      floodFill(map, move, getDoubleBox(map, new Node(currentPos.x(), currentPos.y() + 1)));
      floodFill(map, move, getDoubleBox(map, new Node(currentPos.x() + 1, currentPos.y() + 1)));
    }
    // floodFill(map, move, new Node(currentPos.x() - 1, currentPos.y()));
  }

  private static boolean tryMoveDoubleBox(char[][] map, char move, Node currentPos) {
    char[][] tempMap = Arrays.stream(map).map(char[]::clone).toArray(char[][]::new);
    int dir = 1;
    if (move == '^') {
      dir = -1;
    }
    Node currentBox = getDoubleBox(map, currentPos);
    if (currentBox != null) {
      tempMap[currentBox.y()][currentBox.x()] = '$';
      tempMap[currentBox.y()][currentBox.x() + 1] = '$';
      floodFill(tempMap, move, getDoubleBox(map, new Node(currentPos.x(), currentPos.y() + dir)));
      floodFill(tempMap, move,
          getDoubleBox(map, new Node(currentPos.x() + 1, currentPos.y() + dir)));
      // CommonUtils.print2dChar(tempMap);
      boolean result = true;
      if (move == '^') {
        for (int i = 0; i < tempMap.length; i++) {
          if (ArrayUtils.contains(tempMap[i], '$')) {
            for (int j = 0; j < tempMap[i].length; j++) {
              if (tempMap[i][j] == '$') {
                result = result && (tempMap[i - 1][j] == '.' || tempMap[i - 1][j] == '$');
              }
            }
          }
        }
      } else if (move == 'v') {
        for (int i = tempMap.length - 1; i >= 0; i--) {
          if (ArrayUtils.contains(tempMap[i], '$')) {
            for (int j = 0; j < tempMap[i].length; j++) {
              if (tempMap[i][j] == '$') {
                result = result && (tempMap[i + 1][j] == '.' || tempMap[i + 1][j] == '$');
              }
            }
          }
        }
      }
      if (result) {
        if (move == '^') {
          for (int i = 0; i < tempMap.length; i++) {
            for (int j = 0; j < tempMap[i].length; j++) {
              if (tempMap[i][j] == '$') {
                swapMapVal(map, j, i, j, i - 1);
              }
            }
          }
        } else if (move == 'v') {
          for (int i = tempMap.length - 1; i >= 0; i--) {
            for (int j = 0; j < tempMap[i].length; j++) {
              if (tempMap[i][j] == '$') {
                swapMapVal(map, j, i, j, i + 1);
              }
            }
          }
        }
      }
      return result;
    }
    return false;
  }

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day15.txt");
    boolean isReadMoves = false;
    char[][] map = null;
    List<Character> moveList = new ArrayList<>();
    Node robotPos = null;
    for (int i = 0; i < strList.size(); i++) {
      String str = strList.get(i);
      if (str.length() == 0) {
        isReadMoves = true;
        continue;
      }
      if (!isReadMoves) {
        if (map == null) {
          map = new char[str.length()][str.length()];
        }
        for (int j = 0; j < str.length(); j++) {
          map[i][j] = str.charAt(j);
          if (map[i][j] == '@') {
            robotPos = new Node(j, i);
          }
        }
      } else {
        for (int j = 0; j < str.length(); j++) {
          moveList.add(str.charAt(j));
        }
      }
    }
    // CommonUtils.print2dChar(map);
    for (var move : moveList) {
      robotPos = moveRobot(map, move, robotPos);
      // LOGGER.debug("{} {} {}", move);
    }
    // CommonUtils.print2dChar(map);
    LOGGER.debug("part1 {}", getGPSSum(map));

    map = null;
    isReadMoves = false;
    moveList.clear();
    int abc = 0;
    for (int i = 0; i < strList.size(); i++) {
      String str = strList.get(i);
      if (str.length() == 0) {
        isReadMoves = true;
        continue;
      }
      if (!isReadMoves) {
        if (map == null) {
          map = new char[str.length()][str.length() * 2];
        }
        for (int j = 0; j < str.length(); j++) {
          char c = str.charAt(j);
          switch (c) {
            case '#' -> {
              map[i][j * 2] = '#';
              map[i][j * 2 + 1] = '#';
            }
            case 'O' -> {
              map[i][j * 2] = '[';
              map[i][j * 2 + 1] = ']';
              abc++;
            }
            case '.' -> {
              map[i][j * 2] = '.';
              map[i][j * 2 + 1] = '.';
            }
            case '@' -> {
              map[i][j * 2] = '@';
              map[i][j * 2 + 1] = '.';
              robotPos = new Node(j * 2, i);
            }
          }
        }
      } else {
        for (int j = 0; j < str.length(); j++) {
          moveList.add(str.charAt(j));
        }
      }
    }
    // CommonUtils.print2dChar(map);
    for (var move : moveList) {
      // LOGGER.debug("{}", move, robotPos);
      robotPos = moveRobot2(map, move, robotPos);
      // CommonUtils.print2dChar(map);
    }
    // CommonUtils.print2dChar(map);
    LOGGER.debug("part2 {} {}", abc, getGPSSum2(map));
  }
}
