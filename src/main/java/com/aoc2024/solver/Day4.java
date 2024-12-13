package com.aoc2024.solver;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day4 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day4.class);

  @Override
  public void solve() {
    List<String> list = CommonUtils.getFileStrList("day4.txt");
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
}
