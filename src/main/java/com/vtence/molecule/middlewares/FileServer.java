package com.vtence.molecule.middlewares;

import com.vtence.molecule.Application;
import com.vtence.molecule.FileBody;
import com.vtence.molecule.HttpMethod;
import com.vtence.molecule.HttpStatus;
import com.vtence.molecule.Request;
import com.vtence.molecule.Response;
import com.vtence.molecule.util.HttpDate;
import com.vtence.molecule.util.Joiner;
import com.vtence.molecule.util.MimeTypes;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vtence.molecule.HttpHeaders.ALLOW;
import static com.vtence.molecule.HttpHeaders.IF_MODIFIED_SINCE;
import static com.vtence.molecule.HttpHeaders.LAST_MODIFIED;
import static com.vtence.molecule.HttpMethod.GET;
import static com.vtence.molecule.HttpMethod.HEAD;
import static com.vtence.molecule.HttpStatus.METHOD_NOT_ALLOWED;
import static com.vtence.molecule.HttpStatus.NOT_MODIFIED;

public class FileServer implements Application {

    private final File root;
    private final MimeTypes mediaTypes = MimeTypes.defaults();
    private final Map<String, String> headers = new HashMap<String, String>();

    private static final List<HttpMethod> ALLOWED_METHODS = Arrays.asList(GET, HEAD);
    private static final String ALLOW_HEADER = Joiner.on(", ").join(ALLOWED_METHODS);

    public FileServer(File root) {
        this.root = root;
    }

    public void registerMediaType(String extension, String mediaType) {
        mediaTypes.map(extension, mediaType);
    }

    public FileServer addHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public void handle(Request request, Response response) throws Exception {
        if (!methodAllowed(request)) {
            response.header(ALLOW, ALLOW_HEADER);
            response.status(METHOD_NOT_ALLOWED);
            return;
        }

        File file = new File(root, request.pathInfo());
        if (!canServe(file)) {
            response.status(HttpStatus.NOT_FOUND);
            response.contentType(MimeTypes.TEXT);
            response.body("File not found: " + request.pathInfo());
            return;
        }

        if (notModifiedSince(request, file)) {
            response.status(NOT_MODIFIED);
            return;
        }

        addFileHeaders(response, file);
        addCustomHeaders(response);

        response.status(HttpStatus.OK);
        if (head(request)) return;

        response.body(new FileBody(file));
    }

    private boolean canServe(File file) {
        return file.exists() && file.canRead() && !file.isDirectory();
    }

    private boolean methodAllowed(Request request) {
        return ALLOWED_METHODS.contains(request.method());
    }

    private boolean notModifiedSince(Request request, File file) {
        return HttpDate.format(file.lastModified()).equals(request.header(IF_MODIFIED_SINCE));
    }

    private void addFileHeaders(Response response, File file) {
        response.contentType(mediaTypes.guessFrom(file.getName()));
        response.headerDate(LAST_MODIFIED, file.lastModified());
        response.contentLength(file.length());
    }

    private void addCustomHeaders(Response response) {
        for (String header : headers.keySet()) {
            response.header(header, headers.get(header));
        }
    }

    private boolean head(Request request) {
        return request.method() == HEAD;
    }
}
