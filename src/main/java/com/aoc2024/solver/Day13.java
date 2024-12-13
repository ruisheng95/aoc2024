package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.record.NodeLong;
import com.aoc2024.utils.CommonUtils;

public class Day13 implements AocPuzzle {
  private static final Logger LOGGER = LoggerFactory.getLogger(Day13.class);

  private static final int TOKEN_A_COST = 3;

  private static final int TOKEN_B_COST = 1;

  private NodeLong buttonA;

  private NodeLong buttonB;

  private NodeLong prize;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Day13 [buttonA=").append(buttonA).append(", buttonB=").append(buttonB)
        .append(", prize=").append(prize).append("]");
    return builder.toString();
  }

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day13.txt");
    List<Day13> day13List = new ArrayList<>();
    for (int i = 0; i < strList.size() - 2; i++) {
      String[] btnAStr = strList.get(i).split(",");
      String[] btnBStr = strList.get(i + 1).split(",");
      String[] prizeStr = strList.get(i + 2).split(",");
      Day13 day13 = new Day13();
      day13.buttonA = new NodeLong(NumberUtils.toInt(StringUtils.substringAfter(btnAStr[0], "+")),
          NumberUtils.toInt(StringUtils.substringAfter(btnAStr[1], "+")));
      day13.buttonB = new NodeLong(NumberUtils.toInt(StringUtils.substringAfter(btnBStr[0], "+")),
          NumberUtils.toInt(StringUtils.substringAfter(btnBStr[1], "+")));
      day13.prize = new NodeLong(NumberUtils.toInt(StringUtils.substringAfter(prizeStr[0], "=")),
          NumberUtils.toInt(StringUtils.substringAfter(prizeStr[1], "=")));
      i += 3;
      day13List.add(day13);
    }
    // LOGGER.debug("@@ {}", day13List);

    AtomicLong tokens = new AtomicLong();
    day13List.stream().forEach(day13 -> {
      NodeLong solution = day13.getSolution();
      if (solution != null) {
        // LOGGER.debug("@@ {} {}", day13.prize, solution);
        tokens.addAndGet(solution.x() * 3 + solution.y());
      }
    });
    LOGGER.debug("part1 {}", tokens.get());

    for (Day13 day13 : day13List) {
      day13.prize =
          new NodeLong(day13.prize.x() + 10000000000000L, day13.prize.y() + 10000000000000L);
    }

    tokens.set(0);
    day13List.stream().forEach(day13 -> {
      NodeLong solution = day13.getSolutionPart2();
      if (solution != null) {
        // LOGGER.debug("@@ {} {}", day13.prize, solution);
        tokens.addAndGet(solution.x() * 3 + solution.y());
      }
    });
    LOGGER.debug("part2 {}", tokens.get());
  }

  // Slow brute force method
  private NodeLong getSolution() {
    NodeLong solution = null;
    // HashMap?
    List<NodeLong> xSolutionList = new ArrayList<>();
    boolean isButtonBTokenXLargerThanA = buttonB.x() * TOKEN_A_COST > buttonA.x() ? true : false;
    long start = prize.x() / (isButtonBTokenXLargerThanA ? buttonB.x() : buttonA.x()) + 1;
    for (long i = start; i > start - Math.min(10000, start); i--) {
      long index = i;

      NodeLong solutionX = null;
      int counter = 0;
      NodeLong buttonConst;
      NodeLong buttonMultipied;
      if (isButtonBTokenXLargerThanA) {
        buttonMultipied = buttonA;
        buttonConst = buttonB;
      } else {
        buttonMultipied = buttonB;
        buttonConst = buttonA;
      }

      while (counter * buttonMultipied.x() + index * buttonConst.x() <= prize.x()) {
        if (counter * buttonMultipied.x() + index * buttonConst.x() == prize.x()) {
          if (isButtonBTokenXLargerThanA) {
            solutionX = new NodeLong(counter, index);
          } else {
            solutionX = new NodeLong(index, counter);
          }
        }
        counter++;
      }

      if (solutionX != null) {
        xSolutionList.add(solutionX);
        // LOGGER.debug("@@1 {} {}", prize, solutionX);
      }
    }
    List<NodeLong> xySolutionList = new ArrayList<>();
    boolean isButtonBTokenYLargerThanA = buttonB.y() * TOKEN_A_COST > buttonA.y() ? true : false;
    start = prize.y() / (isButtonBTokenYLargerThanA ? buttonB.y() : buttonA.y()) + 1;
    for (long i = start; i > start - Math.min(10000, start); i--) {
      long index = i;

      NodeLong solutionY = null;
      int counter = 0;
      NodeLong buttonConst;
      NodeLong buttonMultipied;
      if (isButtonBTokenYLargerThanA) {
        buttonMultipied = buttonA;
        buttonConst = buttonB;
      } else {
        buttonMultipied = buttonB;
        buttonConst = buttonA;
      }
      while (counter * buttonMultipied.y() + index * buttonConst.y() <= prize.y()) {
        if (counter * buttonMultipied.y() + index * buttonConst.y() == prize.y()) {
          if (isButtonBTokenYLargerThanA) {
            solutionY = new NodeLong(counter, index);
          } else {
            solutionY = new NodeLong(index, counter);
          }

        }
        counter++;
      }
      if (solutionY != null && xSolutionList.contains(solutionY)) {
        // LOGGER.debug("@@2 {} {}", prize, solutionY);
        xySolutionList.add(solutionY);
      }
    }


    long cost = Integer.MAX_VALUE;
    for (NodeLong xySolution : xySolutionList) {
      // LOGGER.debug("@@2 {} {}", prize, xySolution);
      long newCost = xySolution.x() * TOKEN_A_COST + xySolution.y() * TOKEN_B_COST;
      if (newCost < cost) {
        if (cost != Integer.MAX_VALUE) {
          // LOGGER.debug("@@3 {} {}", prize, xySolution);
        }
        cost = newCost;
        solution = xySolution;
      }
    }
    return solution;
  }

  private NodeLong getSolutionPart2() {

    boolean a = isEvenlyDivisable(prize.x() * buttonB.y() - prize.y() * buttonB.x(),
        buttonA.x() * buttonB.y() - buttonA.y() * buttonB.x());
    boolean b = isEvenlyDivisable(prize.y() * buttonA.x() - prize.x() * buttonA.y(),
        buttonA.x() * buttonB.y() - buttonA.y() * buttonB.x());
    if (a && b) {
      long aVal = (prize.x() * buttonB.y() - prize.y() * buttonB.x())
          / (buttonA.x() * buttonB.y() - buttonA.y() * buttonB.x());
      long bVal = (prize.y() * buttonA.x() - prize.x() * buttonA.y())
          / (buttonA.x() * buttonB.y() - buttonA.y() * buttonB.x());
      // LOGGER.debug("{} {} {}", prize, 3 * aVal + bVal);
      return new NodeLong(aVal, bVal);
    }

    return null;
  }

  private static boolean isEvenlyDivisable(long a, long b) {
    return a % b == 0;
  }
}
