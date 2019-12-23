package de.daikol.motivator.util;

import android.util.Log;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ChallengerBadRequestHandler implements ResponseErrorHandler {
    private static final String LOG_TAG = "ChallengerBadRequest";

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        Log.e(LOG_TAG, "Response error: {" + response.getStatusCode().toString() + "} {" + response.getStatusText() + "}");
        Log.e(LOG_TAG, "Response headers: {" + response.getHeaders().toString() + "}");
        Log.e(LOG_TAG, "Response body: {" + convertStreamToString(response.getBody()) + "}");
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode() == HttpStatus.BAD_REQUEST;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}