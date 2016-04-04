package com.example.test.studentdemo.network;


/**
 * Created by gaurav_bhatnagar on 4/4/2016.
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.example.test.studentdemo.R;
import com.example.test.studentdemo.StudentDemoApplication;


@SuppressWarnings("ConstantConditions")
public class NetworkUtils {
    private static final String EMPTY_STRING = "";
    private static final String TAG = "Network.Utils";
    private static final String GET_PARAMS_START = "?";
    private static final String GET_PARAMS_DIVIDER = "&";
    private static final String GET_PARAMS_KEY_VALUE_DIVIDER = "=";

    /**********************************************************************/
    /**
     * Executes HTTP GET without parameters.
     *
     * @param url
     *            Destination URL.
     * @return response string.
     * @throws IOException
     *
     *************************************************************************/
    public static String get(final String url) throws IOException {
        Log.d(TAG, "get(" + url + ")");
        HttpRequestBase request = new HttpGet(url);
        return executeRequest(request);
    }

    /**********************************************************************/
    /**
     * Executes HTTP GET with custom header.
     *
     * @param url
     *            Destination URL.
     * @param header
     *            List contains custom fields to be added in header.
     * @param params
     *            HTTP params to be added. Pass null if no params required.
     * @return response string.
     * @throws IOException
     *************************************************************************/
    public static String get(final String url, final List<NameValuePair> header,
                             final List<NameValuePair> params) throws IOException {
        HttpRequestBase request = formGetWithCustomHeader(url, header,
                params);
        return (null == request ? EMPTY_STRING : executeRequest(request));
    }

    public byte[] getData(final String url, final List<NameValuePair> header,
                          final List<NameValuePair> params) throws IOException {
        HttpRequestBase request = formGetWithCustomHeader(url, header,
                params);
        return (null == request ? null : executeRequestData(request));
    }


    private static String executeRequest(final HttpRequestBase request)
            throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        String responseString;
        StatusLine statusLine;

        if (null == request) {
            return null;
        }

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        request.setParams(params);

        try {
            response = httpclient.execute(request);
        } catch (Exception ex) {
            if (ex.getMessage() != null){
                throw new IOExceptionWithContent(ex.getMessage(), null);
            }
            else if (ex.getLocalizedMessage() != null) {
                throw new IOExceptionWithContent(ex.getLocalizedMessage(), ex.getLocalizedMessage());
            }
        }

        statusLine = response.getStatusLine();
        if (response.getEntity() == null) {
            if (statusLine.getStatusCode()== HttpStatus.SC_NO_CONTENT){
                if (request.getMethod().equalsIgnoreCase("DELETE")){
                    return "RECORD IS DELETED";
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }

        if (statusLine.getStatusCode() < HttpStatus.SC_BAD_REQUEST) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            responseString = out.toString();
        } else {
            // Closes the connection.
            if (statusLine.getStatusCode() == HttpStatus.SC_BAD_REQUEST){
                responseString =  EntityUtils.toString(response.getEntity(),"UTF-8");
                if (null == responseString || responseString.length() == 0) {
                    throw new IOException(statusLine.getReasonPhrase());
                } else {
                    throw new IOExceptionWithContent(statusLine.getReasonPhrase(), responseString);
                }
            }
            else{
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                if (null == responseString || responseString.length() == 0) {
                    throw new IOException(statusLine.getReasonPhrase());
                } else {
                    throw new IOExceptionWithContent(statusLine.getReasonPhrase(), responseString);
                }
            }
        }

        return responseString;
    }

    private static byte[] executeRequestData(final HttpRequestBase request)
            throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response = null;
        byte[] responseBytes = null;
        String responseString;
        StatusLine statusLine;

        if (null == request) {
            return null;
        }

        if (!isNetworkAvailable()) {
            throw new IOExceptionWithContent(
                    StudentDemoApplication.getInstance().getString(
                            R.string.no_internet_connection),
                    StudentDemoApplication.getInstance().getString(
                            R.string.no_internet_connection));
        }

        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 30000);
        HttpConnectionParams.setSoTimeout(params, 30000);
        request.setParams(params);
        response = httpclient.execute(request);
        statusLine = response.getStatusLine();

        if (response.getEntity() == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.getEntity().writeTo(out);
        if (statusLine.getStatusCode() < HttpStatus.SC_BAD_REQUEST) {
            responseBytes = out.toByteArray();
        }
        out.close();

        if (responseBytes == null) {
            responseString = out.toString();
            if (null == responseString || responseString.length() == 0) {
                throw new IOException(statusLine.getReasonPhrase());
            } else {
                throw new IOExceptionWithContent(
                        statusLine.getReasonPhrase(), responseString);
            }
        }

        return responseBytes;
    }

    /**********************************************************************/
    /**
     * Creates HTTP POST request with given parameters.
     *
     * @param url
     *            Destination URL.
     * @param params
     *            Set of parameters to be added to request.
     * @return HTTP POST request on success or null if failed.
     * @throws UnsupportedEncodingException
     *************************************************************************/
    private static HttpPost formPostRequest(String url,
                                            List<NameValuePair> params) throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(url);
        request.setEntity(new UrlEncodedFormEntity(params,
                HTTP.UTF_8));
        return request;
    }

    /**********************************************************************/
    /**
     * Creates HTTP POST request with custom header and content.
     *
     * @param url
     *            Destination URL.
     * @param header
     *            List contains custom fields to be added in header.
     * @return HTTP POST request on success or null if failed.
     * @throws Exception
     *************************************************************************/
    private static HttpPost formPostWithCustomHeader(final String url,
                                                     final List<NameValuePair> header) throws Exception {
        HttpPost request = null;

        URI uriUrl = new URI(url);
        request = new HttpPost(uriUrl.toASCIIString());
        for (NameValuePair pair : header) {
            request.setHeader(pair.getName(), pair.getValue());
        }

        return request;
    }

    /**********************************************************************/
    /**
     * Creates HTTP POST request with custom header and content.
     *
     * @param url
     *            Destination URL.
     * @param header
     *            List contains custom fields to be added in header.
     * @param params
     *            HTTP params to be added. Pass null if no params required.
     * @return HTTP POST request on success or null if failed.
     * @throws UnsupportedEncodingException
     *************************************************************************/
    private static HttpPost formPostWithCustomHeader(final String url,
                                                     final List<NameValuePair> header, final List<NameValuePair> params)
            throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(url);

        for (NameValuePair pair : header) {
            request.setHeader(pair.getName(), pair.getValue());
        }

        if (null != params) {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }

        return request;

    }

    /**********************************************************************/
    /**
     * Creates HTTP GET request with custom header.
     *
     * @param url
     *            Destination URL.
     * @param header
     *            List contains custom fields to be added in header.
     * @params HTTP params to be added. Pass null if no params required.
     * @return HTTP GET request on success or null if failed.
     * @throws UnsupportedEncodingException
     *************************************************************************/
    private static HttpGet formGetWithCustomHeader(String url,
                                                   final List<NameValuePair> header, final List<NameValuePair> params)
            throws UnsupportedEncodingException {

        if (null != params && params.size() > 0) {
            NameValuePair pair = params.get(0);
            url += (GET_PARAMS_START + pair.getName()
                    + GET_PARAMS_KEY_VALUE_DIVIDER + URLEncoder.encode(pair
                    .getValue(), "UTF-8"));
            for (int i = 1; i < params.size(); ++i) {
                pair = params.get(i);
                url += GET_PARAMS_DIVIDER
                        + pair.getName()
                        + GET_PARAMS_KEY_VALUE_DIVIDER
                        + URLEncoder
                        .encode(pair.getValue(), "UTF-8");
            }
        }

        HttpGet request = null;
        try {
            URI uriUrl = new URI(url);
            request = new HttpGet(uriUrl.toASCIIString());
            if (null != header) {
                for (NameValuePair pair : header) {
                    request.setHeader(pair.getName(), pair.getValue());
                }
            }
        } catch (Exception e) {
            Log.wtf(TAG,e);
        }

        return request;
    }

    /**********************************************************************/
    /**
     * Creates HTTP PUT request with custom header.
     *
     * @param url
     *            Destination URL.
     * @param header
     *            List contains custom fields to be added in header.
     * @param params
     *            HTTP params to be added. Pass null if no params required.
     * @return HTTP PUT request on success or null if failed.
     * @throws UnsupportedEncodingException
     *************************************************************************/
    private static HttpPut formPutRequest(final String url,
                                          final List<NameValuePair> header, final List<NameValuePair> params)
            throws UnsupportedEncodingException {
        HttpPut request = null;

        request = new HttpPut(url);
        if (header != null) {
            for (NameValuePair pair : header) {
                request.addHeader(pair.getName(), pair.getValue());
            }
        }

        if (null != params) {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        }

        return request;
    }

    /**********************************************************************/
    /**
     * Creates HTTP DELETE request with custom header.
     *
     * @param url
     *            Destination URL.
     * @param header
     *            List contains custom fields to be added in header.
     *
     * @return HTTP DELETE request on success or null if failed.
     * @throws UnsupportedEncodingException
     *************************************************************************/
    private static HttpDelete formDeleteRequest(final String url,
                                                final List<NameValuePair> header)
            throws UnsupportedEncodingException {
        HttpDelete request = null;

        request = new HttpDelete(url);
        if (header != null) {
            for (NameValuePair pair : header) {
                request.addHeader(pair.getName(), pair.getValue());
            }
        }

        return request;
    }
    public static boolean isNetworkAvailable() {
        return ((ConnectivityManager) StudentDemoApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null;
    }



}
