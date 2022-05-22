package ru.netology;

import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class Request {
    private final static String DELIMITER = "\r\n\r\n";
    private final static String NEW_LINE = "\r\n";
    private final static String HEADER_DELIMITER = ":";
    private final String url;
    private final HttpMethod method;
    private final String message;
    private final String body;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, Set<String>> params;

    public Request(String message) {
        this.message = message;

        String[] parts = message.split(DELIMITER);
        String head = parts[0];
        String[] headers = head.split(NEW_LINE);
        String[] firstLine = headers[0].split(" ");

        this.method = HttpMethod.valueOf(firstLine[0]);
        this.path = firstLine[1];
        this.url = this.path.split("\\?", 2)[0];

        this.headers = Collections.unmodifiableMap(
                new HashMap<>() {{
                    for (int i = 1; i < headers.length; i++) {
                        String[] header = headers[i].split(HEADER_DELIMITER, 2);
                        put(header[0].trim(), header[1].trim());
                    }
                }}
        );
        String bodyLength = this.headers.get("Content-Length");
        int length = bodyLength != null ? Integer.parseInt(bodyLength) : 0;
        this.body = parts.length > 1 ? parts[1].trim().substring(0, length) : "";

        if (this.method == HttpMethod.GET) {
            this.params = Collections.unmodifiableMap(
                    new HashMap<>() {{
                        List<NameValuePair> queparam;
                        try {
                            queparam = URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        queparam.forEach(nameValuePair -> {
                            String name = nameValuePair.getName();
                            String value = nameValuePair.getValue();
                            Set<String> valus = get(name);
                            if (valus == null) {
                                valus = new HashSet<>();
                            }
                            valus.add(value);
                            put(name, valus);
                        });
                    }}
            );
        } else if (this.method == HttpMethod.POST) {
            this.params = new HashMap<>();
        } else {
            this.params = new HashMap<>();
        }
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getMessage() {
        return message;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Set<String> getQueryParam(String name) {
        return params.get(name);
    }
    public Map<String, Set<String>> getQueryParams() {
        return params;
    }
}
