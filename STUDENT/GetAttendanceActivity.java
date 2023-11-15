package com.project.project.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Students;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetAttendanceActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();


    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.captureImage)
    Button btnCaptureImage;

    @BindView(R.id.imageview)
    ImageView imageView;

    @BindView(R.id.submit)
    Button btnSubmit;

    @BindView(R.id.viewschedule)
    Button btnViewSchedule;

    @BindView(R.id.getattd)
    Button btnGetAttd;

    @BindView(R.id.buttonlayout)
    LinearLayout llButton;

    @BindView(R.id.listlayout)
    LinearLayout llLayout;

    private Unbinder unbinderknife;

    private int CAMERA_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_getattendance_activity);

        unbinderknife = ButterKnife.bind(this);

        fetchStudents();


        btnViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetAttendanceActivity.this, ViewScheduleActivity.class);
                startActivity(intent);
            }
        });

        btnGetAttd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llButton.setVisibility(View.GONE);
                llLayout.setVisibility(View.VISIBLE);
            }
        });

        
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (ParseObject po : studentsList) {
                    if (po.getBoolean(Students.isAttendanceUpdated)) {
                        po.put(Students.totalClassAttended, (po.getInt(Students.totalClassAttended) + 1));
                        po.put(Students.totalClassTaken, (po.getInt(Students.totalClassTaken) + 1));
                    }
                }

                ParseObject.saveAllInBackground(studentsList, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Utils.showToast(getApplicationContext(), "Attendance updated successfully");
                        } else {
                            Utils.showToast(getApplicationContext(), "Attendance updated error");
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                studentsList.get(position).put(Students.isAttendanceUpdated, (!studentsList.get(position).getBoolean(Students.isAttendanceUpdated)));
                ca.notifyDataSetChanged();
            }
        });
    }

    CustomAdapter ca;
    List<ParseObject> studentsList = new ArrayList<>();

    public void updateList() {
        ca = new CustomAdapter(GetAttendanceActivity.this, studentsList);
        listView.setAdapter(ca);
    }

    public void updateAttendance() {

    }


    public void fetchStudents() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Students.class.getSimpleName());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {
                    if (objects.size() > 0) {

                        studentsList = objects;
                        updateList();

                    } else {
                        Utils.showToast(getApplicationContext(), "Invalid credentials");
                    }

                } else {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "fetch error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinderknife.unbind();
    }


    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {

//                    Intent data = result;
                    Bitmap ImageBitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String filename = System.currentTimeMillis() + ".jpg";

                    RequestBody imageRequestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", filename, RequestBody.create(MediaType.parse("image/*jpg"), imageBytes))
                            .build();
                    imageView.setImageBitmap(ImageBitmap);
                    startRecognise(GetAttendanceActivity.this, imageRequestBody);
                    Uri selectedImage = imageUri;
                    Log.i(TAG, "onActivityResult: called imageuri: ");
                }
        }
    }


    public void startRecognise(Activity activity, RequestBody requestBody) {
        String url = "http://192.168.1.4:5000/attendance/";


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                            TextView responseText = findViewById(R.id.responseText);
                        Log.d("failed", "server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                activity.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
//                            TextView responseText = findViewById(R.id.responseText);
                        //                                responseText.setText(response.body().string());
                        final String strresponse;
                        try {
                            strresponse = response.body().string();
                            Log.i(TAG, "run: " + strresponse);

                            JSONObject jsonObject = new JSONObject(strresponse);
                            JSONArray jsonArray = jsonObject.getJSONArray("Present");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Log.i(TAG, "run: usn found:" + jsonArray.getString(i));
//                                findUSNInStudentsList(jsonArray.getString(i));
                                findUSNInStudentsList("456789");
                            }
                            ca.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void findUSNInStudentsList(String usn) {
        for (ParseObject po : studentsList) {
//            Log.i(TAG, "findUSNInStudentsList: found usn: "+po.getString(Students));
            if (po.getString(Students.usn) != null && po.getString(Students.usn).equalsIgnoreCase(usn)) {
                po.put(Students.isAttendanceUpdated, true);
            }
        }
    }


    @Override
    public void onBackPressed(){

        if(llLayout.getVisibility() == View.VISIBLE){
            llLayout.setVisibility(View.GONE);
            llButton.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }


    }


    public class CustomAdapter extends BaseAdapter {

        List<ParseObject> studentsList;

        Context mContext;

        ViewHolder viewHolder;

        public CustomAdapter(Context mContext, List<ParseObject> itemslist) {
            this.mContext = mContext;
            this.studentsList = itemslist;
        }

        @Override
        public int getCount() {
            return studentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.college_items_list_rows, parent, false);

                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.name);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                result = convertView;
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result = convertView;
            }


            final ParseObject object = studentsList.get(position);

            viewHolder.tvTitle.setText(object.getString(Students.name));
            viewHolder.checkBox.setChecked(object.getBoolean(Students.isAttendanceUpdated));


            return result;
        }


        public class ViewHolder {
            TextView tvTitle;
            CheckBox checkBox;
        }
    }


}
