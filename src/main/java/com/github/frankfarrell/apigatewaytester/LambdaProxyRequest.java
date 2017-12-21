package com.github.frankfarrell.apigatewaytester;

import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by frankfarrell on 19/12/2017.
 *
 * Converts REST request to lambda format for testing purposes
 */
public class LambdaProxyRequest {

    public final RequestStreamHandler proxyHandler;


    private ByteArrayOutputStream outputStream;
    private final ObjectMapper objectMapper;

    public LambdaProxyRequest(final RequestStreamHandler proxyHandler) {
        this.proxyHandler = proxyHandler;
        this.objectMapper = new ObjectMapper();
    }

    public void doRequest(final String jsonBody,
                          final Map<String, String> headers,
                          final String requestMethod,
                          final String url,
                          final Map<String, String> queryParams,
                          final Context context) throws IOException {

        final AwsProxyRequest request = new AwsProxyRequest();
        request.setPath(url);
        request.setHttpMethod(requestMethod);
        request.setBody(jsonBody);
        request.setQueryStringParameters(queryParams);
        request.setHeaders(headers);

        InputStream inputStream = new ByteArrayInputStream(objectMapper.writeValueAsBytes(request));
        outputStream = new ByteArrayOutputStream();

        proxyHandler.handleRequest(inputStream, outputStream, context);
    }

    public String getResponse(){
        byte[] byteArray = outputStream.toByteArray();

        return new String(byteArray);

        //TODO Use json path on this to pull out the interesting stuff
    }
}
