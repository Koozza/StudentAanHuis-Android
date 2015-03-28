package com.thijsdev.studentaanhuis;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HttpClientClass {

    private static HttpClientClass instance = null;
    private HttpClient client = null;
    private static String session_id;

    public HttpClientClass() {
    }
    public static HttpClientClass getInstance() {
        if(instance == null) {
            instance = new HttpClientClass();
        }
        return instance;
    }

    public void init() {
        if(client == null)
            client = createClient();
    }

    public HttpClient getHTTPClient() {
        return this.client;
    }

    public String getSource(JSONObject obj, Callback callback) {
        new getSource(callback).execute(obj);

        return null;
    }

    public String doPost(JSONObject obj, Callback callback) {
        new doPost(callback).execute(obj);

        return null;
    }

    private DefaultHttpClient createClient()
    {
        DefaultHttpClient ret = null;

        //sets up parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.expect-continue", false);

        //registers schemes for both http and https
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        registry.register(new Scheme("https", sslSocketFactory, 443));

        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        ret = new DefaultHttpClient(manager, params);

        return ret;
    }

    private class getSource extends AsyncTask<JSONObject, Void, Void> {
        HttpResponse response;

        private OnTaskCompleted listener;
        private String result;

        public getSource(OnTaskCompleted listener){
            this.listener=listener;
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            try
            {
                HttpGet httpget = new HttpGet(params[0].getString("url"));
                response = client.execute(httpget);
                result = EntityUtils.toString(response.getEntity());
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            listener.onTaskCompleted(result);
        }
    }

    private class doPost extends AsyncTask<JSONObject, Void, Void> {
        HttpResponse response;

        private OnTaskCompleted listener;
        private String result;

        public doPost(OnTaskCompleted listener){
            this.listener=listener;
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            try
            {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                Iterator<String> iter = params[0].getJSONObject("params").keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = params[0].getJSONObject("params").getString(key);

                    nameValuePairs.add(new BasicNameValuePair(key, value));
                }

                HttpPost httppost = new HttpPost(params[0].getString("url"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = client.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            listener.onTaskCompleted(result);
        }
    }
}
