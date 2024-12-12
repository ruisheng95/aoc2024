package com.aoc2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static record Node(int x, int y) {

    public static Node getNextNode(boolean left, Node node1, Node node2, int counter) {
      if (left) {
        if (node1.x > node2.x) {
          Node temp = node1;
          node1 = node2;
          node2 = temp;
        }
        int newX = node1.x - (node2.x - node1.x);
        int newY = node1.y + (node1.y - node2.y);
        while (counter > 0) {
          newX -= (node2.x - node1.x);
          newY += (node1.y - node2.y);
          counter--;
        }
        return new Node(newX, newY);
      } else {
        if (node1.x > node2.x) {
          Node temp = node1;
          node1 = node2;
          node2 = temp;
        }
        int newX = node2.x + (node2.x - node1.x);
        int newY = node2.y - (node1.y - node2.y);
        while (counter > 0) {
          newX += (node2.x - node1.x);
          newY -= (node1.y - node2.y);
          counter--;
        }
        return new Node(newX, newY);
      }
    }
  }

  private static record TrailHead(int startX, int startY, int endX, int endY) {
  }

  private static record Day11Node(int index, long stone) {
  }

  private static void print2dChar(char[][] map) {
    // Print
    for (char[] row : map) {
      LOGGER.debug("{}", row);
    }
  }

  private static List<String> getFileStrList(String filename) {
    List<String> strList = new ArrayList<>();
    try (Stream<String> fileStream = Files.lines(Paths.get("src/main/resources/" + filename))) {
      fileStream.forEach(strList::add);
    } catch (IOException e) {
      LOGGER.error("Error readFile ", e);
    }
    return strList;
  }

  private static boolean day9_isDiskOutputSorted(List<String> diskOutputList) {
    var firstHalf = diskOutputList.subList(0, diskOutputList.indexOf("."));
    var secondHalf = diskOutputList.subList(diskOutputList.indexOf("."), diskOutputList.size());

    boolean result = firstHalf.stream().allMatch(NumberUtils::isDigits);
    if (result) {
      result = secondHalf.stream().noneMatch(NumberUtils::isDigits);
    }
    return result;
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

  private static long day11Solve(int index, long stone, Map<Day11Node, Long> day11Cache) {
    if (index == 75) {
      return 1L;
    }
    Day11Node currentNode = new Day11Node(index, stone);
    if (day11Cache.containsKey(currentNode)) {
      return day11Cache.get(currentNode);
    }
    String str = Long.toString(stone);
    long count = 0;
    if ("0".equals(str)) {
      count = day11Solve(index + 1, 1L, day11Cache);
      day11Cache.put(new Day11Node(index + 1, 1L), count);
    } else if (str.length() % 2 == 0) {
      long stone1 = NumberUtils.toLong(str.substring(0, str.length() / 2));
      long stone2 = NumberUtils.toLong(str.substring(str.length() / 2, str.length()));
      long count1 = day11Solve(index + 1, stone1, day11Cache);
      day11Cache.put(new Day11Node(index + 1, stone1), count1);
      long count2 = day11Solve(index + 1, stone2, day11Cache);
      day11Cache.put(new Day11Node(index + 1, stone2), count2);
      count = count1 + count2;
    } else {
      count = day11Solve(index + 1, stone * 2024, day11Cache);
      day11Cache.put(new Day11Node(index + 1, stone * 2024), count);
    }
    return count;
  }

  private static void day12Solve(char[][] map, char c, Node pos, List<Node> totalNodes) {
    if (pos.x < 0 || pos.x == map[0].length || pos.y < 0 || pos.y == map.length
        || map[pos.y][pos.x] != c) {
      return;
    }
    totalNodes.add(pos);
    map[pos.y][pos.x] = '#';
    day12Solve(map, c, new Node(pos.x, pos.y - 1), totalNodes);
    day12Solve(map, c, new Node(pos.x + 1, pos.y), totalNodes);
    day12Solve(map, c, new Node(pos.x, pos.y + 1), totalNodes);
    day12Solve(map, c, new Node(pos.x - 1, pos.y), totalNodes);
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
        if (node.y - 1 >= 0 && map[node.y - 1][node.x] == c) {
          adjacentNode++;
        }
        if (node.x + 1 < col && map[node.y][node.x + 1] == c) {
          adjacentNode++;
        }
        if (node.y + 1 < row && map[node.y + 1][node.x] == c) {
          adjacentNode++;
        }
        if (node.x - 1 >= 0 && map[node.y][node.x - 1] == c) {
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
      if (pos.y - 1 >= 0) {
        c1 = map[pos.y - 1][pos.x];
      }
      if (pos.x + 1 < col) {
        c2 = map[pos.y][pos.x + 1];
      }
      if (pos.y - 1 >= 0 && pos.x + 1 < col) {
        c3 = map[pos.y - 1][pos.x + 1];
      }
      if (c1 != c && c2 != c) {
        return true;
      } else if (c1 == c && c3 != c && c2 == c) {
        return true;
      }
    } else if (dir == 1) {
      if (pos.y + 1 < row) {
        c1 = map[pos.y + 1][pos.x];
      }
      if (pos.x + 1 < col) {
        c2 = map[pos.y][pos.x + 1];
      }
      if (pos.y + 1 < row && pos.x + 1 < col) {
        c3 = map[pos.y + 1][pos.x + 1];
      }
      if (c1 != c && c2 != c) {
        return true;
      } else if (c1 == c && c3 != c && c2 == c) {
        return true;
      }
    } else if (dir == 2) {
      if (pos.y + 1 < row) {
        c1 = map[pos.y + 1][pos.x];
      }
      if (pos.x - 1 >= 0) {
        c2 = map[pos.y][pos.x - 1];
      }
      if (pos.y + 1 < row && pos.x - 1 >= 0) {
        c3 = map[pos.y + 1][pos.x - 1];
      }
      if (c1 != c && c2 != c) {
        return true;
      } else if (c1 == c && c3 != c && c2 == c) {
        return true;
      }
    } else if (dir == 3) {
      if (pos.y - 1 >= 0) {
        c1 = map[pos.y - 1][pos.x];
      }
      if (pos.x - 1 >= 0) {
        c2 = map[pos.y][pos.x - 1];
      }
      if (pos.y - 1 >= 0 && pos.x - 1 >= 0) {
        c3 = map[pos.y - 1][pos.x - 1];
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

  private static void day1() {
    List<Integer> list1 = new ArrayList<>();
    List<Integer> list2 = new ArrayList<>();
    Map<Integer, Integer> list2CountMap = new HashMap<>();
    List<String> strList = getFileStrList("day1.txt");
    for (var str : strList) {
      String[] strArr = str.split("   ");
      list1.add(NumberUtils.toInt(strArr[0]));
      list2.add(NumberUtils.toInt(strArr[1]));
      list2CountMap.merge(NumberUtils.toInt(strArr[1]), 1, (a, b) -> a + b);
    }
    Collections.sort(list1);
    Collections.sort(list2);
    long sum = 0;
    for (int i = 0; i < list1.size(); i++) {
      sum += Math.abs(list1.get(i) - list2.get(i));
    }
    LOGGER.debug("part1 {}", sum);
    sum = 0;
    for (int i = 0; i < list1.size(); i++) {
      sum += list1.get(i) * list2CountMap.getOrDefault(list1.get(i), 0);
    }
    LOGGER.debug("part2 {}", sum);
  }

  private static void day2() {
    List<List<Integer>> list = new ArrayList<>();
    List<String> strList = getFileStrList("day2.txt");
    for (var str : strList) {
      String[] strArr = str.split(" ");
      List<Integer> innerList = new ArrayList<>();
      for (String numbers : strArr) {
        innerList.add(NumberUtils.toInt(numbers));
      }
      list.add(innerList);
    }
    AtomicInteger count = new AtomicInteger();
    list.forEach(innerList -> {
      boolean result = innerList.stream().sorted().toList().equals(innerList);
      if (!result) {
        result = innerList.stream().sorted(Comparator.reverseOrder()).toList().equals(innerList);
      }
      if (result) {
        int max = 0;
        for (int i = 0; i < innerList.size() - 1; i++) {
          int num1 = innerList.get(i);
          int num2 = innerList.get(i + 1);
          if (Math.abs(num1 - num2) == 0) {
            max = 999;
          } else if (Math.abs(num1 - num2) > max) {
            max = Math.abs(num1 - num2);
          }
        }
        if (max <= 3) {
          count.addAndGet(1);
        }
      }
    });
    LOGGER.debug("part1: {}", count.get());

    count.set(0);
    list.forEach(innerList -> {
      boolean result = false;
      result = innerList.stream().sorted().toList().equals(innerList);
      if (!result) {
        result = innerList.stream().sorted(Comparator.reverseOrder()).toList().equals(innerList);
      }
      if (result) {
        int max = 0;
        for (int i = 0; i < innerList.size() - 1; i++) {
          int num1 = innerList.get(i);
          int num2 = innerList.get(i + 1);
          if (Math.abs(num1 - num2) == 0) {
            max = 999;
          } else if (Math.abs(num1 - num2) > max) {
            max = Math.abs(num1 - num2);
          }
        }
        if (max <= 3) {
          count.addAndGet(1);
          result = true;
        } else {
          result = false;
        }
      }
      if (!result) {
        // brute force
        for (int j = 0; j < innerList.size(); j++) {
          List<Integer> tempList = new ArrayList<>(innerList);
          tempList.remove(j);
          result = tempList.stream().sorted().toList().equals(tempList);
          if (!result) {
            result = tempList.stream().sorted(Comparator.reverseOrder()).toList().equals(tempList);
          }
          if (result) {
            int max = 0;
            for (int i = 0; i < tempList.size() - 1; i++) {
              int num1 = tempList.get(i);
              int num2 = tempList.get(i + 1);
              if (Math.abs(num1 - num2) == 0) {
                max = 999;
              } else if (Math.abs(num1 - num2) > max) {
                max = Math.abs(num1 - num2);
              }
            }
            if (max <= 3) {
              count.addAndGet(1);
              result = true;
              break;
            } else {
              result = false;
            }
          }
        }
      }
    });
    LOGGER.debug("part2: {}", count.get());
  }

  private static void day3() {
    AtomicInteger count = new AtomicInteger();
    Pattern mulPattern = Pattern.compile("mul\\(\\d{1,3}\\,\\d{1,3}\\)");
    Pattern numPattern = Pattern.compile("\\d{1,3}");
    List<String> strList = getFileStrList("day3.txt");
    for (var str : strList) {
      Matcher mulMatcher = mulPattern.matcher(str);
      while (mulMatcher.find()) {
        String mulString = mulMatcher.group();
        // LOGGER.debug("@@ {}", mulString);
        Matcher numMatcher = numPattern.matcher(mulString);
        int[] num = new int[2];
        int i = 0;
        while (numMatcher.find()) {
          num[i] = NumberUtils.toInt(numMatcher.group());
          i++;
        }
        // LOGGER.debug("@@ {} {}", num[0], num[1]);
        count.addAndGet(num[0] * num[1]);
      }
    }
    LOGGER.debug("part1: {}", count.get());

    Pattern mulPattern2 = Pattern.compile("do\\(\\)|don't\\(\\)|mul\\(\\d{1,3}\\,\\d{1,3}\\)");
    List<String> tokenList = new ArrayList<>();
    for (var str : strList) {
      Matcher mulMatcher = mulPattern2.matcher(str);
      while (mulMatcher.find()) {
        tokenList.add(mulMatcher.group());
      }
    }
    count.set(0);
    boolean isProcess = true;
    for (int i = 0; i < tokenList.size(); i++) {
      String token = tokenList.get(i);
      if ("do()".equals(token)) {
        isProcess = true;
      } else if ("don't()".equals(token)) {
        isProcess = false;
      } else if (isProcess) {
        Matcher numMatcher = numPattern.matcher(token);
        int[] num = new int[2];
        int j = 0;
        while (numMatcher.find()) {
          num[j] = NumberUtils.toInt(numMatcher.group());
          j++;
        }
        // LOGGER.debug("@@ {} {}", num[0], num[1]);
        count.addAndGet(num[0] * num[1]);
      }
      // LOGGER.debug("@@{}", tokenList.get(i));
    }
    LOGGER.debug("part2: {}", count.get());
  }

  private static void day4() {
    List<String> list = getFileStrList("day4.txt");
    String XMAS = "XMAS";
    long count = 0;
    for (String str : list) {
      count += StringUtils.countMatches(str, XMAS);
      String reverse = new StringBuilder(str).reverse().toString();
      count += StringUtils.countMatches(reverse, XMAS);
    }
    for (int i = 0; i < list.get(0).length(); i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < list.size(); j++) {
        char c = list.get(j).charAt(i);
        sb.append(c);
      }
      String str = sb.toString();
      count += StringUtils.countMatches(str, XMAS);
      String reverse = new StringBuilder(str).reverse().toString();
      count += StringUtils.countMatches(reverse, XMAS);
    }

    for (int i = 0; i < list.size(); i++) {
      for (int j = 0; j < list.get(i).length(); j++) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        while (sb.length() < 4 && i + k < list.size() && j + k < list.get(i).length()) {
          sb.append(list.get(i + k).charAt(j + k));
          k++;
        }
        String str = sb.toString();
        count += StringUtils.countMatches(str, XMAS);
        String reverse = new StringBuilder(str).reverse().toString();
        count += StringUtils.countMatches(reverse, XMAS);

        sb.setLength(0);
        k = 0;
        while (sb.length() < 4 && i - k < list.size() && j + k >= 0) {
          sb.append(list.get(i - k).charAt(j + k));
          k--;
        }
        str = sb.toString();
        count += StringUtils.countMatches(str, XMAS);
        reverse = new StringBuilder(str).reverse().toString();
        count += StringUtils.countMatches(reverse, XMAS);
      }
    }
    LOGGER.debug("part1: {}", count);
    XMAS = "MAS";
    count = 0;
    for (int i = 0; i < list.size(); i++) {
      for (int j = 0; j < list.get(i).length(); j++) {
        StringBuilder sb = new StringBuilder();
        int k = 0;
        while (sb.length() < 3 && i + k < list.size() && j + k < list.get(i).length()) {
          sb.append(list.get(i + k).charAt(j + k));
          k++;
        }
        String str = sb.toString();
        int tempCount = 0;
        tempCount += StringUtils.countMatches(str, XMAS);
        String reverse = new StringBuilder(str).reverse().toString();
        tempCount += StringUtils.countMatches(reverse, XMAS);

        if (tempCount == 0) {
          continue;
        }

        sb.setLength(0);
        k = 0;
        while (sb.length() < 3 && i - k < list.size() && j + k + 2 >= 0) {
          sb.append(list.get(i - k).charAt(j + k + 2));
          k--;
        }
        str = sb.toString();
        tempCount += StringUtils.countMatches(str, XMAS);
        reverse = new StringBuilder(str).reverse().toString();
        tempCount += StringUtils.countMatches(reverse, XMAS);

        if (tempCount == 2) {
          count++;
        }
      }
    }
    LOGGER.debug("part2: {}", count);
  }

  private static void day5() {
    Map<Integer, List<Integer>> forwardMap = new HashMap<>();
    Map<Integer, List<Integer>> backwardMap = new HashMap<>();
    List<List<Integer>> updateList = new ArrayList<>();
    List<String> strList = getFileStrList("day5.txt");
    for (var str : strList) {
      if (str.contains("|")) {
        String[] strArr = str.split("\\|");
        int num1 = NumberUtils.toInt(strArr[0]);
        int num2 = NumberUtils.toInt(strArr[1]);
        forwardMap.computeIfAbsent(num1, k -> new ArrayList<>()).add(num2);
        backwardMap.computeIfAbsent(num2, k -> new ArrayList<>()).add(num1);
      } else if (!str.isEmpty()) {
        List<Integer> innerList = new ArrayList<>();
        String[] strArr = str.split(",");
        for (String numberStr : strArr) {
          innerList.add(NumberUtils.toInt(numberStr));
        }
        updateList.add(innerList);
      }
    }

    List<List<Integer>> filteredList = new ArrayList<>();
    List<List<Integer>> invalidList = new ArrayList<>();
    for (var innerList : updateList) {
      boolean match = true;
      for (int i = 0; i < innerList.size() - 1 && match; i++) {
        int num1 = innerList.get(i);

        var backwardList = backwardMap.get(num1);
        for (int j = i + 1; j < innerList.size() && match; j++) {
          int num2 = innerList.get(j);
          if (backwardList != null && backwardList.contains(num2)) {
            match = false;
            invalidList.add(innerList);
            // LOGGER.debug("failed1 {} {} {}", innerList, num1, num2);
          }
        }

        var forwardList = forwardMap.get(num1);
        for (int j = i - 1; j >= 0 && match; j--) {
          int num2 = innerList.get(j);
          if (forwardList != null && forwardList.contains(num2)) {
            match = false;
            // This part never executes
            // LOGGER.debug("failed2 {} {} {}", innerList, num1, num2);
          }
        }
      }
      if (match) {
        filteredList.add(innerList);
      }
    }
    // LOGGER.debug("{} {}", filteredList.get(0) ,
    // filteredList.get(0).get(filteredList.get(0).size() / 2));
    int result = 0;
    for (var innerList : filteredList) {
      result += innerList.get(innerList.size() / 2);
    }
    LOGGER.debug("part1: {}", result);
    for (var innerList : invalidList) {
      for (int i = 0; i < innerList.size() - 1; i++) {
        for (int j = i + 1; j < innerList.size(); j++) {
          int num1 = innerList.get(i);
          var backwardList = backwardMap.get(num1);
          int num2 = innerList.get(j);
          if (backwardList != null && backwardList.contains(num2)) {
            innerList.set(i, num2);
            innerList.set(j, num1);
            // LOGGER.debug("try {} {} {} ", num1, num2, innerList);
            i = 0;
            j = 1;
            // break;
          }
        }
      }
      // LOGGER.debug("match! {}", innerList);
    }
    result = 0;
    for (var innerList : invalidList) {
      result += innerList.get(innerList.size() / 2);
    }
    LOGGER.debug("part2: {}", result);

    // Validate invalid list
    for (var innerList : invalidList) {
      boolean match = true;
      for (int i = 0; i < innerList.size() - 1 && match; i++) {
        int num1 = innerList.get(i);

        var backwardList = backwardMap.get(num1);
        for (int j = i + 1; j < innerList.size() && match; j++) {
          int num2 = innerList.get(j);
          if (backwardList != null && backwardList.contains(num2)) {
            match = false;
            // LOGGER.debug("failed1 {} {} {}", innerList, num1, num2);
          }
        }

        var forwardList = forwardMap.get(num1);
        for (int j = i - 1; j >= 0 && match; j--) {
          int num2 = innerList.get(j);
          if (forwardList != null && forwardList.contains(num2)) {
            match = false;
            // This part never executes
            // LOGGER.debug("failed2 {} {} {}", innerList, num1, num2);
          }
        }
      }
    }
  }

  private static void day6() {
    List<String> strList = getFileStrList("day6.txt");
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

  private static void day7() {
    List<String> strList = getFileStrList("day7.txt");

    List<Long> answerList = new ArrayList<>();
    List<List<Integer>> numberList = new ArrayList<>();
    for (var str : strList) {
      answerList.add(NumberUtils.toLong(StringUtils.substringBefore(str, ":")));
      String[] numArrStr = StringUtils.substringAfter(str, ":").substring(1).split(" ");
      List<Integer> innerNumberList = new ArrayList<>();
      for (var numStr : numArrStr) {
        innerNumberList.add(NumberUtils.toInt(numStr));
      }
      numberList.add(innerNumberList);
    }

    // LOGGER.debug("{} {}", answerList.size(), numberList.size());
    LOGGER.debug("start");
    AtomicLong count = new AtomicLong();
    IntStream.rangeClosed(0, answerList.size() - 1).boxed().forEach(index -> {
      long answer = answerList.get(index);
      List<Integer> numbers = numberList.get(index);
      boolean match = false;
      for (int i = 0; i < (int) Math.pow(2, (double) numbers.size() - 1) && !match; i++) {
        String binaryStr = StringUtils.leftPad(Integer.toBinaryString(i), numbers.size() - 1, '0');
        long testAnswer = 0;
        for (int j = 0; j < binaryStr.length(); j++) {
          char bit = binaryStr.charAt(j);
          if (bit == '1') {
            // Multiply
            if (testAnswer == 0) {
              testAnswer += numbers.get(j) * numbers.get(j + 1);
            } else {
              testAnswer = testAnswer * numbers.get(j + 1);
            }

          } else {
            // Add
            if (testAnswer == 0) {
              testAnswer += numbers.get(j) + numbers.get(j + 1);
            } else {
              testAnswer = testAnswer + numbers.get(j + 1);
            }
          }
        }
        if (testAnswer == answer) {
          // LOGGER.debug("match {} {}", answer, binaryStr);
          count.addAndGet(answer);
          match = true;
        }
      }
    });

    LOGGER.debug("part1: {}", count.get());
    count.set(0);
    IntStream.rangeClosed(0, answerList.size() - 1).boxed().parallel().forEach(index -> {
      long answer = answerList.get(index);
      List<Integer> numbers = numberList.get(index);
      boolean match = false;
      for (int i = 0; i < Math.pow(3, (double) numbers.size() - 1) && !match; i++) {
        String binaryStr = StringUtils.leftPad(Integer.toString(i, 3), numbers.size() - 1, '0');
        long testAnswer = 0;
        for (int j = 0; j < binaryStr.length(); j++) {
          char bit = binaryStr.charAt(j);
          if (bit == '1') {
            // Multiply
            if (testAnswer == 0) {
              testAnswer += numbers.get(j) * numbers.get(j + 1);
            } else {
              testAnswer = testAnswer * numbers.get(j + 1);
            }
          } else if (bit == '2') {
            // Concatenate
            if (testAnswer == 0) {
              testAnswer =
                  NumberUtils.toLong(numbers.get(j).toString() + numbers.get(j + 1).toString());
            } else {
              testAnswer =
                  NumberUtils.toLong(String.valueOf(testAnswer) + numbers.get(j + 1).toString());
            }
          } else {
            // Add
            if (testAnswer == 0) {
              testAnswer += numbers.get(j) + numbers.get(j + 1);
            } else {
              testAnswer = testAnswer + numbers.get(j + 1);
            }
          }
        }
        if (testAnswer == answer) {
          // LOGGER.debug("match {} {}", answer, binaryStr);
          count.addAndGet(answer);
          match = true;
        }
      }
    });
    LOGGER.debug("part2: {}", count.get());
  }

  private static void day8() {
    List<String> strList = getFileStrList("day8.txt");
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
          Node leftNode = Node.getNextNode(true, node1, node2, 0);
          Node rightNode = Node.getNextNode(false, node1, node2, 0);
          if ((leftNode.x >= 0 && leftNode.x < col) && (leftNode.y >= 0 && leftNode.y < row)) {
            // LOGGER.debug("{} {} {} {}", newY1, newX1, node1, node2);
            antinodeMap[leftNode.y][leftNode.x] = '#';
          }
          if ((rightNode.x >= 0 && rightNode.x < col) && (rightNode.y >= 0 && rightNode.y < row)) {
            antinodeMap[rightNode.y][rightNode.x] = '#';
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
          antinodeMap[node1.y][node1.x] = '#';
          antinodeMap[node2.y][node2.x] = '#';
          Node leftNode = Node.getNextNode(true, node1, node2, 0);
          Node rightNode = Node.getNextNode(false, node1, node2, 0);
          if ((leftNode.x >= 0 && leftNode.x < col) && (leftNode.y >= 0 && leftNode.y < row)) {
            // LOGGER.debug("{} {} {} {}", newY1, newX1, node1, node2);
            antinodeMap[leftNode.y][leftNode.x] = '#';
            int counter = 1;
            while ((leftNode.x >= 0 && leftNode.x < col) && (leftNode.y >= 0 && leftNode.y < row)) {
              antinodeMap[leftNode.y][leftNode.x] = '#';
              leftNode = Node.getNextNode(true, node1, node2, counter);
              counter++;
            }
          }
          if ((rightNode.x >= 0 && rightNode.x < col) && (rightNode.y >= 0 && rightNode.y < row)) {
            antinodeMap[rightNode.y][rightNode.x] = '#';
            int counter = 1;
            while ((rightNode.x >= 0 && rightNode.x < col)
                && (rightNode.y >= 0 && rightNode.y < row)) {
              antinodeMap[rightNode.y][rightNode.x] = '#';
              rightNode = Node.getNextNode(false, node1, node2, counter);
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

  private static void day9() {
    List<String> strList = getFileStrList("day9.txt");
    String diskMap = strList.get(0);
    List<String> diskOutputList = new ArrayList<>();
    int counter = 0;

    List<Integer> freeBlockPosList = new ArrayList<>();
    List<Integer> nonFreeBlockPosList = new ArrayList<>();
    for (int i = 0; i < diskMap.length(); i++) {
      char c = diskMap.charAt(i);
      int bit = Character.getNumericValue(c);
      boolean freeSpaceFlag = i % 2 == 1;
      for (int j = 0; j < bit; j++) {
        if (freeSpaceFlag) {
          freeBlockPosList.add(diskOutputList.size());
          diskOutputList.add(".");
        } else {
          nonFreeBlockPosList.add(diskOutputList.size());
          diskOutputList.add(Integer.toString(counter));
        }
      }
      if (!freeSpaceFlag) {
        counter++;
      }
    }

    Collections.reverse(nonFreeBlockPosList);
    AtomicInteger test = new AtomicInteger();
    LOGGER.debug("start {}", diskOutputList.size());
    IntStream.rangeClosed(0, Math.min(freeBlockPosList.size(), nonFreeBlockPosList.size()) - 1)
        .boxed().forEach(index -> {
          var freeBlockPos = freeBlockPosList.get(index);
          var nonBlockPos = nonFreeBlockPosList.get(index);
          if (freeBlockPos < nonBlockPos) {
            Collections.swap(diskOutputList, freeBlockPos, nonBlockPos);
            test.incrementAndGet();
          }
        });
    LOGGER.debug("disk sorted {}", day9_isDiskOutputSorted(diskOutputList));

    long checksum = 0;
    for (int i = 0; i < diskOutputList.size() && NumberUtils.isDigits(diskOutputList.get(i)); i++) {
      checksum += i * NumberUtils.toInt(diskOutputList.get(i));
    }
    LOGGER.debug("part1 {}", checksum);

    diskOutputList.clear();
    counter = 0;
    Map<Integer, Integer> blockCountMap = new HashMap<>();
    for (int i = 0; i < diskMap.length(); i++) {
      char c = diskMap.charAt(i);
      int bit = Character.getNumericValue(c);
      boolean freeSpaceFlag = i % 2 == 1;
      for (int j = 0; j < bit; j++) {
        if (freeSpaceFlag) {
          diskOutputList.add(".");
        } else {
          diskOutputList.add(Integer.toString(counter));
        }
      }
      if (!freeSpaceFlag) {
        blockCountMap.put(counter, bit);
        counter++;
      }
    }

    List<Integer> blockCountList = new ArrayList<>(blockCountMap.keySet());
    Collections.sort(blockCountList);
    List<List<Integer>> freeSpaceList = new ArrayList<>();
    boolean freeSpaceFlag = false;
    int startIndex = -1, endIndex = -1;
    for (int i = 0; i < diskOutputList.size(); i++) {
      String str = diskOutputList.get(i);
      freeSpaceFlag = ".".equals(str);
      if (freeSpaceFlag) {
        if (startIndex == -1) {
          startIndex = i;
        }
        endIndex = i;
      } else {
        if (startIndex != -1) {
          List<Integer> innerList = new ArrayList<>();
          innerList.add(startIndex);
          innerList.add(endIndex);
          freeSpaceList.add(innerList);
        }
        startIndex = -1;
        endIndex = -1;
      }
    }
    // LOGGER.debug("@@ {}", freeSpaceList.size());

    for (int i = blockCountList.size() - 1; i >= 0; i--) {
      int blockSize = blockCountMap.get(blockCountList.get(i));
      for (List<Integer> freeSpace : freeSpaceList) {
        if (!freeSpace.isEmpty() && blockSize <= freeSpace.get(1) - freeSpace.get(0) + 1) {
          int lastBlockPos = diskOutputList.lastIndexOf(blockCountList.get(i).toString());
          if (freeSpace.get(0) >= lastBlockPos) {
            break;
          }
          // LOGGER.debug("@@ {} {} {}", freeSpace.get(0), freeSpace.get(1), blockSize);
          for (int j = 0; j < blockSize; j++) {
            Collections.swap(diskOutputList, freeSpace.get(0) + j, lastBlockPos - j);
          }
          // LOGGER.debug("part2 {}", diskOutputList);
          if (blockSize == freeSpace.get(1) - freeSpace.get(0) + 1) {
            freeSpace.clear();
          } else {
            freeSpace.set(0, freeSpace.get(0) + blockSize);
          }
          break;
        }
      }
    }

    checksum = 0;
    for (int i = 0; i < diskOutputList.size(); i++) {
      if (!NumberUtils.isDigits(diskOutputList.get(i))) {
        continue;
      }
      checksum += i * NumberUtils.toInt(diskOutputList.get(i));
    }

    LOGGER.debug("part2 {}", checksum);
  }

  private static void day10() {
    List<String> strList = getFileStrList("day10.txt");
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

  private static void day11() {
    List<String> strList = getFileStrList("day11.txt");

    String[] strArr = strList.get(0).split(" ");
    List<Long> stoneList = new ArrayList<>();
    for (String str : strArr) {
      stoneList.add(NumberUtils.toLong(str));
    }

    LOGGER.debug("{}", stoneList);
    List<Long> tempStoneList = new ArrayList<>(stoneList);
    for (int i = 0; i < 25; i++) {
      List<Long> newStoneList = new ArrayList<>();
      for (long stone : tempStoneList) {
        String str = Long.toString(stone);
        if ("0".equals(str)) {
          newStoneList.add(1L);
        } else if (str.length() % 2 == 0) {
          newStoneList.add(NumberUtils.toLong(str.substring(0, str.length() / 2)));
          newStoneList.add(NumberUtils.toLong(str.substring(str.length() / 2, str.length())));
        } else {
          newStoneList.add(stone * 2024);
        }
      }
      tempStoneList = newStoneList;
    }
    LOGGER.debug("part1 {}", tempStoneList.size());

    tempStoneList = new ArrayList<>(stoneList);
    long counter = 0;
    Map<Day11Node, Long> day11Cache = new HashMap<>();
    for (long stone : tempStoneList) {
      counter += day11Solve(0, stone, day11Cache);
    }
    LOGGER.debug("part2 {}", counter);
  }

  private static void day12() {
    List<String> strList = getFileStrList("day12.txt");
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

  public static void main(String[] args) {
    day12();
  }
}
