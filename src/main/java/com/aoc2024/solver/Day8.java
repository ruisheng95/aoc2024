package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.record.Node;
import com.aoc2024.utils.CommonUtils;

public class Day8 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day8.class);

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day8.txt");
    Map<Character, Integer> antennaCountMap = new HashMap<>();
    Map<Character, List<Node>> antennaNodeMap = new HashMap<>();
    int row = strList.size();
    int col = strList.get(0).length();
    char[][] antennaMap = new char[row][col];
    char[][] antinodeMap = new char[row][col];

    for (int i = 0; i < row; i++) {
      String str = strList.get(i);
      for (int j = 0; j < col; j++) {
        char c = str.charAt(j);
        if (c != '.') {
          antennaCountMap.merge(c, 1, (a, b) -> a + b);
          antennaNodeMap.computeIfAbsent(c, k -> new ArrayList<>()).add(new Node(j, i));
        }
        antennaMap[i][j] = c;
        antinodeMap[i][j] = '.';
      }
    }

    for (Map.Entry<Character, List<Node>> entry : antennaNodeMap.entrySet()) {
      List<Node> nodeList = entry.getValue();
      for (int i = 0; i < nodeList.size(); i++) {
        Node node1 = nodeList.get(i);
        for (int j = i + 1; j < nodeList.size(); j++) {
          Node node2 = nodeList.get(j);
          Node leftNode = getNextNode(true, node1, node2, 0);
          Node rightNode = getNextNode(false, node1, node2, 0);
          if ((leftNode.x() >= 0 && leftNode.x() < col)
              && (leftNode.y() >= 0 && leftNode.y() < row)) {
            // LOGGER.debug("{} {} {} {}", newY1, newX1, node1, node2);
            antinodeMap[leftNode.y()][leftNode.x()] = '#';
          }
          if ((rightNode.x() >= 0 && rightNode.x() < col)
              && (rightNode.y() >= 0 && rightNode.y() < row)) {
            antinodeMap[rightNode.y()][rightNode.x()] = '#';
          }
        }
      }
    }
    int count = 0;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        char c = antinodeMap[i][j];
        if (c == '#') {
          count++;
        }
      }
    }
    // print2dChar(antennaMap);
    LOGGER.debug("part1 {}", count);
    // print2dChar(antinodeMap);

    for (int i = 0; i < row; i++) {
      String str = strList.get(i);
      for (int j = 0; j < col; j++) {
        char c = str.charAt(j);
        antennaMap[i][j] = c;
        antinodeMap[i][j] = '.';
      }
    }

    for (Map.Entry<Character, List<Node>> entry : antennaNodeMap.entrySet()) {
      List<Node> nodeList = entry.getValue();
      for (int i = 0; i < nodeList.size(); i++) {
        Node node1 = nodeList.get(i);
        for (int j = i + 1; j < nodeList.size(); j++) {
          Node node2 = nodeList.get(j);
          antinodeMap[node1.y()][node1.x()] = '#';
          antinodeMap[node2.y()][node2.x()] = '#';
          Node leftNode = getNextNode(true, node1, node2, 0);
          Node rightNode = getNextNode(false, node1, node2, 0);
          if ((leftNode.x() >= 0 && leftNode.x() < col)
              && (leftNode.y() >= 0 && leftNode.y() < row)) {
            // LOGGER.debug("{} {} {} {}", newY1, newX1, node1, node2);
            antinodeMap[leftNode.y()][leftNode.x()] = '#';
            int counter = 1;
            while ((leftNode.x() >= 0 && leftNode.x() < col)
                && (leftNode.y() >= 0 && leftNode.y() < row)) {
              antinodeMap[leftNode.y()][leftNode.x()] = '#';
              leftNode = getNextNode(true, node1, node2, counter);
              counter++;
            }
          }
          if ((rightNode.x() >= 0 && rightNode.x() < col)
              && (rightNode.y() >= 0 && rightNode.y() < row)) {
            antinodeMap[rightNode.y()][rightNode.x()] = '#';
            int counter = 1;
            while ((rightNode.x() >= 0 && rightNode.x() < col)
                && (rightNode.y() >= 0 && rightNode.y() < row)) {
              antinodeMap[rightNode.y()][rightNode.x()] = '#';
              rightNode = getNextNode(false, node1, node2, counter);
              counter++;
            }
          }
        }
      }
    }
    count = 0;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        char c = antinodeMap[i][j];
        if (c == '#') {
          count++;
        }
      }
    }
    // print2dChar(antennaMap);
    LOGGER.debug("part2 {}", count);
    // print2dChar(antinodeMap);
  }

  private static Node getNextNode(boolean left, Node node1, Node node2, int counter) {
    if (left) {
      if (node1.x() > node2.x()) {
        Node temp = node1;
        node1 = node2;
        node2 = temp;
      }
      int newX = node1.x() - (node2.x() - node1.x());
      int newY = node1.y() + (node1.y() - node2.y());
      while (counter > 0) {
        newX -= (node2.x() - node1.x());
        newY += (node1.y() - node2.y());
        counter--;
      }
      return new Node(newX, newY);
    } else {
      if (node1.x() > node2.x()) {
        Node temp = node1;
        node1 = node2;
        node2 = temp;
      }
      int newX = node2.x() + (node2.x() - node1.x());
      int newY = node2.y() - (node1.y() - node2.y());
      while (counter > 0) {
        newX += (node2.x() - node1.x());
        newY -= (node1.y() - node2.y());
        counter--;
      }
      return new Node(newX, newY);
    }
  }
}
