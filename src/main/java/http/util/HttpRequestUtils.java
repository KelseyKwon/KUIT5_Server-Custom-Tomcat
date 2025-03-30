package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    /**
     * queryString으로 들어온 String을 &로 기준으로 잘라 queryStrings라는 배열에 저장
     * 다시 각 배열 요소마다 =을 기준으로 나누고, 전에꺼를 key로 후에꺼를 value로 queries에 저장.
     */
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
