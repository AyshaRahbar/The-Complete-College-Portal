package com.project.project.attendance;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Faculty;
import com.project.project.attendance.data.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddFacultyActivity extends AppCompatActivity {

    @BindView(R.id.editText1)
    EditText etUsernmae;

    @BindView(R.id.editText2)
    EditText etName;

    @BindView(R.id.editText3)
    EditText etPhone;

    @BindView(R.id.editText4)
    EditText etAddress;

    @BindView(R.id.editText5)
    EditText etQualification;

    @BindView(R.id.subjectlayout)
    LinearLayout llSubjects;

    @BindView(R.id.button2)
    Button btnSubmit;

    ArrayList<CheckBox> checkBoxeList = new ArrayList<>();
    private Unbinder unbinderknife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_activity_addfaculty);
        unbinderknife = ButterKnife.bind(this);


/*        ParseQuery<ParseObject> query = ParseQuery.getQuery(Subject.class.getSimpleName());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        String subjects = objects.get(0).getString(Subject.subjects);
                        addCheckBoxes(subjects);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });*/

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = etUsernmae.getText().toString();
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                String qualification = etQualification.getText().toString();

//                getSelectedCheckboxValues();

                if (username.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter username");
                } else if (name.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter name");
                } else if (phone.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter phone");
                } else if (address.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter address");
                } else if (qualification.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter qualification");
                } else {
                    if (phone.length() != 10) {
                        Utils.showToast(getApplicationContext(), "Invalid phone");
                    } else {


                        ParseObject parseObject = new ParseObject(Faculty.class.getSimpleName());
                        String password = "" + new Random().nextInt(1000000);
                        parseObject.put(Faculty.username, username);
                        parseObject.put(Faculty.password, password);
                        parseObject.put(Faculty.name, name);
                        parseObject.put(Faculty.phone, phone);
                        parseObject.put(Faculty.qualification, qualification);
                        parseObject.put(Faculty.address, address);
//                        parseObject.put(Faculty.subjects, selectedSubjects);

                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Utils.showToast(getApplicationContext(), "Faculty added successfully");

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

    String selectedSubjects;

    public void getSelectedCheckboxValues() {

        for (int i = 0; i < checkBoxeList.size(); i++) {

            CheckBox checkBox = checkBoxeList.get(i);
            if (checkBox.isChecked()) {
                if (selectedSubjects == null) {
                    selectedSubjects = checkBox.getText().toString();
                } else {
                    selectedSubjects = selectedSubjects + "," + checkBox.getText().toString();
                }
            }
        }
    }

    // We are adding method for checkbox
    public void addCheckBoxes(String subjects) {
        // Split the subjects string into an array of individual subjects
        String[] temp = subjects.split("\\,");
        for (int i = 0; i < temp.length; i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(temp[i]);
            checkBox.setId(View.generateViewId());
            checkBoxeList.add(checkBox);
            // Add the CheckBox to the LinearLayout
            llSubjects.addView(checkBox);
        }
    }
}
