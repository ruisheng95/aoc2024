package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day1 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day1.class);

  private static final String FILE_NAME = "day1.txt";

  @Override
  public void solve() {
    List<Integer> list1 = new ArrayList<>();
    List<Integer> list2 = new ArrayList<>();
    Map<Integer, Integer> list2CountMap = new HashMap<>();
    List<String> strList = CommonUtils.getFileStrList(FILE_NAME);
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
}
