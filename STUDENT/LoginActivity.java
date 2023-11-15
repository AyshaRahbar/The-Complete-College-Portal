package com.project.project.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.attendance.data.Faculty;
import com.project.project.attendance.data.Students;
import com.project.project.bloodbank.data.Users;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.editText)
    EditText etUsernmae;

    @BindView(R.id.editText2)
    EditText etPassword;

    @BindView(R.id.button1)
    Button btnLogin;

    @BindView(R.id.forgotpassword)
    Button btnForgotpassword;

    @BindView(R.id.type)
    RadioGroup rgUsertype;

    private Unbinder unbinderknife;

    public static String name = "", username, password, phone, address;

    public static ParseObject user;
    public static int balance = 100;
    private String TAG = "LoginActivity";
    private boolean isHod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colege_login);
        unbinderknife = ButterKnife.bind(this);


        btnForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                boolean teacher = rgUsertype.getCheckedRadioButtonId() == R.id.teacher ? true : false;
                intent.putExtra("isTeacher", teacher);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = etUsernmae.getText().toString();
                String password = etPassword.getText().toString();

//                username = "admin";
//                password = "admin";

                if (username.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter username");
                } else if (password.equals("")) {
                    Utils.showToast(getApplicationContext(), "Enter password");
                } else {


                    if (username.equals("admin") && password.equals("admin")) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("hod", true);
                        isHod = true;
                        startActivity(intent);
                        return;
                    }

                    if (rgUsertype.getCheckedRadioButtonId() == R.id.teacher) {
                        checkFacultyLogin(username, password);
                        return;
                    }

                    ParseQuery<ParseObject> query = ParseQuery.getQuery(Students.class.getSimpleName());
                    query.whereEqualTo(Students.username, username);
                    query.whereEqualTo(Students.password, password);

                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> users, ParseException e) {
                            if (e == null) {
                                if (users.size() > 0) {

                                    Utils.showToast(LoginActivity.this, "Login successfull");
                                    user = users.get(0);
                                    name = users.get(0).getString(Users.name);
                                    LoginActivity.username = users.get(0).getString(Users.username);
                                    LoginActivity.password = users.get(0).getString(Users.password);
                                    LoginActivity.name = users.get(0).getString(Users.name);
                                    phone = users.get(0).getString(Users.phone);
                                    address = users.get(0).getString(Users.address);


                                    Intent intent = new Intent(LoginActivity.this, StudentHomeActivity.class);
                                    intent.putExtra("hod", true);
                                    isHod = true;
                                    startActivity(intent);

                                } else {
                                    Utils.showToast(getApplicationContext(), "Invalid credentials");
                                }
                            } else {
                                Utils.showToast(getApplicationContext(), "Registration failed");
                            }
                        }
                    });

                }
            }
        });
    }


    public void checkFacultyLogin(String username, String password) {


//        username = "daya";
//        password = "123456";
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Faculty.class.getSimpleName());
        query.whereEqualTo(Faculty.username, username);
        query.whereEqualTo(Faculty.password, password);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if (e == null) {
                    if (users.size() > 0) {


                        user = users.get(0);
                        name = users.get(0).getString(Users.name);
                        LoginActivity.username = users.get(0).getString(Users.username);
                        LoginActivity.password = users.get(0).getString(Users.password);
                        phone = users.get(0).getString(Users.phone);
                        address = users.get(0).getString(Users.address);
//
//                        Intent intent = new Intent(LoginActivity.this, BankHomeActivity.class);
//                        startActivity(intent);

                        Utils.showToast(LoginActivity.this, "Login successfull: " + LoginActivity.username);

                        Intent intent = new Intent(LoginActivity.this, GetAttendanceActivity.class);
                        intent.putExtra("hod", true);
                        isHod = true;
                        startActivity(intent);

                    } else {
                        Utils.showToast(getApplicationContext(), "Invalid credentials");
                    }
                } else {
                    Utils.showToast(getApplicationContext(), "Registration failed");
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
