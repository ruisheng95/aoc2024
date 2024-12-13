package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day11 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day11.class);

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day11.txt");

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
  
  private static record Day11Node(int index, long stone) {
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


}
