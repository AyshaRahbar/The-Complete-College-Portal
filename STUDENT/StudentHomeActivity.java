package com.project.project.attendance;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Students;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

public class StudentHomeActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();


    @BindView(R.id.textview)
    TextView tvAttendance;

    @BindView(R.id.captureImage)
    Button btnCaptureImage;

    @BindView(R.id.applybuspass)
    Button btnApplybuspass;

    @BindView(R.id.onlinecompiler)
    Button btnOnlineCompiler;

    @BindView(R.id.viewnotes)
    Button btnViewNotes;

    @BindView(R.id.attndance)
    Button btnAttendance;

    @BindView(R.id.profile)
    Button btnProfile;

    @BindView(R.id.imageview)
    ImageView imageView;

    private Unbinder unbinderknife;

    private int CAMERA_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_studenthome_activity);

        unbinderknife = ButterKnife.bind(this);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        String name = LoginActivity.user.getString(Students.name);
        String phone = LoginActivity.user.getString(Students.phone);
        String address = LoginActivity.user.getString(Students.address);
        Integer totalClass = LoginActivity.user.getInt(Students.totalClassTaken);
        Integer totalAttended = LoginActivity.user.getInt(Students.totalClassAttended);

        String message = "Name: " + name + "\nPhone: " + phone + "\nAddress: " + address + "\nTotal Class: " + totalClass +
                "\nTotal Attended: " + totalAttended;

        tvAttendance.setText(message);


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentTextViewActivity.class);
                intent.putExtra("student", true);
                startActivity(intent);
            }
        });

        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentTextViewActivity.class);
                intent.putExtra("student", false);
                startActivity(intent);
            }
        });

        btnViewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentHomeActivity.this, ViewNotesActivity.class);
                startActivity(intent);
            }
        });

        btnApplybuspass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://student.mybmtc.com:8280/bmtc/login/secure";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btnOnlineCompiler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.programiz.com/c-programming/online-compiler/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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
                    registerUser(StudentHomeActivity.this, imageRequestBody);
                }
        }
    }


    public static void registerUser(Activity activity, RequestBody postbody) {

        String url = "http://192.168.1.4:5000/save_image/";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(postbody)
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
                        Utils.showToast(activity, "Photo registered failed");
                        Log.d("failed", "server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(activity, "Photo registered successfull");
//                            TextView responseText = findViewById(R.id.responseText);
                        //                                responseText.setText(response.body().string());
                        Log.d("conneced", response.body().toString());
                    }
                });
            }
        });
    }
}
