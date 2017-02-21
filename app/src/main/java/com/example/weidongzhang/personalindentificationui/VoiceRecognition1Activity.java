package com.example.weidongzhang.personalindentificationui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaRouter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.beast.voice.AudioWriter;
import org.beast.voice.Person;
import org.beast.voice.VoiceDBManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.beast.voice.VoiceDBManager1;
import org.beast.voice.VoicePerson;
import org.opencv.android.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by weidong.zhang on 2016/5/17.
 */
public class VoiceRecognition1Activity extends AppCompatActivity{

    public class ProfileID
    {
        public String identificationProfileId;
    }

    public class ProcessingResult{
        public String enrollmentStatus;
        public double remainingEnrollmentSpeechTime;
        public double speechTime;
        public double enrollmentSpeechTime;
    }

    public class EnrollInfo
    {
        public String status;
        public String createdDateTime;
        public String lastActionDateTime;
        public ProcessingResult processingResult;
    }

    private EnrollInfo mEI = new EnrollInfo();

    public class IIprocessingResult
    {
        public String identifiedProfileId;
        public String confidence;
    }

    public class IdentificationInfo
    {
        public String status;
        public String createdDateTime;
        public String lastActionDateTime;
        public IIprocessingResult processingResult;
    }

    public IdentificationInfo mII = new IdentificationInfo();


    private ArrayList<VoicePerson> mPersons = new ArrayList<VoicePerson>();
    private VoiceDBManager1 mgr;

    private static final String mSubscriptionKey = "d2cc3b0702624a409ee8855b86087dcd";
    private static final String HEADER_KEY = "Ocp-Apim-Subscription-Key";
    //private static final String url = "https://api.projectoxford.ai/spid/v1.0/verificationProfiles/";https://api.projectoxford.ai/spid/v1.0/identificationProfiles
    private static final String url = "https://api.projectoxford.ai/spid/v1.0/identificationProfiles/";
    //    private static final String urlEnrollment = "https://api.projectoxford.ai/spid/v1.0/identificationProfiles/49fd355e-fe45-4eae-8677-370199a971c1/enroll";
//    private static final String urlIdentification = "https://api.projectoxford.ai/spid/v1.0/identify?identificationProfileIds=49fd355e-fe45-4eae-8677-370199a971c1";
//private static final String urlEnrollment = "https://api.projectoxford.ai/spid/v1.0/identificationProfiles/86504399-bbe8-4343-9420-34a72527a953/enroll";
//    private static final String urlIdentification = "https://api.projectoxford.ai/spid/v1.0/identify?identificationProfileIds=86504399-bbe8-4343-9420-34a72527a953";
    //private static final String urlEnrollment = "https://api.projectoxford.ai/spid/v1.0/identificationProfiles/8d5ec33a-8740-4a68-a28a-c1c9679cf849/enroll";
    private static final String urlEnrollment = "https://api.projectoxford.ai/spid/v1.0/identificationProfiles/";
    //private static final String urlIdentification = "https://api.projectoxford.ai/spid/v1.0/identify?identificationProfileIds=8d5ec33a-8740-4a68-a28a-c1c9679cf849";
    private static final String urlIdentification = "https://api.projectoxford.ai/spid/v1.0/identify?identificationProfileIds=";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String FORM_DATA = "multipart/form-data";
    private static final String OCTET_STREAM = "octet-stream";
    private static final String DATA = "data";
    private static final String OPERATION_LOCATION = "Operation-Location";

    ProgressDialog mProgressDialog;

    private String sUserName;// = new String("beast");

    private static Handler imageViewerHandler = new Handler();

    ListView lv;


    public class UserInfo{
        public UserInfo(String name, String id)
        {
            mUserName = name;
            mUserID = id;
        }
        String mUserName;
        String mUserID;
    }

    private ArrayList<UserInfo> mUserList = new ArrayList<UserInfo>();

    // Background Task of Ping Speaker Recognition Service
    private class PingTask extends AsyncTask<InputStream, String, String> {

        public class ClientError {
            public String code;

