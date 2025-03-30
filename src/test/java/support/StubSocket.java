package support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class StubSocket extends Socket {
    private final ByteArrayInputStream input;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    public StubSocket(String data) {
        this.input = new ByteArrayInputStream(data.getBytes());
    }

    @Override
    public InputStream getInputStream() {
        return input;
    }

    @Override
    public OutputStream getOutputStream() {
        return output;
    }

    public String output() {
        return output.toString();
    }
}
