package http.util;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    /**
     *
     * @param br
     * socket으로부터 가져온 InputStream
     *
     * @param contentLength
     * 헤더의 Content-Length의 값이 들어와야한다.
     *
     * InputStream 처리에 필요한 BufferedReader, IOException 사용.
     * br : HTTP 요청의 입력 스트림 (Request body)
     * contentLength : HTTP 헤더의 Content-Length 값 (body 길)
     *
     * 즉, BufferedReader로부터 contentLength만큼의 데이터를 읽어들여 문자 배열로 저장하고,
     * 읽은 데이터를 문자열로 변환 후 반환한다.
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}
