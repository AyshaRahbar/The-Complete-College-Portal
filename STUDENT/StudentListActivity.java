package com.project.project.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Students;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {


    ListView lvListview;
    EditText etSemester;
    Button btnSubmit;

    private String TAG = "ViewBooksActivity";

    boolean isAttd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_student_list_activity);

        isAttd = getIntent().getBooleanExtra("attd", false);

        lvListview = (ListView) findViewById(R.id.listview);
        etSemester = (EditText) findViewById(R.id.editText);
        btnSubmit = (Button) findViewById(R.id.button1);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etSemester.getText().toString();
                if (name.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter sem");
                    return;
                }
                fetStudents(Integer.parseInt(name));
            }
        });

        lvListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if (isAttd) {
                    Intent intent = new Intent(StudentListActivity.this, EnterAttendanceDataActivity.class);
                    intent.putExtra("student", studentsList.get(i));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(StudentListActivity.this, UpdateMarksActivity.class);
                    intent.putExtra("student", studentsList.get(i));
                    startActivity(intent);
                }
            }
        });
    }


    public void fetStudents(int sem) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Students.class.getSimpleName());
        query.whereEqualTo("sem", sem);
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

    List<ParseObject> studentsList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();

    public void updateList() {
        nameList.clear();
        for (ParseObject object : studentsList) {
            nameList.add(object.getString(Students.name));
        }

        ArrayAdapter<String> aa = new ArrayAdapter<String>(StudentListActivity.this, android.R.layout.simple_list_item_1, nameList);
        lvListview.setAdapter(aa);
    }

}
