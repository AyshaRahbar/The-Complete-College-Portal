package com.project.project.attendance;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Students;
import com.project.project.acommon.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewStudentAttendanceActivity extends AppCompatActivity {


    ListView lvListview;
    EditText etSemester;
    Button btnSubmit, btnSendSms;
    Spinner spinnerAttd;

    private String TAG = "ViewBooksActivity";

    boolean isHod;

    String selectedSubject, selectedMonth;

    ParseObject selectedStudent;


    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_attendance_list_activity);


        lvListview = (ListView) findViewById(R.id.listview);
        etSemester = (EditText) findViewById(R.id.editText);
        btnSubmit = (Button) findViewById(R.id.button1);
        btnSendSms = (Button) findViewById(R.id.sendsms);
        spinnerAttd = (Spinner) findViewById(R.id.spinneratttd);




        ArrayAdapter<String> aa1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAttd.setAdapter(aa1);

        spinnerAttd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = months[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertParents();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etSemester.getText().toString();
                if(name.equals("")){
                    Utils.showToast(getApplicationContext(), "Enter sem");
                    return;
                }
                fetStudents(Integer.parseInt(name));
            }
        });

        lvListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }


    public void alertParents(){
        for(ParseObject object : studentsList){

            String allData = object.getString(Students.attendance);

            if(allData == null || allData.equals("")){
                continue;
            }

            SmsManager sms = SmsManager.getDefault();

            String[] subjectData = allData.split("\n");
            for(int i=0;i<subjectData.length;i++){
                String[] data = subjectData[i].split(" ");
                String builder = "Subj: " + data[0] + " Avg: " + data[3] ;
                int attd = Integer.parseInt(data[3]);
                if(attd < 75){
                    sms.sendTextMessage(object.getString(Students.phone), null, "your ward attendance has shortage for subject " + data[0] + ", total attendance is "+attd + "%", null, null);
                }
            }
        }
    }
    
    public void fetStudents(int sem) {


        ParseQuery<ParseObject> query = ParseQuery.getQuery(Students.class.getSimpleName());
        query.whereEqualTo("sem", sem);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    studentsList = objects;
                    updateList();
                    if(LoginActivity.isHod){
                        btnSendSms.setVisibility(View.VISIBLE);
                    }
                }else{
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "fetch username error: "+e.getMessage());
                }
            }
        });
    }

    List<ParseObject> studentsList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();

    public void updateList(){
        nameList.clear();
        for(ParseObject object : studentsList){

            String displayData = null;

            String allData = object.getString(Students.attendance);

            if(allData == null || allData.equals("")){
                nameList.add(object.getString(Students.name));
                continue;
            }

            String[] subjectData = allData.split("\n");

            for(int i=0;i<subjectData.length;i++){
                String[] data = subjectData[i].split(" ");
                String builder = "Subj: " + data[0] + " " + extractAttdData(object.getString(Students.attendance), data[0]);
                
                if(displayData == null){
                    displayData = builder;
                }else{
                    displayData = displayData + "\n" + builder;
                }
            }

            nameList.add(object.getString(Students.name) + "\n" + displayData);
        }

        ArrayAdapter<String> aa = new ArrayAdapter<String>(ViewStudentAttendanceActivity.this, android.R.layout.simple_list_item_1, nameList);
        lvListview.setAdapter(aa);
    }


    public String extractAttdData(String data, String subject){

        if(data == null){
            return "";
        }
        String[] temp = data.split("\n");

        for(int i=0; i < temp.length;i++){

            if(temp[i].startsWith(subject)){
                if(temp[i].contains(selectedMonth)){
                    String[] attdData = temp[i].split(" ");
                    String message = "Attd: " + attdData[2] + "/" + attdData[1] + "Avg: " + attdData[3];
                    return message;
                }
            }

        }
        return  "";
    }

}
