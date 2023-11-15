package com.project.project.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.project.project.attendance.data.Subject;

import java.util.ArrayList;
import java.util.List;

public class SelectStudentsActivity extends AppCompatActivity {


    String[] departments = {"cse", "ece", "civil", "CP", "LS", "ADFT"};

    Integer[] sems = {1, 2, 3, 4, 5, 6};

    Spinner spinSem, spinDept, spinSubject;


    String selectedDept, selectedSubject;

    int selectedSem;

    ListView lvListview;

    Button btnSubmit;

    private String TAG = "ViewBooksActivity";

    boolean isAttd;

    boolean isView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_studentsselect_activity);


        isView = getIntent().getBooleanExtra("isview", false);
        isAttd = getIntent().getBooleanExtra("attd", false);

        spinSem = (Spinner) findViewById(R.id.sem);
        spinDept = (Spinner) findViewById(R.id.dept);
        spinSubject = (Spinner) findViewById(R.id.subject);
        lvListview = (ListView) findViewById(R.id.listview);

        btnSubmit = (Button) findViewById(R.id.button1);

        ArrayAdapter<Integer> semsAA = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, sems);
        ArrayAdapter<String> deptAA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, departments);

        semsAA.setDropDownViewResource(android.R.layout.simple_spinner_item);
        deptAA.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinSem.setAdapter(semsAA);
        spinDept.setAdapter(deptAA);


        spinSem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSem = sems[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDept = departments[i];
                fetchSubs(selectedSem, selectedDept);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedSem == 0) {
                    Utils.showToast(getApplicationContext(), "Please select semester");
                } else if (selectedDept == null) {
                    Utils.showToast(getApplicationContext(), "Please select department");
                } else if (selectedSubject == null) {
                    Utils.showToast(getApplicationContext(), "Please select subject");
                } else {
                    fetStudents(selectedSem, selectedDept);
                }

            }
        });

        lvListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if (isView) {
                    Intent intent = new Intent(SelectStudentsActivity.this, StudentDetailsActivity.class);
                    intent.putExtra("student", studentsList.get(i));
                    intent.putExtra("sem", selectedSem);
                    intent.putExtra("sub", selectedSubject);
                    intent.putExtra("dept", selectedDept);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SelectStudentsActivity.this, UpdateMarksActivity.class);
                    intent.putExtra("student", studentsList.get(i));
                    intent.putExtra("sem", selectedSem);
                    intent.putExtra("sub", selectedSubject);
                    intent.putExtra("dept", selectedDept);
                    startActivity(intent);
                }


            }
        });
    }


    public void fetStudents(int sem, String dept) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Students.class.getSimpleName());
        query.whereEqualTo("sem", sem);
        query.whereEqualTo("dept", dept);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    studentsList = objects;
                    updateList();
                } else {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "fetch username error: " + e.getMessage());
                }
            }
        });
    }


    public void fetchSubs(int sem, String dept) {

        Log.i(TAG, "sem: " + sem + " " + dept);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Subject.class.getSimpleName());
        query.whereEqualTo("sem", sem);
        query.whereEqualTo("dept", dept);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    Log.i(TAG, "Objects size: " + objects.size());
                    if (objects.size() > 0) {
                        Log.i(TAG, "size is zero");


                        selectedSubsObject = objects.get(0);
                        String[] subjects = selectedSubsObject.getString(Subject.subjects).split("\\,");
                        String[] labSubs = selectedSubsObject.getString(Subject.labSubs).split("\\,");
                        subjectsList.clear();
                        for (String s : subjects) {
                            subjectsList.add(s);
                        }

                        for (String s : labSubs) {
                            subjectsList.add(s);
                        }

                        udpateSubjectSpinner();
                    } else {
                        Utils.showToast(getApplicationContext(), "Subjects not added in the database for sem : " + sem + " dept: " + dept);
                    }

                } else {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "fetch username error: " + e.getMessage());
                }
            }
        });
    }


    public void udpateSubjectSpinner() {

        ArrayAdapter<String> subsAA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectsList);
        subsAA.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinSubject.setAdapter(subsAA);

        spinSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubject = subjectsList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    List<ParseObject> studentsList = new ArrayList<>();
    ParseObject selectedSubsObject;
    List<String> nameList = new ArrayList<>();
    List<String> subjectsList = new ArrayList<>();

    public void updateList() {
        nameList.clear();
        for (ParseObject object : studentsList) {
            nameList.add(object.getString(Students.name));
        }

        ArrayAdapter<String> aa = new ArrayAdapter<String>(SelectStudentsActivity.this, android.R.layout.simple_list_item_1, nameList);
        lvListview.setAdapter(aa);
    }

}
