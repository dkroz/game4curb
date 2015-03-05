package com.dkroz.game;

import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

/**
 * Created by dkroz on 3/5/15.
 */
public class MyHttpClient {
    private static final String TAG = "MyHttpClient";
    private static DefaultHttpClient httpClient = null;

    private MyHttpClient() {

    }

    /**
     * Get single instance of HttpClient object
     *
     * @return HttpClient
     */
    public static HttpClient getInstance() {
        if (httpClient == null) {
            httpClient = getSecuredHttpClient();
        }
        return httpClient;
    }

    private static DefaultHttpClient getSecuredHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUserAgent(params, "android");
            ConnManagerParams.setTimeout(params, 10000);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            Log.e(TAG, "Secured HttpClient was NOT initialized! ", e);
            return new DefaultHttpClient();
        }
    }

    /**
     * Close unused/expired connections
     */
    public static void close() {
        if (httpClient != null) {
            try {
                httpClient.getConnectionManager().closeIdleConnections(1, TimeUnit.SECONDS);
                httpClient = null;
            } catch (Exception e) {
                Log.e(TAG, "Unable to close unused connection: ", e);
            }
        }
    }

    /**
     * Close unused/expired connections
     */
    public static void destroy() {
        if (httpClient != null) {
            try {
                httpClient.getConnectionManager().shutdown();
                httpClient = null;
            } catch (Exception e) {
                Log.e(TAG, "Unable to close all connections: ", e);
            }
        }
    }

}