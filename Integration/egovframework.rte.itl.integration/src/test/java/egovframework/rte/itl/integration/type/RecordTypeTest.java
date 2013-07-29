package egovframework.rte.itl.integration.type;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import egovframework.rte.itl.integration.message.typed.TypedMap;

@SuppressWarnings("serial")
public class RecordTypeTest
{

    private static final ListType stringListType =
        new ListType("string[]", "string[]", PrimitiveType.STRING);
    
    private static final RecordType recordTypeA =
        new RecordType("A", "A", new HashMap<String, Type>() 
        {{
            put("a", PrimitiveType.STRING);
            put("b", PrimitiveType.INTEGER);
        }});
    
    private static final RecordType recordTypeB =
        new RecordType("B", "B", new HashMap<String, Type>()
        {{
            put("c", PrimitiveType.BOOLEAN);
            put("d", stringListType);
        }});
    
    private static final RecordType recordTypeC =
        new RecordType("C", "C", new HashMap<String, Type>()
        {{
            put("e", recordTypeA);
            put("f", PrimitiveType.CALENDAR);
        }});
    
    @Test
    public void testIsAssignableFrom() throws Exception
    {
        assertTrue(recordTypeA.isAssignableFrom(TypedMap.class));
        assertTrue(recordTypeA.isAssignableFrom(Map.class));
        assertFalse(recordTypeA.isAssignableFrom(Iterable.class));
    }
    
    @Test
    public void testIsAssignableValue() throws Exception
    {
        final Map<String, Object> valueA1 = new HashMap<String, Object>()
        {{
            put("a", "valueA");
            put("b", 1);
        }};
        final Map<String, Object> valueA2 = new HashMap<String, Object>()
        {{
            put("a", "valueA");
        }};
        final Map<String, Object> valueA3 = new HashMap<String, Object>()
        {{
            put("a", "valueA");
            put("c", true);
        }};
        assertTrue(recordTypeA.isAssignableValue(valueA1));
        assertTrue(recordTypeA.isAssignableValue(valueA2));
        try
        {
            recordTypeA.isAssignableValue(valueA3);
            fail();
        }
        catch (NoSuchRecordFieldException e)
        {
        }
        
        final Map<String, Object> valueB1 = new HashMap<String, Object>()
        {{
            put("c", true);
            put("d", new String[] { "x", "y", "z" });
        }};
        final Map<String, Object> valueB2 = new HashMap<String, Object>()
        {{
            put("c", true);
            put("d", new String[] {});
        }};
        final Map<String, Object> valueB3 = new HashMap<String, Object>()
        {{
            put("c", true);
            put("d", null);
        }};
        assertTrue(recordTypeB.isAssignableValue(valueB1));
        assertTrue(recordTypeB.isAssignableValue(valueB2));
        assertTrue(recordTypeB.isAssignableValue(valueB3));
        
        final Map<String, Object> valueC1 = new HashMap<String, Object>()
        {{
            put("e", valueA1);
            put("f", Calendar.getInstance());
        }};
        final Map<String, Object> valueC2 = new HashMap<String, Object>()
        {{
            put("e", valueB1);
            put("f", Calendar.getInstance());
        }};
        assertTrue(recordTypeC.isAssignableValue(valueC1));
        try
        {
            recordTypeC.isAssignableValue(valueC2);
            fail();
        }
        catch (NoSuchRecordFieldException e)
        {
        }
    }
}
