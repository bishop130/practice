package com.suji.lj.myapplication.Utils;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.core.widget.NestedScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Query;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.gson.GsonBuilder;
import com.suji.lj.myapplication.Adapters.NewLocationService;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.UserAccountItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class Utils {

    public Utils() {

    }

    public static String getParamValFromUrlString(String url, String paramKey) {

        Log.d("## url", url);
        String[] urlParamPair = url.split("\\?");
        if (urlParamPair.length < 2) {
            Log.d("##", "파라미터가 존재하지 않는 URL 입니다.");
            return "";
        }
        String queryString = urlParamPair[1];
        Log.d("## queryString", queryString);
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            Log.d("## pair", pair);
            String[] pairArr = pair.split("=");
            if (paramKey.equals(pairArr[0])) {
                return pairArr[1]; // 찾았을 경우
            }
        }
        return "";
    }


    public static ArrayList<ContactItem> getContacts() {
        ArrayList<ContactItem> listItem = new ArrayList<>();
        Query q = Contacts.getQuery();
        q.include(Contact.Field.ContactId, Contact.Field.DisplayName, Contact.Field.PhoneNumber);
        List<Contact> result = q.find();

        try {
            String con = new GsonBuilder().setPrettyPrinting().create().toJson(result);
            Log.d("연락처", con);
            JSONArray jsonArr = new JSONArray(con);
            for (int i = 0; i < jsonArr.length(); i++) {

                JSONObject object = jsonArr.getJSONObject(i);
                ContactItem contactItem = new ContactItem();
                contactItem.setDisplayName(object.getString("displayName"));

                JSONArray jsonArr2 = object.getJSONArray("phoneNumbers");

                for (int j = 0; j < jsonArr2.length(); j++) {
                    JSONObject object2 = jsonArr2.getJSONObject(j);
                    contactItem.setPhoneNumbers(object2.getString("number"));

                }
                listItem.add(contactItem);
            }
            //setupRecyclerView(listItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listItem;
    }

    public static String getValueFromJson(String jsonStr, String key) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            return jsonObject.getString(key);
        } catch (JSONException e) {
            //Timber.d(key + " 값이 없음");
            return "";
        }
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());
    }

    public static String getCustomTime() {
        Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 30);
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(date);
    }

    public static String makeBankTranId() {

        return getServiceCode() + "U" + new SimpleDateFormat("HHmmssSSS", Locale.KOREA).format(new Date());


    }

    public static String getServiceCode() {


        String service_code;
        service_code = "T991596920";
        return service_code;


    }

    public static List<UserAccountItem> UserInfoResponseJsonParse(String body) {
        List<UserAccountItem> userAccountItemList = new ArrayList<>();
        Log.d("오픈뱅킹", "리스트만들기 ");

        try {
            JSONObject obj = new JSONObject(body);
            JSONArray jsonArray = obj.getJSONArray("res_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                UserAccountItem userAccountItem = new UserAccountItem();
                userAccountItem.setApi_tran_id(obj.getString("api_tran_id"));
                userAccountItem.setApi_tran_dtm(obj.getString("api_tran_dtm"));
                userAccountItem.setRsp_code(obj.getString("rsp_code"));
                userAccountItem.setRsp_message(obj.getString("rsp_message"));
                userAccountItem.setUser_seq_no(obj.getString("user_seq_no"));
                userAccountItem.setUser_ci(obj.getString("user_ci"));
                userAccountItem.setUser_name(obj.getString("user_name"));
                userAccountItem.setRes_cnt(obj.getString("res_cnt"));
////


                userAccountItem.setFintech_use_num(object.getString("fintech_use_num"));
                userAccountItem.setAccount_alias(object.getString("account_alias"));
                userAccountItem.setBank_code_std(object.getString("bank_code_std"));
                userAccountItem.setBank_code_sub(object.getString("bank_code_sub"));
                userAccountItem.setBank_name(object.getString("bank_name"));
                //userAccountItem.setAccount_num(object.getString("account_num"));
                userAccountItem.setAccount_num_masked(object.getString("account_num_masked"));
                userAccountItem.setAccount_holder_name(object.getString("account_holder_name"));
                userAccountItem.setAccount_type(object.getString("account_type"));
                userAccountItem.setInquiry_agree_yn(object.getString("inquiry_agree_yn"));
                userAccountItem.setInquiry_agree_dtime(object.getString("inquiry_agree_dtime"));
                userAccountItem.setTransfer_agree_yn(object.getString("transfer_agree_yn"));
                userAccountItem.setTransfer_agree_dtime(object.getString("transfer_agree_dtime"));
                userAccountItem.setPayer_num(object.getString("payer_num"));

                userAccountItemList.add(userAccountItem);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userAccountItemList;


    }

    public static List<UserAccountItem> AccountInfoResponseJsonParse(String body) {
        List<UserAccountItem> userAccountItemList = new ArrayList<>();
        Log.d("오픈뱅킹", "리스트만들기 ");

        try {
            JSONObject obj = new JSONObject(body);
            JSONArray jsonArray = obj.getJSONArray("res_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                UserAccountItem userAccountItem = new UserAccountItem();
                userAccountItem.setApi_tran_id(obj.getString("api_tran_id"));
                userAccountItem.setApi_tran_dtm(obj.getString("api_tran_dtm"));
                userAccountItem.setRsp_code(obj.getString("rsp_code"));
                userAccountItem.setRsp_message(obj.getString("rsp_message"));
                userAccountItem.setUser_name(obj.getString("user_name"));
                userAccountItem.setRes_cnt(obj.getString("res_cnt"));
////


                userAccountItem.setFintech_use_num(object.getString("fintech_use_num"));
                userAccountItem.setAccount_alias(object.getString("account_alias"));
                userAccountItem.setBank_code_std(object.getString("bank_code_std"));
                userAccountItem.setBank_code_sub(object.getString("bank_code_sub"));
                userAccountItem.setBank_name(object.getString("bank_name"));
                //userAccountItem.setAccount_num(object.getString("account_num"));
                userAccountItem.setAccount_num_masked(object.getString("account_num_masked"));
                userAccountItem.setAccount_holder_name(object.getString("account_holder_name"));
                userAccountItem.setAccount_type(object.getString("account_type"));
                userAccountItem.setInquiry_agree_yn(object.getString("inquiry_agree_yn"));
                userAccountItem.setInquiry_agree_dtime(object.getString("inquiry_agree_dtime"));
                userAccountItem.setTransfer_agree_yn(object.getString("transfer_agree_yn"));
                userAccountItem.setTransfer_agree_dtime(object.getString("transfer_agree_dtime"));
                userAccountItem.setAccount_state(object.getString("account_state"));

                userAccountItemList.add(userAccountItem);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userAccountItemList;


    }


    public static void getKeyHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.suji.lj.myapplication", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }

            }
        }
        return false;
    }

    public static String getCurrentTimeForBankNum() {
        return new SimpleDateFormat("HHmmssSSS", Locale.KOREA).format(new Date());
    }

    public static String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    public static Bitmap getBitmapFromString(String bitmap_string) {

        byte[] decodedByteArray = Base64.decode(bitmap_string, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public static boolean compareNowAndInput(int sel_hour, int sel_min) {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);

        int now_hour = Calendar.getInstance().get(Calendar.HOUR);
        int now_min = Calendar.getInstance().get(Calendar.MINUTE);
        long min = 0;

        try {

            Date date_now = dateFormat.parse(now_hour + ":" + now_min);
            Date date_input = dateFormat.parse(sel_hour + ":" + sel_min); //String을 포맷에 맞게 변경
            long duration = date_input.getTime() - date_now.getTime(); // 입력시간-현재시간
            min = duration / 60000;
            Log.d("싱글", min + "duration");


        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (min >= 30) { // 30분이상 지났을때

            return true;
        } else {
            return false;
        }


    }

    public static boolean checkIsToday(int sel_year, int sel_month, int sel_day) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        Log.d("싱글", year + "year");
        Log.d("싱글", month + "month");
        Log.d("싱글", day + "day");
        Log.d("싱글", sel_year + "sel_year");
        Log.d("싱글", sel_month + "ser_month");
        Log.d("싱글", sel_day + "sel_day");

        if ((year == sel_year) && (month == sel_month) && (day == sel_day)) {
            return true;
        } else {
            return false;
        }


    }

    public static void wakeDoze(Context context) {
        Log.d("서비스", "wakeDoze");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //PendingIntent foregroundService_sender = PendingIntent.getForegroundService(this, 123, new Intent(this, LocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,3000, foregroundService_sender);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//26
            //PendingIntent foregroundService_sender = PendingIntent.getForegroundService(this, 123, new Intent(this, LocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent foregroundService_sender = PendingIntent.getService(context, 123, new Intent(context, NewLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(0, foregroundService_sender), foregroundService_sender);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //21
            PendingIntent foregroundService_sender = PendingIntent.getService(context, 123, new Intent(context, NewLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setExact(AlarmManager.RTC, 0, foregroundService_sender);
        } else {
            PendingIntent foregroundService_sender = PendingIntent.getService(context, 123, new Intent(context, NewLocationService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, 0, foregroundService_sender);
        }


    }


    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public static boolean isEmpty(Object s) {
        if (s == null) {
            return true;
        }
        if ((s instanceof String) && (((String) s).trim().length() == 0)) {
            return true;
        }
        if (s instanceof Map) {
            return ((Map<?, ?>) s).isEmpty();
        }
        if (s instanceof List) {
            return ((List<?>) s).isEmpty();
        }
        if (s instanceof Object[]) {
            return (((Object[]) s).length == 0);
        }
        return false;
    }


    public static void fixMapScroll(ImageView imageView, NestedScrollView scrollView) {

        imageView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }

        });


    }

    public static void slideView(View view,
                                 int currentHeight,
                                 int newHeight) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(3000);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().height = value.intValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public static List<Integer> makeBalancePortion(int person_num) {
        List<Integer> list_portion = new ArrayList<>();
        int total_portion = 0;
        double result;

        for (int i = 0; i < person_num; i++) {
            result = 1.0 / person_num;
            list_portion.add((int) (result * 100));
        }

        for (int i = 0; i < person_num; i++) {

            total_portion += list_portion.get(i);

        }

        if (!(total_portion == 100)) {

            for (int i = 0; i < person_num; i++) {

                list_portion.set(i, list_portion.get(i) + 1);
                if (is100(list_portion)) {
                    break;
                }


            }
        }


        Log.d("에디트", "리스트포션 내용");
        return list_portion;
    }

    public static List<Integer> makeRankPriorityPortion(int person_num) {
        List<Integer> list_child = new ArrayList<>();
        List<Double> list_mother = new ArrayList<>();
        List<Integer> list_portion = new ArrayList<>();

        double sum;
        double num1 = 0.0;
        double num2 = 1.0;
        double total = 0.0;
        double result;
        int total_portion = 0;

        for (int i = 0; i < person_num; i++) {

            sum = num1 + num2; // 두 값을 더한 후
            num1 = num2;
            num2 = sum; //
            list_mother.add(sum);
            total = total + sum;

        }


        for (int i = 0; i < person_num; i++) {
            result = list_mother.get(i) / total;
            Log.d("더블", (int) result + "");
            list_portion.add((int) (result * 100));

        }
        Collections.reverse(list_portion);
        for (int i = 0; i < person_num; i++) {

            total_portion += list_portion.get(i);

        }
        if (!(total_portion == 100)) {

            for (int i = 0; i < person_num; i++) {

                list_portion.set(i, list_portion.get(i) + 1);
                if (is100(list_portion)) {
                    break;
                }


            }
        }

        return list_portion;


    }

    public static boolean is100(List<Integer> list) {

        int total_portion = 0;
        for (int i = 0; i < list.size(); i++) {

            total_portion += list.get(i);

        }
        if (total_portion == 100) {
            return true;
        } else {
            return false;

        }

    }

    public static int getStringLength(String string) {
        if (Strings.isNullOrEmpty(string)) {
            return 0;
        }
        int length = string.length();
        int charLength = 0;
        for (int i = 0; i < length; i++) {
            charLength += string.codePointAt(i) > 0x00ff ? 2 : 1;
        }
        return charLength;
    }

    public static String makeNumberComma(double num) {


        return new DecimalFormat("###,###.##").format(num);
    }

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
