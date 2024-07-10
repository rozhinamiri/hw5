package utils;

import java.io.IOException;
import java.net.Socket;

public class NetworkUtil {

    public static Socket createClientSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }
}

