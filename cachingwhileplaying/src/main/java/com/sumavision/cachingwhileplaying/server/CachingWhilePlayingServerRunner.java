package com.sumavision.cachingwhileplaying.server;

import java.io.IOException;

public class CachingWhilePlayingServerRunner {
    public static void run(Class serverClass) {
        try {
            executeInstance((CachingWhilePlayingNanoHTTPD) serverClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeInstance(CachingWhilePlayingNanoHTTPD server) {
        try {
            server.start();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        System.out.println("Server started, Hit Enter to stop.\n");

        try {
            System.in.read();
        } catch (Throwable ignored) {
        }

        server.stop();
        System.out.println("Server stopped.\n");
    }
}
