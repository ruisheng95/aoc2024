package com.aoc2024.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

  public static List<String> getFileStrList(String filename) {
    List<String> strList = new ArrayList<>();
    try (Stream<String> fileStream = Files.lines(Paths.get("src/main/resources/" + filename))) {
      fileStream.forEach(strList::add);
    } catch (IOException e) {
      LOGGER.error("Error readFile ", e);
    }
    return strList;
  }

  public static void print2dChar(char[][] map) {
    // Print
    for (char[] row : map) {
      LOGGER.debug("{}", row);
    }
  }
}
