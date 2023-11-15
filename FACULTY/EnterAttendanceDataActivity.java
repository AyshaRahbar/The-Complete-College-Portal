package com.project.project.attendance;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Students;
import com.project.project.acommon.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EnterAttendanceDataActivity extends AppCompatActivity {


    @BindView(R.id.editText1)
    EditText etTotalClass;

    @BindView(R.id.editText2)
    EditText etTotalAttended;

    @BindView(R.id.button1)
    Button btnSubmit;

    @BindView(R.id.spinner)
    Spinner spinSubjects;


    @BindView(R.id.spinneratttd)
    Spinner spineerAttd;

    private Unbinder unbinderknife;

    public static String name = "", username, password, phone, address, fatherPhone, type = "";

    private String TAG = "LoginActivity";

    String[] subjectsArray;

    String selectedSubject, selectedMonth;

    ParseObject selectedStudent;


    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_attendance_activity);

        unbinderknife = ButterKnife.bind(this);

        selectedStudent = getIntent().getParcelableExtra("student");

        subjectsArray = LoginActivity.subjects.split("\\,");

        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectsArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSubjects.setAdapter(aa);

        spinSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubject = subjectsArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<String> aa1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spineerAttd.setAdapter(aa1);

        spineerAttd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = months[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String totalClass = etTotalClass.getText().toString();
                String attended = etTotalAttended.getText().toString();


                if (totalClass.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter total class");
                } else if (attended.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter total attended");
                } else {

                    int intTotal = Integer.parseInt(totalClass);
                    int intAttd = Integer.parseInt(attended);
                    double avg = (double) intAttd / (double) intTotal ;

                    int avgpercent = (int) (avg * 100);
                    String data = selectedStudent.getString(Students.attendance);

                    if (data == null || data.equals("")) {
                        data = selectedSubject + " " + totalClass + " " + attended + " " + avgpercent + " " + selectedMonth;
                    } else {
                        data = data + "\n" + selectedSubject + " " + totalClass + " " + attended + " " + avgpercent + " " + selectedMonth;
                    }


                    selectedStudent.put(Students.attendance, data);
                    selectedStudent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Utils.showToast(getApplicationContext(), "attendance updated successfully");
                            } else {
                                e.printStackTrace();
                                Utils.showToast(getApplicationContext(), "attendance updated failure. " + e.getMessage());
                            }
                        }
                    });
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
