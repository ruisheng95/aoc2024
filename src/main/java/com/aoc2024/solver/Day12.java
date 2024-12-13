package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.record.Node;
import com.aoc2024.utils.CommonUtils;

public class Day12 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day12.class);

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day12.txt");
    int row = strList.size();
    int col = strList.get(0).length();
    char[][] map = new char[row][col];
    Map<Character, Integer> regionCount = new HashMap<>();
    for (int i = 0; i < row; i++) {
      String str = strList.get(i);
      for (int j = 0; j < col; j++) {
        char c = str.charAt(j);
        map[i][j] = c;
        regionCount.merge(c, 1, (a, b) -> a + b);
      }
    }
    char[][] oriMap = Arrays.stream(map).map(char[]::clone).toArray(char[][]::new);

    AtomicLong counter = new AtomicLong();
    regionCount.keySet().stream().forEach(c -> {
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          if (map[i][j] == c) {
            List<Node> totalNodes = new ArrayList<>();
            Node currentNode = new Node(j, i);
            day12Solve(map, c, currentNode, totalNodes);
            counter.addAndGet(day12_getPriceFromTotalNodesPart1(oriMap, totalNodes, c));
          }
        }
      }
    });
    LOGGER.debug("part1 {}", counter.longValue());

    counter.set(0);
    char[][] map2 = Arrays.stream(oriMap).map(char[]::clone).toArray(char[][]::new);
    regionCount.keySet().stream().forEach(c -> {
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          if (map2[i][j] == c) {
            List<Node> totalNodes = new ArrayList<>();
            Node currentNode = new Node(j, i);
            day12Solve(map2, c, currentNode, totalNodes);
            counter.addAndGet(day12_getPriceFromTotalNodesPart2(oriMap, totalNodes, c));
          }
        }
      }
    });
    LOGGER.debug("part2 {}", counter.longValue());
  }


  private static void day12Solve(char[][] map, char c, Node pos, List<Node> totalNodes) {
    if (pos.x() < 0 || pos.x() == map[0].length || pos.y() < 0 || pos.y() == map.length
        || map[pos.y()][pos.x()] != c) {
      return;
    }
    totalNodes.add(pos);
    map[pos.y()][pos.x()] = '#';
    day12Solve(map, c, new Node(pos.x(), pos.y() - 1), totalNodes);
    day12Solve(map, c, new Node(pos.x() + 1, pos.y()), totalNodes);
    day12Solve(map, c, new Node(pos.x(), pos.y() + 1), totalNodes);
    day12Solve(map, c, new Node(pos.x() - 1, pos.y()), totalNodes);
  }

  private static long day12_getPriceFromTotalNodesPart1(char[][] map, List<Node> totalNodes,
      char c) {
    if (totalNodes.size() == 1) {
      return 4;
    } else {
      int row = map.length;
      int col = map[0].length;
      int adjacentNode = 0;
      for (Node node : totalNodes) {
        if (node.y() - 1 >= 0 && map[node.y() - 1][node.x()] == c) {
          adjacentNode++;
        }
        if (node.x() + 1 < col && map[node.y()][node.x() + 1] == c) {
          adjacentNode++;
        }
        if (node.y() + 1 < row && map[node.y() + 1][node.x()] == c) {
          adjacentNode++;
        }
        if (node.x() - 1 >= 0 && map[node.y()][node.x() - 1] == c) {
          adjacentNode++;
        }
      }
      long price = totalNodes.size() * ((totalNodes.size() * 4) - adjacentNode);
      // LOGGER.debug("cal2 {} {}", c, price);
      return price;
    }
  }

  private static boolean getSidesFromNode(char[][] map, char c, Node pos, int dir) {
    int row = map.length;
    int col = map[0].length;
    // 0: NE, 1: ES, 2: SW, 3: WN
    char c1 = '#';
    char c2 = '#';
    char c3 = '#';
    if (dir == 0) {
      if (pos.y() - 1 >= 0) {
        c1 = map[pos.y() - 1][pos.x()];
      }
      if (pos.x() + 1 < col) {
        c2 = map[pos.y()][pos.x() + 1];
      }
      if (pos.y() - 1 >= 0 && pos.x() + 1 < col) {
        c3 = map[pos.y() - 1][pos.x() + 1];
      }
      if (c1 != c && c2 != c) {
        return true;
      } else if (c1 == c && c3 != c && c2 == c) {
        return true;
      }
    } else if (dir == 1) {
      if (pos.y() + 1 < row) {
        c1 = map[pos.y() + 1][pos.x()];
      }
      if (pos.x() + 1 < col) {
        c2 = map[pos.y()][pos.x() + 1];
      }
      if (pos.y() + 1 < row && pos.x() + 1 < col) {
        c3 = map[pos.y() + 1][pos.x() + 1];
      }
      if (c1 != c && c2 != c) {
        return true;
      } else if (c1 == c && c3 != c && c2 == c) {
        return true;
      }
    } else if (dir == 2) {
      if (pos.y() + 1 < row) {
        c1 = map[pos.y() + 1][pos.x()];
      }
      if (pos.x() - 1 >= 0) {
        c2 = map[pos.y()][pos.x() - 1];
      }
      if (pos.y() + 1 < row && pos.x() - 1 >= 0) {
        c3 = map[pos.y() + 1][pos.x() - 1];
      }
      if (c1 != c && c2 != c) {
        return true;
      } else if (c1 == c && c3 != c && c2 == c) {
        return true;
      }
    } else if (dir == 3) {
      if (pos.y() - 1 >= 0) {
        c1 = map[pos.y() - 1][pos.x()];
      }
      if (pos.x() - 1 >= 0) {
        c2 = map[pos.y()][pos.x() - 1];
      }
      if (pos.y() - 1 >= 0 && pos.x() - 1 >= 0) {
        c3 = map[pos.y() - 1][pos.x() - 1];
      }
      if (c1 != c && c2 != c) {
        return true;
      } else if (c1 == c && c3 != c && c2 == c) {
        return true;
      }
    }
    return false;
  }

  private static long day12_getPriceFromTotalNodesPart2(char[][] map, List<Node> totalNodes,
      char c) {
    if (totalNodes.size() == 1) {
      return 4;
    } else {
      long corner = 0;
      for (Node node : totalNodes) {
        for (int i = 0; i < 4; i++) {
          if (getSidesFromNode(map, c, node, i)) {
            corner++;
          }
        }
      }
      // LOGGER.debug("cal2 {} {} {}", c, totalNodes.size(), corner);
      return totalNodes.size() * corner;
    }
  }

}