            public String message;

            public UUID requestId;
        }

        public class ServiceError {
            public ClientError error;
        }

        private HttpClient mClient = new DefaultHttpClient();
        private Gson mGson = new Gson();
        @Override
        protected String doInBackground(InputStream... params)
        {
            try {
//                // Create Profile
//
//                Log.i("PingTask", "InBackground");
//                HttpPost request = new HttpPost(url);
//                System.out.println("executing request " + request.getURI());
//                request.setHeader(HEADER_KEY, mSubscriptionKey);
//                request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
//
//                request.setEntity(new ByteArrayEntity((byte[])(new String("{\"locale\":\"en-us\",}").getBytes())));
//                HttpResponse response = this.mClient.execute(request);
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
//                    return readInput(response.getEntity().getContent());
//                } else {
//                    String json = readInput(response.getEntity().getContent());
//                    if (json != null) {
//                        ServiceError error = mGson.fromJson(json, ServiceError.class);
//                        if (error != null) {
//                            //throw new ClientException(error.error);
//                            Log.i("PingTask", "Json Error!!!");
//                        }
//                    }
//
//                    Log.i("PingTask", "Json Error!!!!!!");
//                }
//                Log.i("Ping", "status code: " + statusCode);


//                // Create Enrollment
//                Log.i("PingTask", "InBackground");
//                HttpPost request = new HttpPost(urlEnrollment);
//                Log.i("PingTask", "InBackground44444");
//                System.out.println("executing request " + request.getURI());
//                request.setHeader(HEADER_KEY, mSubscriptionKey);
//                request.setHeader(CONTENT_TYPE, FORM_DATA);
//
//                //request.setEntity(new ByteArrayEntity((byte[])(new String("{\"locale\":\"en-us\",}").getBytes())));
//
//
////                File file = Environment.getExternalStorageDirectory();//获取sdcard路径
////                if(file.exists()){//判断是否存在sdcard
////                    System.out.println("sdcard file path========" + file.getAbsolutePath());
////                    File myfile = new File(file, "myfile_sdcard.txt");
////                    myfile.createNewFile();
////                    FileOutputStream fos = new FileOutputStream(myfile);
////                    fos.write("Tomorrow is anathor day".getBytes());
////                    fos.flush();
////                    fos.close();
////                }
////
////                //读取sdcard文件
////                File file1 = new File(file, "myfile_sdcard.txt");
////                InputStream inputStream = new FileInputStream(file1);
////                Reader reader = new InputStreamReader(inputStream);
////                BufferedReader bufferedReader1 = new BufferedReader(reader);
////                String iline = null;
////                while(null != (iline = bufferedReader1.readLine())){
////                    System.out.println("from sdcard myfile_sdcard.txt============" + iline);
////                }
////                bufferedReader1.close();
////                reader.close();
////                inputStream.close();
//
//                File file = Environment.getExternalStorageDirectory();
//                File file1 = new File(file, "test.wav");
//                InputStream inputStream = new FileInputStream(file1);
//
//
//                byte[] buff = new byte[8000];
//
//                int bytesRead = 0;
//
//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//
//                while((bytesRead = inputStream.read(buff)) != -1) {
//                    bao.write(buff, 0, bytesRead);
//                }
//
//                byte[] data = bao.toByteArray();
//
//                ByteArrayInputStream bin = new ByteArrayInputStream(data);
//
//                System.out.println(bin.available());
//                System.out.println("The voice file size is: " + data.length/1024./1024.);
//
//                request.setEntity(new ByteArrayEntity((byte[]) (data)));
//
//                Log.i("PingTask", "InBackground33333");
//                HttpResponse response = this.mClient.execute(request);
//                Log.i("PingTask", "InBackground22222");
//                int statusCode = response.getStatusLine().getStatusCode();
//                Log.i("PingTask", "InBackground11111");
//                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
//                    readInput(response.getEntity().getContent());
//                    //Header hd = response.getFirstHeader(OPEARATION_LOCATION);
//                    String sOpr = response.getFirstHeader(OPERATION_LOCATION).getValue();
//                    System.out.println(sOpr);
//                    HttpGet request1 = new HttpGet(sOpr);
//                    request1.setHeader(HEADER_KEY, mSubscriptionKey);
//                    HttpClient mClient1 = new DefaultHttpClient();
//                    for(int i = 0; i < 20; ++i)
//                    {
//                        HttpResponse response1 = mClient1.execute(request1);
//                        readInput(response1.getEntity().getContent());
//                    }
//                    return "";
//                } else {
//                    String json = readInput(response.getEntity().getContent());
//                    if (json != null) {
//                        ServiceError error = mGson.fromJson(json, ServiceError.class);
//                        if (error != null) {
//                            //throw new ClientException(error.error);
//                            Log.i("PingTask", "Json Error!!!");
//                        }
//                    }
//
//                    Log.i("PingTask", "Json Error!!!!!!");
//                }
//                Log.i("Ping", "status code: " + statusCode);


//                // Identification
//                System.out.println("Begin Indentify!");
//                HttpPost idtRequest = new HttpPost(urlIdentification);
//                System.out.println("executing request " + idtRequest.getURI());
//                idtRequest.setHeader(HEADER_KEY, mSubscriptionKey);
//                idtRequest.setHeader(CONTENT_TYPE, FORM_DATA);
//
//                File file = Environment.getExternalStorageDirectory();
//                File file1 = new File(file, "test.wav");
//                InputStream inputStream = new FileInputStream(file1);
//
//
//                byte[] buff = new byte[8000];
//
//                int bytesRead = 0;
//
//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//
//                while((bytesRead = inputStream.read(buff)) != -1) {
//                    bao.write(buff, 0, bytesRead);
//                }
//
//                byte[] data = bao.toByteArray();
//
//                ByteArrayInputStream bin = new ByteArrayInputStream(data);
//
//                System.out.println(bin.available());
//                System.out.println("The voice file size is: " + data.length/1024./1024.);
//
//                idtRequest.setEntity(new ByteArrayEntity((byte[]) (data)));
//
//                HttpResponse response = this.mClient.execute(idtRequest);
//                int statusCode = response.getStatusLine().getStatusCode();
//                System.out.println("Identification status code is: " + statusCode);
//                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
//                    readInput(response.getEntity().getContent());
//                    String sOpr = response.getFirstHeader(OPERATION_LOCATION).getValue();
//                    //sOpr += "sss";
//                    System.out.println(sOpr);
//                    HttpGet request1 = new HttpGet(sOpr);
//                    request1.setHeader(HEADER_KEY, mSubscriptionKey);
//                    HttpClient mClient1 = new DefaultHttpClient();
//                    for(int i = 0; i < 20; ++i)
//                    {
//                        HttpResponse response1 = mClient1.execute(request1);
//                        readInput(response1.getEntity().getContent());
//                    }
//                    return "";
//                }else
//                {
//                    String json = readInput(response.getEntity().getContent());
//                    if (json != null) {
//                        ServiceError error = mGson.fromJson(json, ServiceError.class);
//                        if (error != null) {
//                            //throw new ClientException(error.error);
//                            Log.i("PingTask", "Json Error!!!");
//                        }
//                    }
//                }


                return "";
            }catch (Exception e)
            {

            }
            return "";
        }


