package com.vtence.molecule.simple;

import com.vtence.molecule.Application;
import com.vtence.molecule.Server;
import com.vtence.molecule.util.Charsets;
import com.vtence.molecule.util.FailureReporter;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

public class SimpleServer implements Server {

    private final int port;

    private FailureReporter failureReporter = FailureReporter.IGNORE;
    private Charset defaultCharset = Charsets.ISO_8859_1;

    private Connection connection;

    public SimpleServer(int port) {
        this.port = port;
    }

    public void reportErrorsTo(FailureReporter reporter) {
        this.failureReporter = reporter;
    }

    public void defaultCharset(Charset charset) {
        defaultCharset = charset;
    }

    public int port() {
        return port;
    }

    public void run(final Application app) throws IOException {
        connection = new SocketConnection(new ApplicationContainer(app));
        SocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
    }

    public void shutdown() throws IOException {
        if (connection != null) connection.close();
    }

    public class ApplicationContainer implements Container {
        private final Application app;

        public ApplicationContainer(Application app) {
            this.app = app;
        }

        public void handle(Request request, Response response) {
            try {
                app.handle(new SimpleRequest(request), new SimpleResponse(response, defaultCharset));
            } catch (Exception failure) {
                failureReporter.errorOccurred(failure);
            } finally {
                close(response);
            }
        }

        private void close(Response response) {
            try {
                response.close();
            } catch (IOException e) {
                failureReporter.errorOccurred(e);
            }
        }
    }
}