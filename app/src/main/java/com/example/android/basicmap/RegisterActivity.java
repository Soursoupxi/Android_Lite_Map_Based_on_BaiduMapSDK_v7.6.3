package com.example.android.basicmap;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends BaseActivity
{
    private EditText myName;
    private EditText myPwd;
    private EditText myRePwd;
    private CheckBox myAccept;
    private Button mySubmit;

    private static final String CONTENT_URI="content://com.example.android.basicmap.SQLiteProvider/users";
    private ContentResolver resolver=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        findViews();
    }

    private void findViews()
    {
        myName=(EditText)findViewById(R.id.myEditRegName);
        myPwd=(EditText)findViewById(R.id.myEditRegPwd);
        myRePwd=(EditText)findViewById(R.id.myEditRegRePwd);
        myAccept=(CheckBox)findViewById(R.id.myCheckBoxAccept);
        mySubmit=(Button)findViewById(R.id.myButtonSubmit);
    }

    public void onCheckBoxClick(View view)
    {
        if(myAccept.isChecked())
        {
            if(isValid(myRePwd) && !myPwd.getText().toString().isEmpty())
            {
                mySubmit.setEnabled(true);
            }
            else
            {
                Toast.makeText(this,"密码不能为空且两次输入的密码必须相同！",Toast.LENGTH_SHORT).show();
                myAccept.setChecked(false);
            }
        }
        else
        {
            mySubmit.setEnabled(false);
        }
    }

    private boolean isValid(EditText editText)
    { //判断再次输入的密码是否与输入的密码一致
        String pwd=myPwd.getText().toString();
        String repwd=myRePwd.getText().toString();
        if(!repwd.equals(pwd))
        {
            //在myRePwd控件（再次输入密码的文本框）中显示错误提示
            editText.setError("两次输入的密码不一致，请重新输入！");
            return false;
        }
        else
        {
            return true;
        }
    }

    public void onSubmitClick(View view)
    {
        Long _id;
        String name=myName.getText().toString();
        String pwd=myPwd.getText().toString();
        User user=new User(name,pwd);
        _id=user.saveUser(this);

        Toast.makeText(this,"恭喜您已成功注册，现在您可以使用所有功能。",Toast.LENGTH_LONG).show();

        Bundle bundle=new Bundle();

        bundle.putInt("id",_id.intValue());
        bundle.putString("name",name);
        bundle.putString("pwd",pwd);

        Intent intent=new Intent(this,RegisterSuccessActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
