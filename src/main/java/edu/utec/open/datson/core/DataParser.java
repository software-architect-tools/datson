package edu.utec.open.datson.core;

import java.util.List;
import java.util.Map;

public interface DataParser {

  public Object parseCollection(List<Map<String, Object>> rawData) throws Exception;
}
