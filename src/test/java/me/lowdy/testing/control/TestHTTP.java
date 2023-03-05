package me.lowdy.testing.control;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TestHTTP {
    @Test
    public void test() throws IOException {
        int port = 800;
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Listening on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            String inputLine;
            String path = null;
            System.out.println("Client connected");
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("GET")) {
                    String[] tokens = inputLine.split(" ");
                    path = tokens[1];
                }
                if (inputLine.equals("")) break;
                System.out.println("    " + inputLine);
            }
            if (Objects.equals(path, "/")) {
                System.out.println("Got main page response");
                out.write(("HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Server: SimpleHttpServer\r\n" +
                        "\r\n" +
                        "<html><head>\r\n" +
                        "<title>We got contact</title>\r\n" +
                        "</head><body>\r\n" +
                        "<h1>Hi.</h1>\r\n" +
                        "<p>This is some kind of a main page</p>\r\n" +
                        "</body></html>\r\n").getBytes());
                in.close();
                out.close();
                clientSocket.close();
                continue;
            }
            Path filePath = Paths.get("C:" + path);
            if (Files.isDirectory(filePath) || !Files.exists(filePath)) {
                System.out.println("Got not found response");
                out.write(("HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Server: SimpleHttpServer\r\n" +
                        "\r\n" +
                        "<html><head>\r\n" +
                        "<title>We got contact</title>\r\n" +
                        "</head><body>\r\n" +
                        "<h1>Hi.</h1>\r\n" +
                        "<p>There is no file on C:" + path + "</p>\r\n" +
                        "</body></html>\r\n").getBytes());
                in.close();
                out.close();
                clientSocket.close();
                continue;
            }
            System.out.println("Got file response");
            out.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/octet-stream\r\n" +
                    "Content-Disposition: attachment; filename=\"" + filePath.getFileName() + "\"\r\n" +
                    "Server: SimpleHttpServer\r\n" +
                    "\r\n").getBytes());
            byte[] fileBytes = Files.readAllBytes(filePath);
            out.write(fileBytes, 0, fileBytes.length);
            in.close();
            out.close();
            clientSocket.close();
        }
    }
}
