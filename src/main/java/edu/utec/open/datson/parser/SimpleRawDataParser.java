package edu.utec.open.datson.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import edu.utec.open.datson.core.DataParser;

public class SimpleRawDataParser implements DataParser {

  @SuppressWarnings("unchecked")
  @Override
  public Object parseEntityWithCollection(List<Map<String, Object>> rawData,
      String entityAttributeNames, String collectionAlias) throws Exception {
    Map<String, Object> entity = (Map<String, Object>) parseEntity(rawData, entityAttributeNames);
    Object collection = parseCollection(rawData, false);
    entity.put(collectionAlias, collection);
    return entity;

  }

  @Override
  public Object parseEntity(List<Map<String, Object>> rawData, String entityAttributeNames)
      throws Exception {

    List<String> expectedAttributes =
        Arrays.asList(entityAttributeNames.replaceAll("\\s", "").split(","));

    LinkedHashMap<String, Object> simpleEntity =
        getExpectedColumns(expectedAttributes, rawData.get(0));

    return simpleEntity;
  }

  @Override
  public Object parseCollection(List<Map<String, Object>> rawData) throws Exception {
    return parseCollection(rawData, true);
  }

  @Override
  public Object parseCollection(List<Map<String, Object>> rawData, boolean includeSimpleAttributes)
      throws Exception {
    // get header names
    if (rawData == null || rawData.isEmpty()) {
      throw new Exception("data is null or empty");
    }

    ArrayList<Object> parsedResult = new ArrayList<Object>();

    for (Map<String, Object> row : rawData) {

      Map<String, Object> parsedRow = new HashMap<String, Object>();

      for (Entry<String, Object> entry : row.entrySet()) {
        performInsertion(entry.getKey(), entry.getValue(), parsedRow, includeSimpleAttributes);
      }

      System.out.println("--------------");
      System.out.println(parsedRow);

      parsedResult.add(parsedRow);
    }

    return parsedResult;
  }

  @SuppressWarnings("unchecked")
  public void performInsertion(String key, Object value, Map<String, Object> parsedRow,
      boolean includeSimpleAttributes) throws Exception {
    if (key == null || key.isEmpty()) {
      return;
    }

    int count = key.length() - key.replace(".", "").length();

    System.out.println("key:" + key);

    // if key has more or eq than one dot
    // it means a inner key
    if (count >= 1) {
      // get child of key to create one by one
      String[] nodesOfTheKey = key.split("\\.");

      Object thisNode = parsedRow;
      Object previousNode = null;
      for (int a = 0; a < nodesOfTheKey.length; a++) {
        String child = nodesOfTheKey[a];// get first node key
        System.out.println("child :" + child);
        if (a == nodesOfTheKey.length - 1) {// last iteration
          System.out.println("is last");
          System.out.println("save in:" + thisNode);
          if (thisNode instanceof Map) {
            if (((Map<String, Object>) thisNode).get(child) == null) {
              ((Map<String, Object>) thisNode).put(child, value);
            }
          } else if (thisNode instanceof ArrayList) {
            System.out.println("add to list:" + value);
            ((ArrayList) thisNode).add(value);
          } else {
            throw new Exception("type not supported as final node:" + thisNode.getClass());
          }
        } else {
          if (thisNode instanceof Map) {
            if (((Map<String, Object>) thisNode).get(child) == null) {
              // create it if not exist
              if (child.endsWith("[]")) {
                System.out.println("is collection");
                ((Map<String, Object>) thisNode).put(child.replace("[]", ""),
                    new ArrayList<HashMap<String, Object>>());
              } else {
                ((Map<String, Object>) thisNode).put(child.replace("[]", ""),
                    new HashMap<String, Object>());
              }

              thisNode = ((Map<String, Object>) thisNode).get(child.replace("[]", ""));
            }
          } else {
            throw new Exception("type not supported as middle node:" + thisNode.getClass());
          }
        }

      }

    } else {
      // this key does not have dots, it is a simple key
      if (includeSimpleAttributes) {
        parsedRow.put(key, value);
      }
    }
  }

  public Object parse(List<Map<String, Object>> rawData) throws Exception {

    Map<String, Object> complexOject = new HashMap<String, Object>();

    for (Map<String, Object> row : rawData) {
      for (Entry<String, Object> entry : row.entrySet()) {
        System.out.println("-----");
        performInsertion(entry.getKey(), entry.getValue(), complexOject);
      }
    }

    return complexOject;

  }

  public void performInsertion(String key, Object value, Object jsonObject) throws Exception {

    int count = key.length() - key.replace(".", "").length();
    System.out.println("key:" + key);
    System.out.println("value:" + value);
    if (count >= 1) {
      String[] nodesOfTheKey = key.split("\\.");
      String child = nodesOfTheKey[0];
      String nodeKey = child.replace("[]", "");
      if (child.endsWith("[]")) {
        System.out.println(nodeKey + " is a collection");
        System.out.println("parent is:" + jsonObject.getClass());
        
        if (jsonObject instanceof Map) {
          if (!((Map<String, Object>) jsonObject).containsKey(nodeKey)) {
            ((Map<String, Object>) jsonObject).put(nodeKey, new ArrayList<>());
          } 
          performInsertion(key.replace(nodeKey + "[].", ""), value,
              ((Map<String, Object>) jsonObject).get(nodeKey));
        } else if (jsonObject instanceof ArrayList) {
          int currentSize = ((ArrayList) jsonObject).size();
          ((ArrayList) jsonObject).add(new ArrayList<>());
          performInsertion(key.replace(nodeKey + "[].", ""), value,
              ((ArrayList) jsonObject).get(currentSize));
        } else {
          throw new Exception("type not supported as middle node:" + jsonObject.getClass());
        }
      } else {

        if (!((Map<String, Object>) jsonObject).containsKey(nodeKey)) {
          ((Map<String, Object>) jsonObject).put(nodeKey, new HashMap<String, Object>());
        }
      }

    } else {
      System.out.println("simple value");
      System.out.println("parent is:" + jsonObject.getClass());
      if (jsonObject instanceof Map) {
        if (((Map<String, Object>) jsonObject).get(key) == null) {
          ((Map<String, Object>) jsonObject).put(key, value);
        }
      } else if (jsonObject instanceof ArrayList) {
        System.out.println("add to list:" + value);
        HashMap<String, Object> entry = new HashMap<String, Object>();
        entry.put(key, value);
        ((ArrayList) jsonObject).add(entry);
      } else {
        throw new Exception("type not supported as final node:" + jsonObject.getClass());
      }
    }

  }

  private LinkedHashMap<String, Object> getExpectedColumns(List<String> expectedColumns,
      Map<String, Object> row) {
    LinkedHashMap<String, Object> headerData = new LinkedHashMap<String, Object>();

    for (Entry<String, Object> entry : row.entrySet()) {
      if (expectedColumns.contains(entry.getKey())) {
        headerData.put(entry.getKey(), entry.getValue());
      }
    }

    return headerData;

  }

}
