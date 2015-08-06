package com.github.ruediste1.i18n.message;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class TMessagePatternExtractionUtilTest {

    @TMessages
    private interface A {
        void foo();
    }

    @Test
    public void test() {
        Map<String, String> patterns = TMessagePatternExtractionUtil
                .getPatterns(A.class);
        assertEquals(1, patterns.size());
        assertEquals("Foo.", patterns.get(A.class.getName() + ".foo"));
    }
}
