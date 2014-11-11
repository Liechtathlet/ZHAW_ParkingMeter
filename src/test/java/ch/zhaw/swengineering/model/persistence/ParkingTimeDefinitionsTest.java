package ch.zhaw.swengineering.model.persistence;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class ParkingTimeDefinitionsTest extends TestCase {
    public void testParkingTimeDefinitionsAreOrdered() throws Exception {
        ParkingTimeDefinition definition1 = new ParkingTimeDefinition();
        definition1.setOrderId(3);
        ParkingTimeDefinition definition2 = new ParkingTimeDefinition();
        definition2.setOrderId(1);
        ParkingTimeDefinition definition3 = new ParkingTimeDefinition();
        definition3.setOrderId(2);

        List<ParkingTimeDefinition> definitions = new ArrayList<>();
        definitions.add(definition1);
        definitions.add(definition2);
        definitions.add(definition3);

        ParkingTimeDefinitions parkingTimeDefinitions = new ParkingTimeDefinitions();
        parkingTimeDefinitions.setParkingTimeDefinitions(definitions);

        List<ParkingTimeDefinition> actualDefinitions =
                parkingTimeDefinitions.getParkingTimeDefinitions();

        assertEquals(definition2, actualDefinitions.get(0));
        assertEquals(definition3, actualDefinitions.get(1));
        assertEquals(definition1, actualDefinitions.get(2));
    }
}