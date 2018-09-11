package com.jacsstuff.quizudo.download;


import android.util.Log;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloaderImpl implements Downloader {


    @Override
    public Response getResponse(String requestUrl) {

       StringBuilder str = new StringBuilder();
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet;
        try{
            httpGet = new HttpGet(requestUrl);
        }
        catch (IllegalArgumentException e){
            return new Response(str.toString(), Response.Code.OTHER_ERROR);
        }
        Response.Code responseCode;

        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.i("DownloaderImpl", "statusCode :" + statusCode);
            switch(statusCode){
                case 200:

                    responseCode = Response.Code.SUCCESS;
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                    }
                    is.close();
                    break;
                case 404:
                    responseCode = Response.Code.NOT_FOUND;
                    break;
                case 500:
                    responseCode = Response.Code.SERVER_ERROR;
                    break;
                default:
                    responseCode = Response.Code.OTHER_ERROR;
                    statusLine.getReasonPhrase();
                    break;
            }
        } catch (Exception e) {
            return new Response("", Response.Code.SERVER_NOT_FOUND);
        }
        return new Response(str.toString(), responseCode);

    }
}
