package org.glitch.dragoman.util;

import java.io.IOException;
import java.net.ServerSocket;

public class NetworkUtils {

    public static int getFreePort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get free port!", ex);
        }
    }
}
