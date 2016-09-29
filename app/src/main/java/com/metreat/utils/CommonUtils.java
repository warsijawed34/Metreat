package com.metreat.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.R;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static AlertDialog dialog, dialog2;
    static DisplayMetrics displaymetrics;
    public static Dialog customDialog;
    public static TextView messageTv, buttonTv;

    public static void showAlertDialog(Context mContext, String title, String message) {

        if (mContext == null) return;
        if (dialog != null && dialog.isShowing()) return;

        dialog = new AlertDialog.Builder(mContext).create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.ok), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

//    public static void showAlertDialogWith2Btn(Context mContext, String title, String message) {
//
//        if (mContext == null) return;
//        if (dialog2 != null && dialog2.isShowing()) return;
//
//        dialog2 = new AlertDialog.Builder(mContext).create();
//        dialog2.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        dialog2.setTitle(title);
//        dialog2.setMessage(message);
//        dialog2.setButton(AlertDialog.BUTTON_POSITIVE, mContext.getString(R.string.show), new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        dialog2.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.cancel), new OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog2.show();
//    }

//    public static void showCustomDialog(Context context, String message, String button){
//        customDialog = new Dialog(context);
//        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        customDialog.setContentView(R.layout.custom_dialog);
//        messageTv = (TextView) customDialog.findViewById(R.id.tv_message);
//        buttonTv= (TextView) customDialog.findViewById(R.id.tv_button);
//
//        messageTv.setText(message);
//        buttonTv.setText(button);
//        customDialog.show();
  // }

/*

   public static void showCustomDialog(Context context, String message, String button){
       customDialog = new Dialog(context);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setContentView(R.layout.custom_dialog);
        messageTv = (TextView) customDialog.findViewById(R.id.tv_message);
        buttonTv= (TextView) customDialog.findViewById(R.id.tv_button);
        messageTv.setText(message);
        buttonTv.setText(button);
        customDialog.show();
     }*/

    public static void showToast(Context mContext, String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static boolean isValidEmailAddress(String emailAddress) {
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(emailAddress);
        if (!matcher.find()) {
            return false;
        }
        return true;
    }

    public static boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean pstatus = false;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                pstatus = true;
            }
        }
        return pstatus;
    }

    public static String getCalculatedDate(String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return s.format(new Date(cal.getTimeInMillis()));
    }

    //    //private static final String PHONE_REGEX = "\\d{3}-\\d{7}";
//    public final static Pattern MOBILE_PATTERN = Pattern
//            .compile("\\d{3}-\\d{7}");
//
//    public static boolean checkMobile(String zip) {
//        return MOBILE_PATTERN.matcher(zip).matches();
//    }
//
//
//    public static final boolean isValidPhoneNumber(CharSequence target) {
//        if (target.length() != 10) {
//            return false;
//        } else {
//            return android.util.Patterns.PHONE.matcher(target).matches();
//        }
//    }
//
//
//    private static void getHeightWigth(Context context) {
//        displaymetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//    }
//
//    public static int getWidth(Context context) {
//        getHeightWigth(context);
//        int width = displaymetrics.widthPixels;
//        return width;
//    }
//
//
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
//
//    public static boolean onlySpecialCharacter(String str) {
//        String specialCharacters = "[" + "-/@#!*$%^&.'_+={}()" + "]+";
//        return !str.matches(specialCharacters);
//    }
//
//    public static String convertUTF(String s) {
//        String data = "";
//        try {
//            data = URLEncoder.encode(s, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return data;
//    }
//
//    public static boolean isAppInstalled(Context context, String packageName) {
//        try {
//            context.getPackageManager().getApplicationInfo(packageName, 0);
//            return true;
//        }
//        catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//    }
//
//    public static boolean checkTime(int toH, int toM, int fromH, int fromM, EditText toTime, EditText fromTime) {
//        if (!fromTime.getText().toString().isEmpty()) {
//            if (fromH < toH) {
//                return true;
//
//            } else if (fromH == toH) {
//                if (fromM < toM) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } else {
//                return false;
//            }
//        } else if (!toTime.getText().toString().isEmpty()) {
//            if (fromH < toH) {
//                return true;
//
//            } else if (fromH == toH) {
//                if (fromM < toM) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } else {
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }
//
//    public static boolean isAppInForeground(Context context) {
//        List<ActivityManager.RunningTaskInfo> tasks =
//                ((ActivityManager) context.getSystemService(
//                        Context.ACTIVITY_SERVICE))
//                        .getRunningTasks(1);
//        if (tasks.isEmpty()) {
//            return false;
//        }
//        return tasks
//                .get(0)
//                .topActivity
//                .getPackageName()
//                .equalsIgnoreCase(context.getPackageName());
//    }
public static String checkStringValue(String value) {

    if (value == null || value.equals(null) || value.equals("null") || value.equals(""))
        value = "";
    return value;
}

    /**
     * convertUTF method used to encode string into UTF-8
     *
     * @param s
     * @return
     */
    public static String convertUTF(String s) {
        String data = "";
        try {
            data = URLEncoder.encode(s, "UTF-8");
            if (data.contains("%5C")) {
                data = data.replaceAll("%5C", "\\\\");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static boolean isAppInForeground(Context context) {
        List<ActivityManager.RunningTaskInfo> tasks =
                ((ActivityManager) context.getSystemService(
                        Context.ACTIVITY_SERVICE))
                        .getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        return tasks
                .get(0)
                .topActivity
                .getPackageName()
                .equalsIgnoreCase(context.getPackageName());
    }

}