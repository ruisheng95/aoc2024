package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day7 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day7.class);

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day7.txt");

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
}
