package com.inserteffect.demo.plugin;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

import com.inserteffect.demo.Service.ServiceException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.annotation.VisibleForTesting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.inserteffect.demo.Service.Data;
import static com.inserteffect.demo.Service.Provider;

/**
 * Open Weather Map provider for {@link com.inserteffect.demo.Service.Provider}.
 */
public final class OpenWeatherMap implements Provider {

    /**
     * * Current weather from Open Weather Map. See <a href="http://openweathermap.org/current">API documentation.</a>
     *
     * @param id Location ID.
     * @return List of weather data.
     * @throws ServiceException if request or response parsing fails.
     */
    @Override
    public List<Data> getData(Integer... id) throws ServiceException {

        try {

            final String response = Request.getResponse(id);
            return Parser.getData(response);

        } catch (MalformedURLException e) {
            throw new ServiceException("Malformed URL.");
        } catch (IOException e) {
            throw new ServiceException("Could not establish connection.");
        } catch (JSONException e) {
            throw new ServiceException("Could not parse response.");
        }
    }

    private final static class Request {

        private final static String getResponse(Integer... id) throws IOException {

            final String ids = Arrays.toString(id).replaceAll("[ \\[\\]]", "");
            URL url = new URL("http", "api.openweathermap.org", 80, String.format("/data/2.5/group?id=%s&units=metric&lang=de", ids));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.connect();
            final InputStream stream = connection.getInputStream();
            final String response = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
            Closeables.closeQuietly(stream);

            return response;
        }
    }

    static class Parser {

        @VisibleForTesting
        static List<Data> getData(String json) throws JSONException {

            List<Data> data = Collections.EMPTY_LIST;
            if (json != null && json.length() > 0) {
                JSONArray jsonArray = new JSONObject(json).optJSONArray("list");
                if (jsonArray != null && jsonArray.length() > 0) {
                    data = new ArrayList<Data>(jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final JSONObject jsonObject = jsonArray.optJSONObject(i);
                        data.add(getItem(jsonObject));
                    }
                }
            }
            return data;
        }

        private static Data getItem(final JSONObject jsonObject) {
            return new Data() {

                @Override
                public String getTitle() {
                    return jsonObject.optString("name");
                }

                @Override
                public String getDescription() {
                    final double temperature = jsonObject.optJSONObject("main").optDouble("temp");
                    final String sky = jsonObject.optJSONArray("weather").optJSONObject(0).optString("description");
                    return String.format("%.0f° %s", temperature, sky);
                }
            };
        }
    }
}
