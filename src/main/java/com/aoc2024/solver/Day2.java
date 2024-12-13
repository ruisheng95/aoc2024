package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day2 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day2.class);

  @Override
  public void solve() {
    List<List<Integer>> list = new ArrayList<>();
    List<String> strList = CommonUtils.getFileStrList("day2.txt");
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
}
