package com.inserteffect.demo.plugin;

import com.inserteffect.demo.Service;
import com.inserteffect.demo.plugin.OpenWeatherMap.Parser;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import android.test.suitebuilder.annotation.SmallTest;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
@SmallTest
public class OpenWeatherMapParserTest {

    private final int mExpected;

    private final String mInput;

    public OpenWeatherMapParserTest(int expected, String input) {
        mExpected = expected;
        mInput = input;
    }

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0, ""},
                {0, "{}"},
                {0, "{\"list\":[]}"},
                {1, "{\"list\":[{\"name\":\"Bayreuth\",\"main\":{\"temp\":30.0},\"weather\":[{\"description\":\"sunny\"}]}]}"},
        });
    }

    @Test
    public void shouldParseJsonResponseFromOpenWeatherMapGroupIdsRequest() throws JSONException {
        final List<Service.Data> actual = Parser.parse(mInput);
        assertEquals(mExpected, actual.size());
        if (actual.size() > 0) {
            assertEquals("Bayreuth", actual.get(0).getTitle());
            assertEquals("30° sunny", actual.get(0).getDescription());
        }
    }
}
