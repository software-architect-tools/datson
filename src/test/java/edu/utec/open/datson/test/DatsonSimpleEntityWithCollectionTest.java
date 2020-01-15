package edu.utec.open.datson.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import edu.utec.open.datson.common.TestDataHelper;
import edu.utec.open.datson.parser.SimpleRawDataParser;
import junit.framework.TestCase;

// https://stackoverflow.com/questions/16718163/jdbctemplate-set-nested-pojo-with-beanpropertyrowmapper
public class DatsonSimpleEntityWithCollectionTest extends TestCase {

  private static final Logger logger =
      LogManager.getLogger(DatsonSimpleEntityWithCollectionTest.class);

  public List<Map<String, Object>> getData() {
    List<Map<String, Object>> rawData = new ArrayList<Map<String, Object>>();
    rawData.add(TestDataHelper.createMockedRow("category,position,businessUnit[].name", "profesor",
        "profesortc", "pregrado"));
    rawData.add(TestDataHelper.createMockedRow("category,position,businessUnit[].name", "profesor",
        "profesortc", "postgrado"));
    rawData.add(TestDataHelper.createMockedRow("category,position,businessUnit[].name", "profesor",
        "profesortc", "doctorado"));
    return rawData;
  }

  public List<Map<String, Object>> getData2() {
    List<Map<String, Object>> rawData = new ArrayList<Map<String, Object>>();
    rawData.add(TestDataHelper.createMockedRow(
        "category,position,businessUnit[].name,businessUnit[].profile[].id", "profesor",
        "profesortc", "pregrado",1));
    rawData.add(TestDataHelper.createMockedRow(
        "category,position,businessUnit[].name,businessUnit[].profile[].id", "profesor",
        "profesortc", "postgrado",2));
    rawData.add(TestDataHelper.createMockedRow(
        "category,position,businessUnit[].name,businessUnit[].profile[].id", "profesor",
        "profesortc", "doctorado",2));
    return rawData;
  }

  @Test
  public void testSimpleEntityWithCollectionDataToJson() throws Exception {
    SimpleRawDataParser dataParser = new SimpleRawDataParser();
    Object parsed = dataParser.parse(getData());

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(parsed);

    logger.info(json);

    Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

    assertEquals("profesor", JsonPath.read(document, "$.category"));
    assertEquals("profesortc", JsonPath.read(document, "$.position"));

    logger
        .info("businessUnit is " + ((Object) JsonPath.read(document, "$.businessUnit")).getClass());

    Object businessUnitObj = JsonPath.read(document, "$.businessUnit");
    assertTrue(businessUnitObj instanceof ArrayList<?>);

    List<?> businessUnit = (List<?>) businessUnitObj;

    assertEquals(3, businessUnit.size());

    assertEquals("pregrado", JsonPath.read(document, "$.businessUnit[0].name"));

  }
  
//  @Test
//  public void testSimpleEntityWithCollectionDataToJson2() throws Exception {
//    SimpleRawDataParser dataParser = new SimpleRawDataParser();
//    Object parsed = dataParser.parse(getData2());
//
//    ObjectMapper mapper = new ObjectMapper();
//    String json = mapper.writeValueAsString(parsed);
//
//    logger.info(json);
//
//    Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
//
//    assertEquals("profesor", JsonPath.read(document, "$.category"));
//    assertEquals("profesortc", JsonPath.read(document, "$.position"));
//
//    logger
//        .info("businessUnit is " + ((Object) JsonPath.read(document, "$.businessUnit")).getClass());
//
//    Object businessUnitObj = JsonPath.read(document, "$.businessUnit");
//    assertTrue(businessUnitObj instanceof ArrayList<?>);
//
//    List<?> businessUnit = (List<?>) businessUnitObj;
//
//    assertEquals(3, businessUnit.size());
//
//    assertEquals("pregrado", JsonPath.read(document, "$.businessUnit[0].name"));
//
//  }


}
