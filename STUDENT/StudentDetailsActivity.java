package com.project.project.attendance;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Marks;

import java.util.List;

public class StudentDetailsActivity extends AppCompatActivity {


    private String TAG = "ViewBooksActivity";

    TextView tvDetails;

    String selectedSubject, selectedDept;

    int selectedSem;

    ParseObject selectedStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.college_student_details);

        selectedStudent = getIntent().getParcelableExtra("student");

        selectedSubject = getIntent().getStringExtra("sub");

        selectedSem = getIntent().getIntExtra("sem", 1);

        selectedDept = getIntent().getStringExtra("dept");
        tvDetails = (TextView) findViewById(R.id.details);

        fetchStudentsSubs();


    }


    public void fetchStudentsSubs() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Marks.class.getSimpleName());
        query.whereEqualTo(Marks.subject, selectedSubject);
        query.whereEqualTo(Marks.dept, selectedDept);
        query.whereEqualTo(Marks.sem, selectedSem);
        query.whereEqualTo(Marks.studentObjectId, selectedStudent.getObjectId());

        Log.i(TAG, "");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    Log.i(TAG, "objects found: " + objects.size());
                    if (objects.size() > 0) {
                        String data = "";
                        data = "Name: " + selectedStudent.getString("name") + "\n" + "Sub: "+selectedSubject + "\n";
                        if (selectedSubject.startsWith("lab")) {
                            data = data + "CO1: " + objects.get(0).getInt(Marks.co1) + "   CO2: " + objects.get(0).getInt(Marks.co2) + "\n" +
                                    "CO3: " + objects.get(0).getInt(Marks.co3) + "   CO4: " + objects.get(0).getInt(Marks.co4) + "\n" +
                                    "Activity: " + objects.get(0).getInt(Marks.activity) + "   Record: " + objects.get(0).getInt(Marks.record) + "\n"
                                    + "Average: " + objects.get(0).getInt(Marks.average);
                        } else {
                            data = data + "CO1: " + objects.get(0).getInt(Marks.co1) + "   CO2: " + objects.get(0).getInt(Marks.co2) + "\n" +
                                    "CO3: " + objects.get(0).getInt(Marks.co3) + "   CO4: " + objects.get(0).getInt(Marks.co4) + "\n" +
                                    "CO5: " + objects.get(0).getInt(Marks.co5) + "   CO6: " + objects.get(0).getInt(Marks.co6) + "\n"
                                    + "Activity: " + objects.get(0).getInt(Marks.activity) + "\n"
                                    + "Average: " + objects.get(0).getInt(Marks.average);
                        }


                        tvDetails.setText(data);
                    }

                } else {
                    e.printStackTrace();
                    Utils.showToast(getApplicationContext(), "fetch username error: " + e.getMessage());
                }
            }
        });
    }
}
