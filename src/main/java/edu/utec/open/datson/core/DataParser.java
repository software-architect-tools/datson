package edu.utec.open.datson.core;

import java.util.List;
import java.util.Map;

public interface DataParser {

  public Object parseEntityWithCollection(List<Map<String, Object>> rawData,
      String entityAttributeNames, String collecionAlias) throws Exception;

  public Object parseCollection(List<Map<String, Object>> rawData, boolean includeSimpleAttributes) throws Exception;
  
  public Object parseCollection(List<Map<String, Object>> rawData) throws Exception;

  public Object parseEntity(List<Map<String, Object>> rawData, String entityAttributeNames)
      throws Exception;
}
