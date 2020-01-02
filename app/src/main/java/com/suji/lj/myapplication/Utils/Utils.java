package com.suji.lj.myapplication.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

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
import com.google.gson.GsonBuilder;
import com.kakao.usermgmt.response.model.User;
import com.suji.lj.myapplication.Adapters.DBHelper;
import com.suji.lj.myapplication.Items.ContactItem;
import com.suji.lj.myapplication.Items.UserAccountItem;
import com.suji.lj.myapplication.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class Utils {

    public Utils(){

    }
    public static String getParamValFromUrlString(String url, String paramKey){

        Log.d("## url", url);
        String[] urlParamPair = url.split("\\?");
        if(urlParamPair.length < 2){
            Log.d("##", "파라미터가 존재하지 않는 URL 입니다.");
            return "";
        }
        String queryString = urlParamPair[1];
        Log.d("## queryString", queryString);
        StringTokenizer st = new StringTokenizer(queryString, "&");
        while(st.hasMoreTokens()){
            String pair = st.nextToken();
            Log.d("## pair", pair);
            String[] pairArr = pair.split("=");
            if(paramKey.equals(pairArr[0])){
                return pairArr[1]; // 찾았을 경우
            }
        }
        return "";
    }


    public ArrayList<ContactItem> getContacts(){
        ArrayList<ContactItem> listItem = new ArrayList<>();
        Query q = Contacts.getQuery();
        q.include(Contact.Field.ContactId, Contact.Field.DisplayName, Contact.Field.PhoneNumber);
        List<Contact> result = q.find();

        try {
            String con = new GsonBuilder().setPrettyPrinting().create().toJson(result);
            Log.d("연락처",con);
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
    public static String makeBankTranId(){

        return getServiceCode()+"U"+ new SimpleDateFormat("HHmmssSSS",Locale.KOREA).format(new Date());


    }

    public static String getServiceCode(){


        String service_code;
        service_code = "T991596920";
        return service_code;



    }

    public static List<UserAccountItem> UserInfoResponseJsonParse(String body){
        List<UserAccountItem> userAccountItemList = new ArrayList<>();
        Log.d("오픈뱅킹","리스트만들기 ");

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




    public static void getKeyHash(Context context){
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


    public static void volleyConnection(Context context, HashMap<String,String> hashMap, VolleyResponseListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request;
        String getURL = "http://bishop130.cafe24.com/Mission_List.php";

        request = new StringRequest(Request.Method.POST, getURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("서비스", "전송성공");
                listener.onResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());

                Toast.makeText(context, "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //HashMap<String, String> hashMap = new HashMap<String, String>();

               // hashMap.put("token", token);
               // hashMap.put("user_id", user_id);

                return hashMap;
            }
        };
        requestQueue.add(request);


    }
    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(Object response);
    }


    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
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


    public static void refreshDatabase(Context context, final String user_id) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String refreshDatabaseURL = "http://bishop130.cafe24.com/refreshDB.php";

        DBHelper dbHelper = new DBHelper(context, "alarm_manager.db", null, 5);
        StringRequest request = new StringRequest(Request.Method.POST, refreshDatabaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("메인", "DB_refresh");
                String sql = "DELETE FROM alarm_table";
                dbHelper.selectData(sql);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String mission_id = jsonObject.getString("mission_id");
                        String time = jsonObject.getString("mission_time");
                        String lat = jsonObject.getString("mission_lat");
                        String lng = jsonObject.getString("mission_lng");
                        String title = jsonObject.getString("mission_name");
                        String date = jsonObject.getString("time");
                        String date_time = date + " " + time;

                        String query = "INSERT INTO alarm_table VALUES(null,'" + time + "','" + lat + "','" + lng + "','" +
                                mission_id + "','" + date + "','" + date_time + "','false','" + title + "','" + user_id + "')";
                        Log.d("메인", "   lat:" + String.valueOf(lat) + "   " +
                                "lng:" + String.valueOf(lng) + "     time:" + time + "     mission_id:" + mission_id + "   " +
                                "  date:" + date + "     date+time:" + date_time + "      title:" + title + "       user_id:" + user_id);
                        dbHelper.insert(query);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(context, "전송실패" + error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("user_id", user_id);

                return hashMap;
            }
        };
        requestQueue.add(request);

    }

    public static String getCurrentTimeForBankNum() {
        return new SimpleDateFormat("HHmmssSSS", Locale.KOREA).format(new Date());
    }

    public static String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    public static Bitmap getBitmapFromString(String bitmap_string){

        byte[] decodedByteArray = Base64.decode(bitmap_string, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public static boolean compareNowAndInput(int sel_hour, int sel_min){

        DateFormat dateFormat = new SimpleDateFormat("HH:mm",Locale.KOREA);

        int now_hour = Calendar.getInstance().get(Calendar.HOUR);
        int now_min = Calendar.getInstance().get(Calendar.MINUTE);
        long min=0;

        try {

            Date date_now = dateFormat.parse(now_hour+":"+now_min);
            Date date_input = dateFormat.parse(sel_hour+":"+sel_min); //String을 포맷에 맞게 변경
            long duration = date_input.getTime() - date_now.getTime(); // 입력시간-현재시간
            min = duration/60000;
            Log.d("싱글",min+"duration");


        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (min>=30) { // 30분이상 지났을때

            return true;
        }else{
            return false;
        }


    }

    public static boolean checkIsToday(int sel_year, int sel_month, int sel_day){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        Log.d("싱글",year+"year");
        Log.d("싱글",month+"month");
        Log.d("싱글",day+"day");
        Log.d("싱글",sel_year+"sel_year");
        Log.d("싱글",sel_month+"ser_month");
        Log.d("싱글",sel_day+"sel_day");

        if((year==sel_year)&&(month==sel_month)&&(day==sel_day)){
            return true;
        }else{
            return false;
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
        } else if(unit == "meter"){
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


}
