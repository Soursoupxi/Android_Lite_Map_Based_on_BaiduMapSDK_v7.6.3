package com.example.android.basicmap;

//用户状态管理类用于让用户不能在未登入情况下使用部分功能，也用于对未登入的用户弹出广告和推荐登入账号提醒
public class UserSessionManager
{
    private static UserSessionManager userSessionManager;
    private boolean isUserLoggedIn;

    //私有构造函数防止外部实例化
    private UserSessionManager()
    {
        isUserLoggedIn=false; //初始状态设定为未登入
    }

    //获取单例实例
    public static synchronized UserSessionManager getInstance()
    {
        if(userSessionManager==null)
        {
            userSessionManager=new UserSessionManager();
        }
        return userSessionManager;
    }

    public void setUserLoggedIn(boolean userLoggedIn)
    {
        isUserLoggedIn=userLoggedIn;
    }

    public boolean isUserLoggedIn()
    {
        return isUserLoggedIn;
    }
}
