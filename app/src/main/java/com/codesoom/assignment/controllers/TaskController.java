package com.codesoom.assignment.controllers;

import com.codesoom.assignment.services.TaskManager;
import com.codesoom.assignment.utils.HttpStatus;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class TaskController implements HttpHandler {

    private final TaskManager taskManager = new TaskManager();
    private final GetRouter getRouter = new GetRouter(taskManager);
    private final PostRouter postRouter = new PostRouter(taskManager);
    private final PutRouter putRouter = new PutRouter(taskManager);
    private final DeleteRouter deleteRouter = new DeleteRouter(taskManager);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = getReqMethod(exchange);
        String path = getReqPath(exchange);
        String body = getReqBody(exchange);

        switch (method) {
            case "GET":
                this.getRouter.route(exchange, path);
                break;
            case "POST":
                this.postRouter.route(exchange, path, body);
                break;
            case "PUT":
            case "PATCH":
                this.putRouter.route(exchange, path, body);
                break;
            case "DELETE":
                this.deleteRouter.route(exchange, path);
                break;
            default:
                TaskController.sendResponse(exchange, HttpStatus.BAD_REQUEST.statusCode(), "This request can not be properly handled");
        }
    }


    private String getReqMethod(@NotNull HttpExchange exchange) {
        return exchange.getRequestMethod();
    }

    private String getReqPath(@NotNull HttpExchange exchange) {
        return exchange.getRequestURI().getPath();
    }

    private String getReqBody(@NotNull HttpExchange exchange) {
        InputStream inputStream = exchange.getRequestBody();
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }


    public static void sendResponse(@NotNull HttpExchange exchange, Integer statusCode, @NotNull String content) {
        try {
            // Response Header
            exchange.sendResponseHeaders(statusCode, content.getBytes().length);

            // Response Body
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(content.getBytes());

            // Send and Close
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}