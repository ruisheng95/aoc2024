package com.aoc2024.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aoc2024.utils.CommonUtils;

public class Day9 implements AocPuzzle {

  private static final Logger LOGGER = LoggerFactory.getLogger(Day9.class);

  @Override
  public void solve() {
    List<String> strList = CommonUtils.getFileStrList("day9.txt");
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

  private static boolean day9_isDiskOutputSorted(List<String> diskOutputList) {
    var firstHalf = diskOutputList.subList(0, diskOutputList.indexOf("."));
    var secondHalf = diskOutputList.subList(diskOutputList.indexOf("."), diskOutputList.size());

    boolean result = firstHalf.stream().allMatch(NumberUtils::isDigits);
    if (result) {
      result = secondHalf.stream().noneMatch(NumberUtils::isDigits);
    }
    return result;
  }
}
