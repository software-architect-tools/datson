package edu.utec.open.datson.common;

import java.util.HashMap;
import java.util.Map;

public class TestDataHelper {

  public static Map<String, Object> createMockedRow(Object... arguments) {
    String[] columNames = ((String) arguments[0]).replaceAll("\\s", "").split(",");
    HashMap<String, Object> row = new HashMap<String, Object>();
    for (int a = 0; a < columNames.length; a++) {
      row.put(columNames[a], arguments[a + 1]);
    }
    return row;
  }
}
