package com.example.android.basicmap;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity
{
    private EditText myEditTextAccount;
    private EditText myEditTextPwd;
    private Button myButtonLogin;
    private Button myButtonReg;

    private String myAccount; //账号
    private Integer myAccountInt; //账号(Integer型)
    private String myUsername; //用户名
    private String myPwd; //密码
    private static final String CONTENT_URI="content://com.example.android.basicmap.SQLiteProvider/users/";
    private ContentResolver resolver=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findViews();
    }

    private void findViews()
    {
        myEditTextAccount=(EditText)findViewById(R.id.myTextLogAccount);
        myEditTextPwd=(EditText)findViewById(R.id.myTextLogPwd);
        myButtonLogin=(Button)findViewById(R.id.myButtonLogin);
        myButtonReg=(Button)findViewById(R.id.myButtonReg);
    }

    private boolean loadUser(String account)
    {
        resolver=getContentResolver();
        Uri uri=Uri.parse(CONTENT_URI+account);
        Cursor cursor=resolver.query(uri,new String[]{"name","pwd"},null,null,null);
        if(cursor.getCount()>0)
        {
            myUsername=cursor.getString(cursor.getColumnIndex("name"));
            myPwd=cursor.getString(cursor.getColumnIndex("pwd"));
            System.err.println("myUsername"+myUsername+"\nmyPwd"+myPwd);
            return true;
        }
        else
        {
            System.err.println("当前账号不存在");
            return false;
        }
    }

    public void onLoginClick(View view)
    {
        String account=myEditTextAccount.getText().toString();
        String pwd=myEditTextPwd.getText().toString();
        if(!loadUser(account))
        {
            Toast.makeText(this,"当前账号不存在，请重新输入。",Toast.LENGTH_LONG).show();
            myEditTextAccount.setText("");
        }
        else
        {
            //判断当前界面输入的用户名与密码是否与注册信息相同
            if(pwd.equals(myPwd))
            {
                Toast.makeText(this,"欢迎回来，用户"+myUsername+"，您现在可以使用全部功能。",Toast.LENGTH_LONG).show();
                UserSessionManager.getInstance().setUserLoggedIn(true);
                Intent intent=new Intent(this,BaseActivity.class);
                Bundle bundle=new Bundle();
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this,"用户名或密码错误！请重新输入。",Toast.LENGTH_SHORT).show();
                myEditTextAccount.setText(account);
                myEditTextPwd.setText("");
            }
        }
    }

    public void onRegClick(View view)
    {
        Intent intent=new Intent(this,RegisterActivity.class);
        Bundle bundle=new Bundle();
        startActivity(intent);
    }
}







