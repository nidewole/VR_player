package com.asha.md360player4android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Tools {

	/**
	 * 通过浏览器下载文件
	 *
	 * @param context
	 * @param url
	 *            下载文件的url
	 * @return void
	 * @date 
	 */
	public static void downLoadFile(Context context, String url) {
		if (!TextUtils.isEmpty(url)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				/*Intent service = new Intent(context, AppDownloadService.class);
				service.putExtra(AppDownloadService.INTENT_URL, url);
				context.startService(service);*/
			} else {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				context.startActivity(intent);
			}
		}
	}
	
	
	  /**
     * 程序是否在前台运行
     * @return true:前台运行 false:代表后台运行
     */
    public final static  boolean isAppOnForeground(Activity context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否6.0以上系统
     * @return
     */
    public static boolean isOverMarshmallow() {
      return Build.VERSION.SDK_INT >= 23;//23
    }

    @TargetApi(value = 23)
    public static List<String> findDeniedPermissions(Activity activity, String... permission){
      List<String> denyPermissions = new ArrayList<String>();
      for(String value : permission){
        if(activity.checkCallingOrSelfPermission(value) != PackageManager.PERMISSION_GRANTED){
          denyPermissions.add(value);
        }
      }
      return denyPermissions;
    }

    public static List<Method> findAnnotationMethods(Class clazz, Class<? extends Annotation> clazz1){
      List<Method> methods = new ArrayList<Method>();
      for(Method method : clazz.getDeclaredMethods()){
        if(method.isAnnotationPresent(clazz1)){
          methods.add(method);
        }
      }
      return methods;
    }



    @SuppressLint("NewApi")
	public static Activity getActivity(Object object){
      if(object instanceof Fragment){
        return ((Fragment)object).getActivity();
      } else if(object instanceof Activity){
        return (Activity) object;
      }
      return null;
    }
	
    /**
     * 转换字符文本为UTF8格式
     * @param str 需要转换字符文本
     * @return
     */
    public static String ChangeUTF8Str(String str){
        if(TextUtils.isEmpty(str)){
            throw  new NullPointerException("需要转换成UTF8格式字符文本是null");
        }
        String changeUtf8str = "";
        try {
            changeUtf8str = URLEncoder.encode(str, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        return changeUtf8str;
    }
}
