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
public class DatsonOneLevelTest extends TestCase {

  private static final Logger logger = LogManager.getLogger(DatsonOneLevelTest.class);
  
  public List<Map<String, Object>> getOneLevelData() {
    List<Map<String, Object>> rawData = new ArrayList<Map<String, Object>>();
    rawData.add(TestDataHelper.createMockedRow("id, name, job", 100, "jack", "developer"));
    rawData.add(TestDataHelper.createMockedRow("id, name, job", 101, "senku", "scientist"));
    rawData.add(TestDataHelper.createMockedRow("id, name, job", 102, "richard", "anunaki"));
    return rawData;
  }

  @Test
  public void testOneLevelDataToJson() throws Exception {
    SimpleRawDataParser dataParser = new SimpleRawDataParser();
    Object parsed = dataParser.parseCollection(getOneLevelData());

    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(parsed);
    
    logger.info(json);

    Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

    assertEquals(100, ((Integer)(JsonPath.read(document, "$.[0].id"))).intValue());
    assertEquals(101, ((Integer)(JsonPath.read(document, "$.[1].id"))).intValue());
    assertEquals(102, ((Integer)(JsonPath.read(document, "$.[2].id"))).intValue());

    assertEquals("senku", JsonPath.read(document, "$.[1].name"));
  }


}
