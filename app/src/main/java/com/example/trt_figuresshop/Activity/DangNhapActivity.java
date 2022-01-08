package com.example.trt_figuresshop.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trt_figuresshop.Model.UserModel;
import com.example.trt_figuresshop.R;
import com.example.trt_figuresshop.Retrofit.API;
import com.example.trt_figuresshop.Retrofit.RetrofitClient;
import com.example.trt_figuresshop.Retrofit.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.internal.Util;

public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki;
    EditText email,pass;
    AppCompatButton btndangnhap;
    API api;
    CompositeDisposable compositeDisposable = new CompositeDisposable();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControl();
    }

    private void initControl() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DangKiActivity.class);
                startActivity(intent);
            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();
                if (TextUtils.isEmpty(str_email)){
                    Toast.makeText(getApplicationContext(), "ban chua nhap Email", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(str_pass)){
                    Toast.makeText(getApplicationContext(), "ban chua nhap pass", Toast.LENGTH_SHORT).show();
                } else {
                    Paper.book().write("email",str_email);
                    Paper.book().write("pass",str_pass);
                    compositeDisposable.add(api.dangNhap(str_email,str_pass)
                            .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userModel -> {
                                if(userModel.isSuccess()){
                                    Utils.user_current = userModel.getResult().get(0);
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();

                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                    ));
                }
            }
        });
    }

    private void initView() {
        Paper.init(this);
        api = RetrofitClient.getInstance(Utils.Base_URL).create(API.class);
        txtdangki = findViewById(R.id.txtdangki);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);
        if(Paper.book().read("email") != null && Paper.book().read("pass") != null){
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(Utils.user_current.getEmail() != null && Utils.user_current.getPassword()!= null){
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPassword());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}

