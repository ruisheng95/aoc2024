package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day3 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day3.class);

  @Override
  public void solve() {
    AtomicInteger count = new AtomicInteger();
    Pattern mulPattern = Pattern.compile("mul\\(\\d{1,3}\\,\\d{1,3}\\)");
    Pattern numPattern = Pattern.compile("\\d{1,3}");
    List<String> strList = CommonUtils.getFileStrList("day3.txt");
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
}