        private String readInput(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer json = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }

            Log.i("AsyncTask", json.toString()+"iiiiii");

            return json.toString();
        }

        @Override
        protected void onPreExecute() {
            //mProgressDialog.show();
            Log.i("AsyncTask", "PreExecute");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
            Log.i("AsyncTask", "ProgressUpdate");
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("AsyncTask", "PostExecute");
        }



    }


    private class CreateProfileTask extends AsyncTask<InputStream, String, String> {

        public class ClientError {
            public String code;

            public String message;

            public UUID requestId;
        }

        public class ServiceError {
            public ClientError error;
        }

        private HttpClient mClient = new DefaultHttpClient();
        private Gson mGson = new Gson();
        @Override
        protected String doInBackground(InputStream... params)
        {
            try {
                // Create Profile

                Log.i("PingTask", "InBackground");
                HttpPost request = new HttpPost(url);
                System.out.println("executing request " + request.getURI());
                request.setHeader(HEADER_KEY, mSubscriptionKey);
                request.setHeader(CONTENT_TYPE, APPLICATION_JSON);

                request.setEntity(new ByteArrayEntity((byte[])(new String("{\"locale\":\"en-us\",}").getBytes())));
                HttpResponse response = this.mClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
                    return readInput(response.getEntity().getContent());
                } else {
                    String json = readInput(response.getEntity().getContent());
                    if (json != null) {
                        ServiceError error = mGson.fromJson(json, ServiceError.class);
                        if (error != null) {
                            //throw new ClientException(error.error);
                            Log.i("PingTask", "Json Error!!!");
                        }
                    }

                    Log.i("PingTask", "Json Error!!!!!!");
                }
                Log.i("Ping", "status code: " + statusCode);

                return "";
            }catch (Exception e)
            {

            }
            return "";
        }


        private String readInput(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer json = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }

            Log.i("AsyncTask", json.toString()+"iiiiii");


            ProfileID face = mGson.fromJson(json.toString(), ProfileID.class);


            return face.identificationProfileId;
        }

        @Override
        protected void onPreExecute() {
            //mProgressDialog.show();
            Log.i("AsyncTask", "PreExecute");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
            Log.i("AsyncTask", "ProgressUpdate");
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("AsyncTask", "PostExecute");
            if(!result.isEmpty())
            {
                mUserList.add(new UserInfo(sUserName,result));

                mPersons.add(new VoicePerson(sUserName,result));
                mgr.add(new VoicePerson(sUserName,result));

//                imageViewerHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //mUserList.add(new UserInfo("bb", "12341234"));
//                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//                        Map<String, Object> map = new HashMap<String, Object>();
//                        for (UserInfo ui : mUserList) {
//                            map.put("name", ui.mUserName);
//                            map.put("id", ui.mUserID);
//                            list.add(map);
//                        }
//                        //mImageView.setBackgroundColor(1000001);
//                        lv = (ListView) findViewById(R.id.listView_VoiceRecognition1_User);
//                        SimpleAdapter adapter = new SimpleAdapter(VoiceRecognition1Activity.this, list, android.R.layout.simple_list_item_2,
//                                new String[]{"name", "id"}, new int[]{android.R.id.text1, android.R.id.text2});
//                        lv.setAdapter(adapter);
//                    }
//                });

                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //mUserList.add(new UserInfo("bb", "12341234"));
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        Map<String, Object> map = new HashMap<String, Object>();
                        for (VoicePerson ui : mPersons) {
                            map.put("name", ui.mName);
                            map.put("id", ui.mID);
                            list.add(map);
                        }
                        //mImageView.setBackgroundColor(1000001);
                        lv = (ListView) findViewById(R.id.listView_VoiceRecognition1_User);
                        SimpleAdapter adapter = new SimpleAdapter(VoiceRecognition1Activity.this, list, android.R.layout.simple_list_item_2,
                                new String[]{"name", "id"}, new int[]{android.R.id.text1, android.R.id.text2});
                        lv.setAdapter(adapter);
                    }
                });
            }
        }


    }


    private class EnrollTask extends AsyncTask<InputStream, String, String> {

        public class ClientError {
            public String code;

            public String message;

            public UUID requestId;
        }

        public class ServiceError {
            public ClientError error;
        }

        private HttpClient mClient = new DefaultHttpClient();
        private Gson mGson = new Gson();
        @Override
        protected String doInBackground(InputStream... params)
        {
            try {

                // Create Enrollment
                Log.i("PingTask", "InBackground");
                String urlLocal = new String("");
//                for(UserInfo ui:mUserList)
//                {
//                    if(ui.mUserName == sUserName)
//                    {
//                        urlLocal = new String(urlEnrollment + ui.mUserID + "/enroll");
//                    }
//                }

                for(VoicePerson ui:mPersons)
                {
                    if(ui.mName.equals(sUserName))
                    {
                        urlLocal = new String(urlEnrollment + ui.mID + "/enroll");
                    }
                }

                HttpPost request = new HttpPost(urlLocal);
                Log.i("PingTask", "InBackground44444");
                System.out.println("executing request " + request.getURI());
                request.setHeader(HEADER_KEY, mSubscriptionKey);
                request.setHeader(CONTENT_TYPE, FORM_DATA);

                File file = Environment.getExternalStorageDirectory();
                File file1 = new File(file, "test.wav");
                InputStream inputStream = new FileInputStream(file1);


                byte[] buff = new byte[8000];

                int bytesRead = 0;

                ByteArrayOutputStream bao = new ByteArrayOutputStream();

                while((bytesRead = inputStream.read(buff)) != -1) {
                    bao.write(buff, 0, bytesRead);
                }

                byte[] data = bao.toByteArray();

                ByteArrayInputStream bin = new ByteArrayInputStream(data);

                System.out.println(bin.available());
                System.out.println("The voice file size is: " + data.length/1024./1024.);

                request.setEntity(new ByteArrayEntity((byte[]) (data)));

                Log.i("PingTask", "InBackground33333");
                HttpResponse response = this.mClient.execute(request);
                Log.i("PingTask", "InBackground22222");
                int statusCode = response.getStatusLine().getStatusCode();
                Log.i("PingTask", "InBackground11111");
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
                    readInput(response.getEntity().getContent());
                    //Header hd = response.getFirstHeader(OPEARATION_LOCATION);
                    String sOpr = response.getFirstHeader(OPERATION_LOCATION).getValue();
                    System.out.println(sOpr);
                    HttpGet request1 = new HttpGet(sOpr);
                    request1.setHeader(HEADER_KEY, mSubscriptionKey);
                    HttpClient mClient1 = new DefaultHttpClient();
                    String status = new String("");
                    for(int i = 0; i < 20; ++i)
                    {
                        HttpResponse response1 = mClient1.execute(request1);
                        mEI = readInput1(response1.getEntity().getContent());
                        if(mEI != null)
                        {
                            if(mEI.status.equals("succeeded"))
                            {
                                inputStream.close();
                                bin.close();
                                bao.close();
                                return mEI.processingResult.enrollmentStatus;
                            }
                        }
                    }
                } else {
                    String json = readInput(response.getEntity().getContent());
                    if (json != null) {
                        ServiceError error = mGson.fromJson(json, ServiceError.class);
                        if (error != null) {
                            //throw new ClientException(error.error);
                            Log.i("PingTask", "Json Error!!!");
                        }
                    }

                    Log.i("PingTask", "Json Error!!!!!!");
                }
                Log.i("Ping", "status code: " + statusCode);


                inputStream.close();
                bin.close();
                bao.close();
                return "";
            }catch (Exception e)
            {

            }
            return "";
        }


        private String readInput(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer json = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }

            Log.i("AsyncTask", json.toString()+"iiiiii");

            return json.toString();
        }

        private EnrollInfo readInput1(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer json = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }

            Log.i("AsyncTask", json.toString() + "iiiiii");

            EnrollInfo ei = mGson.fromJson(json.toString(), EnrollInfo.class);
            //Log.i("AsyncTask", mEI.processingResult.enrollmentStatus + "iiiiii");

            return ei;
        }

        @Override
        protected void onPreExecute() {
            //mProgressDialog.show();
            Log.i("AsyncTask", "PreExecute");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
            Log.i("AsyncTask", "ProgressUpdate");
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("AsyncTask", "PostExecute");

            if(!result.isEmpty())
            {
                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView)findViewById(R.id.textView_VoiceRecognition1_ResInfo);
                        tv.setText("Enroll status is: " + mEI.processingResult.enrollmentStatus);
                    }
                });
            }
        }



    }

    private class IdentificationTask extends AsyncTask<InputStream, String, String> {

        public class ClientError {
            public String code;

            public String message;

            public UUID requestId;
        }

        public class ServiceError {
            public ClientError error;
        }

        private HttpClient mClient = new DefaultHttpClient();
        private Gson mGson = new Gson();
        @Override
        protected String doInBackground(InputStream... params)
        {
            try {
                // Identification


                File file = Environment.getExternalStorageDirectory();
                File file1 = new File(file, "test.wav");
                InputStream inputStream = new FileInputStream(file1);


                byte[] buff = new byte[8000];

                int bytesRead = 0;

                ByteArrayOutputStream bao = new ByteArrayOutputStream();

                while((bytesRead = inputStream.read(buff)) != -1) {
                    bao.write(buff, 0, bytesRead);
                }

                final byte[] data = bao.toByteArray();

                ByteArrayInputStream bin = new ByteArrayInputStream(data);

                System.out.println(bin.available());
                System.out.println("The voice file size is: " + data.length / 1024. / 1024.);

                try{
                    for(final VoicePerson p:mPersons)
                    {
                        if(Indentify(p.mID, data))
                        {
                            return mII.processingResult.identifiedProfileId;
                        }
                        Thread.sleep(50000);
                    }
                }catch (Exception ex)
                {

                }

                inputStream.close();

                bin.close();
                bao.close();

                return "";
            }catch (Exception e)
            {

            }
            return "";
        }


        private boolean Indentify(String uidTest, byte[] data) throws IOException {
            try
            {
                System.out.println("Begin Indentify!");
                String urlLocal = new String("");
//            for(UserInfo ui:mUserList)
//            {
//                if(ui.mUserName == sUserName)
//                {
//                    urlLocal = new String(urlIdentification + ui.mUserID);
//                }
//            }
                System.out.println("Loop data's size is: " + data.length);
                urlLocal = new String(urlIdentification + uidTest);
                HttpPost idtRequest = new HttpPost(urlLocal);
                System.out.println("executing request " + idtRequest.getURI());
                idtRequest.setHeader(HEADER_KEY, mSubscriptionKey);
                idtRequest.setHeader(CONTENT_TYPE, FORM_DATA);

                idtRequest.setEntity(new ByteArrayEntity((byte[]) (data)));

                HttpResponse response = this.mClient.execute(idtRequest);
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Identification status code is: " + statusCode);
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED) {
                    readInput(response.getEntity().getContent());
                    String sOpr = response.getFirstHeader(OPERATION_LOCATION).getValue();
                    //sOpr += "sss";
                    System.out.println(sOpr);
                    HttpGet request1 = new HttpGet(sOpr);
                    request1.setHeader(HEADER_KEY, mSubscriptionKey);
                    HttpClient mClient1 = new DefaultHttpClient();
                    for(int i = 0; i < 20; ++i) {
                        HttpResponse response1 = mClient1.execute(request1);
                        mII = readInput1(response1.getEntity().getContent());
                        if(mII != null)
                        {
                            if(mII.status.equals("succeeded"))
                            {
                                //return mII.processingResult.identifiedProfileId;
                                if(uidTest.equals(mII.processingResult.identifiedProfileId))
                                {
                                    if((mII.processingResult.confidence.equals("High")))
                                    {
                                        return true;
                                    }
                                    else
                                    {
                                        return false;
                                    }
                                }
                                else
                                {
                                    return false;
                                }
                            }

                            if(mII.status.equals("failed"))
                            {
                                return false;
                            }
                        }
                    }
                    return false;
                }else
                {
                    String json = readInput(response.getEntity().getContent());
                    if (json != null) {
                        ServiceError error = mGson.fromJson(json, ServiceError.class);
                        if (error != null) {
                            //throw new ClientException(error.error);
                            Log.i("PingTask", "Json Error!!!");
                        }
                    }
                    return false;
                }
            }
            catch (Exception ex)
            {
                return false;
            }
        }


        private String readInput(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer json = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }

            Log.i("AsyncTask", json.toString() + "iiiiii");

            return json.toString();
        }


        private IdentificationInfo readInput1(InputStream is) throws IOException {
            try{
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer json = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    json.append(line);
                }

                Log.i("AsyncTask", json.toString()+"iiiiii");

                IdentificationInfo ii = mGson.fromJson(json.toString(), IdentificationInfo.class);

                return ii;
            }catch (Exception ex)
            {
                return null;
            }
        }


        @Override
        protected void onPreExecute() {
            //mProgressDialog.show();
            Log.i("AsyncTask", "PreExecute");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setMessage(progress[0]);
            Log.i("AsyncTask", "ProgressUpdate");
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("AsyncTask", "PostExecute");

            if(!result.isEmpty())
            {
                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView)findViewById(R.id.textView_VoiceRecognition1_ResInfo);

                        for(VoicePerson p:mPersons)
                        {
                            if(p.mID.equals(mII.processingResult.identifiedProfileId))
                            {
                                tv.setText("Indentification status is: " + p.mName);
                                return;
                            }
                        }

                        tv.setText("Indentification status is: " + "Not Recognized!!!!");
                    }
                });
            }
            else {
                imageViewerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView)findViewById(R.id.textView_VoiceRecognition1_ResInfo);
                        tv.setText("Indentification status is: " + "Not Recognized!!!!");
                    }
                });
            }

        }



    }

    private AudioWriter wr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recognition_1);

        mgr = new VoiceDBManager1(this);

        mPersons = (ArrayList<VoicePerson>)mgr.query();

        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (VoicePerson person : mPersons) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", person.mName);
            map.put("id", person.mID);
            list.add(map);
        }
        lv = (ListView) findViewById(R.id.listView_VoiceRecognition1_User);
        SimpleAdapter adapter = new SimpleAdapter(VoiceRecognition1Activity.this, list, android.R.layout.simple_list_item_2,
                new String[]{"name", "id"}, new int[]{android.R.id.text1, android.R.id.text2});
        lv.setAdapter(adapter);


