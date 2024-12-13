package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day5 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day5.class);

  @Override
  public void solve() {
    Map<Integer, List<Integer>> forwardMap = new HashMap<>();
    Map<Integer, List<Integer>> backwardMap = new HashMap<>();
    List<List<Integer>> updateList = new ArrayList<>();
    List<String> strList = CommonUtils.getFileStrList("day5.txt");
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
}
