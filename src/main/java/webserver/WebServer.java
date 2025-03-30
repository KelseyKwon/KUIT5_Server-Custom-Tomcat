package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer {
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_THREAD_NUM = 50;
    private static final Logger log = Logger.getLogger(WebServer.class.getName());

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        // 최대 50개의 클라이언트를 동시에 처리 가능.
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // TCP 환영 소켓 (지정 포트에서 클라이언트 연결 대기)
        try (ServerSocket welcomeSocket = new ServerSocket(port)){

            // 연결 소켓
            Socket connection;
            // connection이 null이 아닐 때 까지, (클라이언트 연결을 수락할 수 있을때 까지)
            while ((connection = welcomeSocket.accept()) != null) {
                // 각 연결을 RequestHandler 스레드에 전달하여 처리.
                service.submit(new RequestHandler(connection));
            }
        }

    }
}