//        mUserList.add(new UserInfo(sUserName,result));
//
//        imageViewerHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                //mUserList.add(new UserInfo("bb", "12341234"));
//                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//                Map<String, Object> map = new HashMap<String, Object>();
//                for (UserInfo ui : mUserList) {
//                    map.put("name", ui.mUserName);
//                    map.put("id", ui.mUserID);
//                    list.add(map);
//                }
//                //mImageView.setBackgroundColor(1000001);
//                lv = (ListView) findViewById(R.id.listView_VoiceRecognition1_User);
//                SimpleAdapter adapter = new SimpleAdapter(VoiceRecognition1Activity.this, list, android.R.layout.simple_list_item_2,
//                        new String[]{"name", "id"}, new int[]{android.R.id.text1, android.R.id.text2});
//                lv.setAdapter(adapter);
//            }
//        });
    }

    public void Record(View view) throws InterruptedException {
        Log.i("record", "Begin to record audio\n");

        Button bt = (Button)findViewById(R.id.button_VoiceRecognition1_Record);

        bt.setEnabled(false);

        wr = new AudioWriter();

        wr.startTesting();

        Thread.sleep(80000, 1000);

        wr.stopTesting();

        ProgressDialog mProgressDialog1 = new ProgressDialog(this);
        mProgressDialog1.setTitle("The record is end!!!!!");
        mProgressDialog1.show();

        Log.i("record", "End to record audio\n");

        bt.setEnabled(true);
    }

    public void CreateUser(View view)
    {
        EditText tv = (EditText)findViewById(R.id.editText_VoiceRecognition1_Name);
        sUserName = tv.getText().toString();

        Log.i("Ping", "Begin to ping oxford server!!!");

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new CreateProfileTask().execute(inputStream);

    }

    public void Enroll(View view)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new EnrollTask().execute(inputStream);
    }

    public void Indentification(View view)
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new IdentificationTask().execute(inputStream);
    }

    public void InputUserName(View view)
    {
        EditText tv = (EditText)findViewById(R.id.editText_VoiceRecognition1_Name);
        sUserName = tv.getText().toString();
    }

}
