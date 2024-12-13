package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Day13 {
  private static final Logger LOGGER = LoggerFactory.getLogger(Day13.class);

  private static final int TOKEN_A_COST = 3;

  private static final int TOKEN_B_COST = 1;

  public Node buttonA;

  public Node buttonB;

  public Node prize;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Day13 [buttonA=").append(buttonA).append(", buttonB=").append(buttonB)
        .append(", prize=").append(prize).append("]");
    return builder.toString();
  }

  public Node getSolution() {
    Node solution = null;
    // HashMap?
    List<Node> xSolutionList = new ArrayList<>();
    boolean isButtonBTokenXLargerThanA = buttonB.x() * TOKEN_A_COST > buttonA.x() ? true : false;
    long start = prize.x() / (isButtonBTokenXLargerThanA ? buttonB.x() : buttonA.x()) + 1;
    for (long i = start; i > start - Math.min(10000, start); i--) {
      long index = i;

      Node solutionX = null;
      int counter = 0;
      Node buttonConst;
      Node buttonMultipied;
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
            solutionX = new Node(counter, index);
          } else {
            solutionX = new Node(index, counter);
          }
        }
        counter++;
      }

      if (solutionX != null) {
        xSolutionList.add(solutionX);
        // LOGGER.debug("@@1 {} {}", prize, solutionX);
      }
    }
    List<Node> xySolutionList = new ArrayList<>();
    boolean isButtonBTokenYLargerThanA = buttonB.y() * TOKEN_A_COST > buttonA.y() ? true : false;
    start = prize.y() / (isButtonBTokenYLargerThanA ? buttonB.y() : buttonA.y()) + 1;
    for (long i = start; i > start - Math.min(10000, start); i--) {
      long index = i;

      Node solutionY = null;
      int counter = 0;
      Node buttonConst;
      Node buttonMultipied;
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
            solutionY = new Node(counter, index);
          } else {
            solutionY = new Node(index, counter);
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
    for (Node xySolution : xySolutionList) {
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

  public Node getSolutionPart2() {

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
      return new Node(aVal, bVal);
    }

    return null;
  }

  public static boolean isEvenlyDivisable(long a, long b) {
    return a % b == 0;
  }
}
