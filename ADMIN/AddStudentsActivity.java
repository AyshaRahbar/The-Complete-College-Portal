package com.project.project.attendance;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Faculty;
import com.project.project.attendance.data.Students;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddStudentsActivity extends AppCompatActivity {

    @BindView(R.id.editText1)
    EditText etUsn;

    @BindView(R.id.editText2)
    EditText etName;

    @BindView(R.id.editText3)
    EditText etPhone;

    @BindView(R.id.editText4)
    EditText etAddress;

    @BindView(R.id.editText5)
    EditText etSem;

    @BindView(R.id.button2)
    Button btnSubmit;

    ArrayList<CheckBox> checkBoxeList = new ArrayList<>();
    private Unbinder unbinderknife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_activity_addstudent);
        unbinderknife = ButterKnife.bind(this);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = etUsn.getText().toString();
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String sem = etSem.getText().toString();

                if (username.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter username");
                } else if (name.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter name");
                } else if (phone.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter phone");
                } else if (address.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter address");
                } else if (sem.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter sem");
                } else {
                    if (phone.length() != 10) {
                        Utils.showToast(getApplicationContext(), "Invalid phone");
                    } else {


                        ParseObject parseObject = new ParseObject(Students.class.getSimpleName());
                        String password = "" + new Random().nextInt(1000000);
                        parseObject.put(Students.username, username);
                        parseObject.put(Students.usn, username);
                        parseObject.put(Faculty.password, password);
                        parseObject.put(Faculty.name, name);
                        parseObject.put(Faculty.phone, phone);
                        parseObject.put(Faculty.address, address);
                        parseObject.put(Students.sem, Integer.parseInt(sem));
                        
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Utils.showToast(getApplicationContext(), "Student added successfully");

                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phone, null, "Your credentials, username: " + username + "\nPwd:" + password, null, null);
                                    finish();
                                } else {
                                    e.printStackTrace();
                                    Utils.showToast(getApplicationContext(), "Faculty add error: " + e.getMessage());
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinderknife.unbind();
    }
}
