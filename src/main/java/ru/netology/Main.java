package ru.netology;
import javax.imageio.IIOException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
  public static void main(String[] args) throws IOException {
    Server server = new Server();
    server.addHandler(HttpMethod.GET, "/links.html", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/spring.png", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/spring.svg", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream){
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/index.html", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/events.html", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/events.js", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/app.js", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/styles.css", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        responseOut(request, responseStream);
      }
    });
    server.addHandler(HttpMethod.GET, "/forms.html", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream){
        responseOut(request, responseStream);
      }
    });

    server.addHandler(HttpMethod.GET, "/classic.html", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        Response response = new Response();
        Path filePath = Path.of(".", "public", request.getUrl());
        try {
          String mimeType = Files.probeContentType(filePath);
          response.setMimeType(mimeType);
          String template = Files.readString(filePath, StandardCharsets.UTF_8);
          String result = template.replace("{time}", LocalDateTime.now().toString());
          response.setBody(result.getBytes(StandardCharsets.UTF_8));
          responseStream.write(response.message());
          responseStream.write(response.getBody());
          responseStream.flush();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });

    server.addHandler(HttpMethod.POST, "/forms.html", new Handler() {
      public void handle(Request request, BufferedOutputStream responseStream) {
        Response response = new Response();
        response.setMimeType("text/html; charset=utf-8");
        String bodyString = request.getBody();
        String[] loginParts = bodyString.split("&");
        String[] login = loginParts[0].split("=");
        String[] password = loginParts[1].split("=");
        if (login[0].equals("login") && login[1].equals("admin") && password[0].equals("password") && password[1].equals("1234")) {
          response.setBody("<html><body><h1>добро пожаловать</h1></body><html>".getBytes(StandardCharsets.UTF_8));
        } else {
          response.setStatusCode(403);
          response.setStatus("Forbidden");
          response.setBody("<html><body><h1>добро пожаловать</h1></body><html>".getBytes(StandardCharsets.UTF_8));
        }
        try {
          responseStream.write(response.message());
          responseStream.write(response.getBody());
          responseStream.flush();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    server.listen(9999);
  }

  static void responseOut(Request request, BufferedOutputStream responseStream){
    var response = new Response();
    final var filePath = Path.of(".","public",request.getUrl());
    try {
      final var mimeType = Files.probeContentType(filePath);
      response.setMimeType(mimeType);
      response.setBody(Files.readAllBytes(filePath));
      responseStream.write(response.message());
      responseStream.write(response.getBody());
      responseStream.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}


