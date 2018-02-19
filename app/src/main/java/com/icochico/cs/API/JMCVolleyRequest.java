package com.t2ksports.wwe2k16cs.API;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by janusz on 10/3/15
 * This class is used to form and send a HTTP request to the 2k games server
 */
public class JMCVolleyRequest {
    /** Log tag*/
    String TAG = "VOLLEY";
    /** URL Request*/
    private  BaseVolleyRequest request;

    /** Static class holding fields used to form  the HTTP request*/
    static class PostRequest{

        static String lineEnd = "\r\n";
        static String doubleLineEnd = "\r\n\r\n";
        static String boundary = "apiclient-" + System.currentTimeMillis();
        static String twoHyphens = "--";
        static String lineStart = lineEnd+twoHyphens+boundary+lineEnd;
        static String endofrequest = lineEnd+twoHyphens+boundary+twoHyphens+lineEnd;
        static String mimeType ="multipart/form-data; boundary="+boundary;
        static int TimeOUT = 900000;
        static String kUsername ="2k16logouser";
        static String kPassword = "cRaX7tr6rE7hewre";
    }

    /** Addtional API details goes here*/
    public enum APIKeys{
        kAddMethodName("add"),//add method
        kTokenFieldName("tok"),//token passed to the API
        kImageTypeName("content_type"), // internal to my api content-type don't confuse it with form content-type
        kImageFieldName("image"),//That would be image's field name in the form
        kEndpointURL("https://wwe.2k.com/wwe2k16logo/api.php"),
//       kEndpointURL("https://wwe-stg.2k.com/wwe2k16logo/api.php"),
//       kEndpointURL("http://izotx.com/api/"),

        kAPISuccessfuleResponse("1");
        private final String text;

        /**
         * @param text text to assign
         */
        APIKeys(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }



    /**
     * The method creates a request and uploads the image and other information about it to the 2k server
     * @param bitmap binary data of image to send
     * @param token  token that needs to be entered to upload the image (generated on the website)
     * @param contentType internal 2k logo type - don't confuse it with png/jpg type
     * @param context  context in which operation is executed
     * @param apiListener an object that will be receiving information about error/successful operation
     * */
    public BaseVolleyRequest sendFile(Bitmap bitmap, String token, String contentType, Context context, final WebAPIInterface apiListener) {
        //Prepare request

        final String mToken = token;
        final String mContentType = contentType;
        String url = APIKeys.kEndpointURL.toString();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        final byte[] bitmapData = byteArrayOutputStream.toByteArray();


        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String s = "";
                try{
                    s = new String(response.data, "UTF-8");


                }
                catch (Exception e){
                    Log.i(TAG, "Network Response Error " + e.getMessage());
                    Crashlytics.getInstance().logException(new Exception("EXCEPTION"));
                }
                finally {

                    Log.i(TAG, " Response is:  "+s);
                    if (s.equals(APIKeys.kAPISuccessfuleResponse.toString())){
                        apiListener.successfulResponse(s);
                    }
                    else{
                        apiListener.errorResponse(s);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               apiListener.errorResponse("Error. Please try again");
                if( error.networkResponse != null){
                    Log.i(TAG, "Error " + error.networkResponse.statusCode);

                }
                Log.i(TAG, "Error " + error);
                Log.i(TAG, "Error " + error.getNetworkTimeMs());
                Log.i(TAG, "Error " + error.getLocalizedMessage());
            }
        },PostRequest.kUsername, PostRequest.kPassword) {
            @Override
            public String getBodyContentType() {
                return PostRequest.mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                //Check the details of sending the post request: http://www.w3.org/TR/html401/interact/forms.html#form-data-set
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                int bytesRead, bytesAvailable, bufferSize;

                try {

                   /*Token*/
                    dos.writeBytes(PostRequest.lineStart);
                    Log.i(TAG, PostRequest.lineStart);

                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kTokenFieldName.toString()+"\"" + PostRequest.lineEnd);
                    dos.writeBytes(PostRequest.lineEnd);
                    dos.writeBytes(mToken);

                    /*Add Method*/
                    dos.writeBytes(PostRequest.lineStart);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kAddMethodName .toString()+"\"" + PostRequest.lineEnd);
                    dos.writeBytes(PostRequest.lineEnd);
                    dos.writeBytes("add");
                    Log.i(TAG, PostRequest.lineStart);
                    /*Image Type*/
                    dos.writeBytes(PostRequest.lineStart);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kImageTypeName .toString()+"\"" + PostRequest.lineEnd);
                    dos.writeBytes(PostRequest.lineEnd);
                    dos.writeBytes(mContentType);
                    Log.i(TAG, PostRequest.lineStart);
                   /*Image File*/
                    dos.writeBytes(PostRequest.lineStart);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kImageFieldName.toString()+"\";filename=\""
                            + "wwwe2kandroid.png" + "\"" + PostRequest.lineEnd);
                    dos.writeBytes("Content-Type: image/png");
                    dos.writeBytes(PostRequest.lineEnd);
                    dos.writeBytes(PostRequest.lineEnd);

                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }


                    dos.writeBytes(PostRequest.endofrequest);

                    return bos.toByteArray();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapData;
            }
        };
        baseVolleyRequest.setRetryPolicy(new DefaultRetryPolicy(
                        PostRequest.TimeOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)


        );

        Volley.newRequestQueue(context).add(baseVolleyRequest);
        request = baseVolleyRequest;
        return baseVolleyRequest;
    }

    public void cancel(){
            if (request != null) {
                request.cancel();
            }
    }
}