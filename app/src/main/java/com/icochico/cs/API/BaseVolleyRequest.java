package com.t2ksports.wwe2k16cs.API;


import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by janusz on 10/3/15.
 * Subclass of the Volley Request class, contains its required methods.
 */
public class BaseVolleyRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private  String user;
    private String pass;
    public BaseVolleyRequest(String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(0, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    public BaseVolleyRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener,  String user, String pass) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.user = user;
        this.pass = pass;
    }


    public BaseVolleyRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(
                    response,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();
        if(user!=null && pass !=null){
            String creds = String.format("%s:%s",this.user,this.pass);
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
            params.put("Authorization", auth);

        }
        return params;
    }

}