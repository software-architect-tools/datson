package edu.utec.open.datson.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import edu.utec.open.datson.core.DataParser;

public class SimpleRawDataParser implements DataParser {

  @Override
  public Object parseCollection(List<Map<String, Object>> rawData) throws Exception {
    // get header names
    if (rawData == null || rawData.isEmpty()) {
      throw new Exception("data is null or empty");
    }

    ArrayList<Object> parsedResult = new ArrayList<Object>();

    for (Map<String, Object> row : rawData) {

      Map<String, Object> parsedRow = new HashMap<String, Object>();

      for (Entry<String, Object> entry : row.entrySet()) {
        performInsertion(entry.getKey(), entry.getValue(), parsedRow);
      }

      parsedResult.add(parsedRow);
    }

    return parsedResult;
  }

  public void performInsertion(String key, Object value, Map<String, Object> parsedRow) {
    if (key == null || key.isEmpty()) {
      return;
    }

    int count = key.length() - key.replace(".", "").length();
    if (count >= 1) {
      String[] dots = key.split(".");
      for (int a = 0; a < dots.length - 1; a++) {
        if (parsedRow.get(dots[a]) == null) {
          parsedRow.put(dots[a], new HashMap<String, Object>());
        }
      }
    } else {
      parsedRow.put(key, value);
    }
  }

}
