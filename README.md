基于百度地图SDK7.6.3开发的简单地图应用，地图视图可放大缩小、可切换显示模式为普通模式、卫星模式、实时路况模式、城市热力模式，共包含区域检索、公交路线检索、兴趣点检索、地理经纬度检索、驾车路径规划检索功能；利用本地SQLite存储已注册的用户信息，模拟用户注册与登入账号来使用一些需要登入后使用的功能。在进行程序演示前请在app/src/main/AndroidManifest.xml中的第18行处android:value="Your API_KEY Here"填写申请到的百度地图API_KEY。提示：如果本应用无法运行在模拟器，那么请使用实机运行本应用。

BaiduMap Lite is an open-source Android application based on Baidu Maps SDK 7.6.3. It provides essential map functionalities, including zooming, multiple view modes (standard, satellite, real-time traffic, heatmap), and various search features such as area search, POI search, transit route search, geolocation lookup, and driving route planning. The app also includes a local SQLite-based user system to simulate registration and login for accessing certain features. Before start the application, Fill line 18 of " android:value="Your API_KEY Here" " in app/src/main/AndroidManifest.xml with the applied Baidu Map API_KEY. Tip: If this application cannot run in the simulator, please use a real machine to run the application.

以下是本精简版Android地图的应用展示。
The following is lite Android map application display.

基础Activity | Base Activity
该Activity实现Toolbar菜单栏，可以通过菜单栏切换地图显示模式、切换地图功能、注册或登入账号、修改百度Logo位置。
This Activity implements Toolbar the menu bar, which allowed to switch the map display mode, switch map functions, register or log in to an account, and modify the Baidu Logo position.
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-233538.png)

登入界面 | Login Page
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-233553.png)

注册界面 | Registration Page
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-233637.png)

注册成功界面 | Registration Success Page
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-233641.png)

POI检索界面 | POI Search Page
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-234225.png)

经纬度检索界面 | Longitude Latitude Search Page
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-234410.png)

公交线路检索界面 | Bus Route Search Page
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-234429.png)
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250116-234441.png)

驾车路径规划检索界面 | Driving Route Planning Search Page
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250117-001653.png)
![image](https://github.com/Soursoupxi/Android_Lite_Map_Based_on_BaiduMapSDK_v7.6.3/blob/main/screenshots/Screenshot_20250117-001706.png)
