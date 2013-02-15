package com.vtence.molecule.middlewares;

import com.vtence.molecule.Request;
import com.vtence.molecule.Response;

import javax.sql.DataSource;
import java.sql.Connection;

public class ConnectionScope extends AbstractMiddleware {

    private final DataSource dataSource;

    public ConnectionScope(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void handle(Request request, Response response) throws Exception {
        Connection connection = dataSource.getConnection();
        Reference ref = new Reference(request);

        ref.set(connection);
        try {
            forward(request, response);
        } finally {
            ref.unset();
            connection.close();
        }
    }

    public static class Reference {
        private final Request request;

        public Reference(Request request) {
            this.request = request;
        }

        public Connection get() {
            return (Connection) request.attribute(Connection.class);
        }

        private void set(Connection connection) {
            request.attribute(Connection.class, connection);
        }

        public void unset() {
            request.removeAttribute(Connection.class);
        }
    }
}
