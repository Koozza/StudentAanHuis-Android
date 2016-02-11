package com.thijsdev.studentaanhuis;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.api.client.http.HttpStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class HttpClientClass {
    private HttpClientObject httpClientObject = new HttpClientObject();
    final private HttpClientClass self = this;

    public HttpClientClass() {
    }

    public void retryLastCall() {
        httpClientObject.addAttempt();
        if(httpClientObject.getType() == HttpClientObject.GET) {
            new getSource().execute(httpClientObject);
        }else{
            new doPost().execute(httpClientObject);
        }
    }

    public void getSource(JSONObject obj, Callback success, Callback failed) {
        httpClientObject.setArguments(obj);
        httpClientObject.setSuccess(success);
        httpClientObject.setFailed(failed);
        httpClientObject.setAttempt(0);
        httpClientObject.setType(HttpClientObject.GET);

        new getSource().execute(httpClientObject);
    }

    public void doPost(JSONObject obj, Callback success, Callback failed) {
        httpClientObject.setArguments(obj);
        httpClientObject.setSuccess(success);
        httpClientObject.setFailed(failed);
        httpClientObject.setAttempt(0);
        httpClientObject.setType(HttpClientObject.POST);

        new doPost().execute(httpClientObject);
    }

    public void giveFeedback(Context context, final String title, final String data) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPref.getBoolean("send_anon_data", true)) {
            //Send anon data

            try {
                JSONObject obj = new JSONObject();
                JSONObject params = new JSONObject();

                //get version info
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

                params.put("app_version", info.versionName);
                params.put("title", title);
                params.put("data", data);

                obj.put("url", "http://thijsd.nl/SAH/feedback.php");
                obj.put("params", params);

                doPost(obj, new Callback(), new DefaultCallbackFailure(context));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public HttpClientObject getHttpClientObject() {
        return httpClientObject;
    }

    private class getSource extends AsyncTask<HttpClientObject, Void, Void> {
        private String result = null;

        @Override
        protected Void doInBackground(HttpClientObject... params) {
            HttpURLConnection urlConnection = null;

            try
            {
                URL url = new URL(params[0].getArguments().getString("url"));
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setReadTimeout(20000);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setDoInput(true);

                int status = urlConnection.getResponseCode();

                if(status != HttpStatusCodes.STATUS_CODE_OK) {
                    String a = convertStreamToString(new BufferedInputStream(urlConnection.getErrorStream()));
                }

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = convertStreamToString(in);
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            if(result == null)
                httpClientObject.getFailed().onTaskCompleted(result, self);
            else
                httpClientObject.getSuccess().onTaskCompleted(result);
        }
    }


    private class doPost extends AsyncTask<HttpClientObject, Void, Void> {
        private String result = null;

        @Override
        protected Void doInBackground(HttpClientObject... params) {
            HttpURLConnection urlConnection = null;
            try
            {
                URL url = new URL(params[0].getArguments().getString("url"));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(16000);
                urlConnection.setConnectTimeout(16000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params[0].getArguments().getJSONObject("params")));
                writer.flush();
                writer.close();
                os.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                result = convertStreamToString(in);
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            if(result == null)
                httpClientObject.getFailed().onTaskCompleted(result, self);
            else
                httpClientObject.getSuccess().onTaskCompleted(result);
        }
    }

    private String getQuery(JSONObject jsonObject) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> params = jsonObject.keys();
        while (params.hasNext()) {
            if (first)
                first = false;
            else
                result.append("&");

            String key = params.next();
            String value = null;
            try {
                value = jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value, "UTF-8"));
        }

        return result.toString();
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
