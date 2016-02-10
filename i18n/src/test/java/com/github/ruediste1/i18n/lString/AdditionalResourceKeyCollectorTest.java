package com.github.ruediste1.i18n.lString;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class AdditionalResourceKeyCollectorTest {

    private static class TestA implements AdditionalResourceKeyProvider {

        @Override
        public void provideKeys(KeyReceiver receiver) {
            receiver.add("test", "value");
        }

    }

    @Test
    public void testSimple() {
        Map<String, String> keys = AdditionalResourceKeyCollector
                .collectKeys(TestA.class);
        assertEquals(1, keys.size());
        assertEquals("value", keys.get("test"));
    }

    static private class TestB implements AdditionalResourceKeyProvider {

        @Override
        public void provideKeys(KeyReceiver receiver) {
            receiver.add("test1", "value1");
            receiver.addProvider(TestA.class);
        }

    }

    @Test
    public void testInclude() {
        Map<String, String> keys = AdditionalResourceKeyCollector
                .collectKeys(TestB.class);
        assertEquals(2, keys.size());
        assertEquals("value", keys.get("test"));
        assertEquals("value1", keys.get("test1"));
    }
}
