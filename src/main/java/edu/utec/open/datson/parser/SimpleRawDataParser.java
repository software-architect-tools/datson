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
        System.out.println("perform before:"+parsedRow);
        performInsertion(entry.getKey(), entry.getValue(), parsedRow);
        System.out.println("perform after:"+parsedRow);
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
    
    // if key has more or eq than one dot
    // it means a inner key
    if (count >= 1) {
      //get child of key to create one  by one
      String[] nodesOfTheKey = key.split("\\.");
      
      Map<String, Object> finalNode = null;
      for (int a = 0; a < nodesOfTheKey.length - 1; a++) {
        String child = nodesOfTheKey[a];//get first node key
        if (parsedRow.get(child) == null) {
          //create it if not exist
          parsedRow.put(child, new HashMap<String, Object>());
        }
        //get this node (created or existing)
        finalNode = (Map<String, Object>) parsedRow.get(child);
        //this node will be the current node int the next iteration
        parsedRow = finalNode;
        //at the end of iteration, final node will be filled with simple value
      }
      
      //put the simple value in the latest node
      //nodesOfTheKey has the keys. We need the lastest key
      //latest key is the length -1 index
      finalNode.put(nodesOfTheKey[nodesOfTheKey.length-1], value);
      
    } else {
      //final node was found
      //this key does not have dots, it is a simple key
      parsedRow.put(key, value);
    }
  }  
  
  
  

}
