package com.inserteffect.demo.plugin;

import com.google.common.base.Charsets;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import android.test.suitebuilder.annotation.SmallTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static com.inserteffect.demo.Service.ServiceException;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class OpenWeatherMapTest {

    @Mock
    HttpURLConnection mHttpURLConnection;

    private OpenWeatherMap mOpenWeatherMap;

    @Before
    public void setUp() {
        mOpenWeatherMap = new OpenWeatherMap(new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(final URL url) throws IOException {
                return mHttpURLConnection;
            }
        });
    }

    @Test
    public void shouldThrowServiceExceptionForIOException() throws IOException {
        when(mHttpURLConnection.getInputStream()).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new IOException();
            }
        });
        try {
            mOpenWeatherMap.getData();
            Assert.fail("Exception expected.");
        } catch (ServiceException e) {
            assertEquals("Could not establish connection.", e.getMessage());
        }
    }

    @Test
    public void shouldThrowServiceExceptionForJSONParsing() throws IOException {
        when(mHttpURLConnection.getInputStream()).thenReturn(new ByteArrayInputStream("{".getBytes(Charsets.UTF_8)));
        try {
            mOpenWeatherMap.getData();
            Assert.fail("Exception expected.");
        } catch (ServiceException e) {
            assertEquals("Could not parse response.", e.getMessage());
        }
    }
}
