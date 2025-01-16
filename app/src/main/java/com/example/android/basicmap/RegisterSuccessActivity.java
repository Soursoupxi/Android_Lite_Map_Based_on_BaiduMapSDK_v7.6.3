package com.example.android.basicmap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterSuccessActivity extends AppCompatActivity
{
    private TextView mySuccessText;
    private Button myEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registersuccess);
        findViews();
        showSuccess();
    }

    private void findViews()
    {
        mySuccessText=(TextView)findViewById(R.id.successRegisterText);
        myEnter=(Button)findViewById(R.id.myButtonEnter);
    }

    private void showSuccess()
    {
        Bundle bundle=getIntent().getExtras(); //获取接口信息
        mySuccessText.setText("注册成功！您的账号信息如下：\n账号："+bundle.getInt("id")+"\n用户名："+bundle.getString("name")+"\n密码："+bundle.getString("pwd"));
    }

    public void onEnterClick(View view)
    {
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}
