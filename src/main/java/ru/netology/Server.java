package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class Server {
    final int DEFAULT_THREAD_POOL = 64;
    private final int threadPoolCount;
    final int BUFFER_SIZE = 256;

    public Server() {
        this.threadPoolCount = DEFAULT_THREAD_POOL;
    }

    public Server(int threadPool) {
        this.threadPoolCount = threadPool;
    }

    private final ConcurrentHashMap<HttpMethod, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen(int port) throws IOException {
        var threadPool = Executors.newFixedThreadPool(threadPoolCount);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            try {
                var socket = serverSocket.accept();
                threadPool.submit(() -> processRequest(socket));
            } catch (IOException e) {
                System.out.println("ошибка : " + e);
            }
        }
    }

    public void processRequest(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            var buffer = CharBuffer.allocate(BUFFER_SIZE);
            var builder = new StringBuilder();

            while (in.ready()) {
                in.read(buffer);
                buffer.flip();
                builder.append(buffer);
                buffer.clear();
            }
            Request request = new Request(builder.toString());

            ConcurrentHashMap<String, Handler> methodMap = handlers.get(request.getMethod());
            if (methodMap != null) {
                String url = request.getUrl().split("\\?", 2)[0];
                Handler handler = methodMap.get(url);
                if (handler != null) {
                    handler.handle(request, out);
                    return;
                }else{
                    answer405(out);
                }
            }
        } catch (IOException e) {
            System.out.println("ошибка : " + e);
        }
    }

    private void answer405(BufferedOutputStream out) {
        var response = new Response();
        response.setStatusCode(405);
        response.setStatus("Ошибка метода,метод не найден");
        response.setBody("<html><body><h1>Методо не наден</h1></body><html>".getBytes(StandardCharsets.UTF_8));
        try {
            out.write(response.message());
            out.write(response.getBody());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHandler(HttpMethod method, String mes, Handler handler) {
        var methodMap = handlers.get(method);
        if (methodMap == null) {
            methodMap = new ConcurrentHashMap<>();
            handlers.put(method, methodMap);
        }
        methodMap.put(mes, handler);
    }
}