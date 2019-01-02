package com.sixtey7.fjservice;

import java.io.IOException;
import java.util.logging.LogManager;

import io.helidon.microprofile.server.Server;


public final class Main {
    private Main() { }

    public static void main(final String[] args) throws IOException {
        setupLogging();

        Server server = startServer();

        System.out.println("http://localhost:" + server.getPort());
    }

    static Server startServer() {
        // Server will automatically pick up configuration from
        // microprofile-config.properties
        // and Application classes annotated as @ApplicationScoped
        return Server.create().start();
    }

    private static void setupLogging() throws IOException {
        //load logging configuration
        LogManager.getLogManager().readConfiguration(
            Main.class.getResourceAsStream("/logging.properties")
        );
    }
}