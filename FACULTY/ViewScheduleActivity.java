package com.project.project.attendance;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.project.R;
import com.project.project.Utils;
import com.project.project.acommon.data.Users;
import com.project.project.salon.data.OrderItems;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ViewScheduleActivity extends AppCompatActivity {


    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.imageView)
    ImageView ivImageView;

    private Unbinder unbinderknife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewshcedule);
        unbinderknife = ButterKnife.bind(this);

        ParseFile file = LoginActivity.user.getParseFile("scheduleImage");

        Picasso.with(this).load(file.getUrl()).into(ivImageView);
        

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinderknife.unbind();

    }

}
