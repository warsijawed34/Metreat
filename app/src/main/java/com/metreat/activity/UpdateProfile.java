package com.metreat.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.adapter.EventTypeListAdapter;
import com.metreat.imageLoading.ImageLoader;
import com.metreat.imageLoading.ImageViewRounded;
import com.metreat.model.EventTypeListModel;
import com.metreat.model.GiftedCouponsModel;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by vinove on 4/8/16.
 */
public class UpdateProfile extends BaseActivityDrawerMenu implements View.OnClickListener, OnWebServiceResult,  ActivityCompat.OnRequestPermissionsResultCallback {
    Context mContext;
    TextView tvUploadPhoto;
    EditText etUserName, etEmail,etMobileNumber, etDOB,
            etCity, etCountry,etAddessLine1,etAddressLine2,etPostalCode;
    Button btnSave;
    Toolbar toolbar;
    DatePickerDialog dobDatePicker, specialDatePicker;
    String strDOB, strSpecialDate, strUserId, strTokenId, strEvent;
    AlertDialog alertD;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_KITKAT_GALLERY = 2;
    private static final int REQUEST_CAMERA = 3;
    private String imagepath, encodedImage = "";
    Bitmap bMap;
    ImageViewRounded ivProfilePhoto;
    CallWebService webService;
    ImageLoader imgL;
    String StrProfilePic, listId, listTittle,dateFormat, dateFormateEvent;
    LinearLayout linearLayoutEvent;
    LayoutInflater mInflater;
    View viewAdd;
    RelativeLayout rlOccasionDate, rlOccasion;
    EditText edOccasion,edOccasionDate;
    ImageView eventAdd;
    ListPopupWindow eventPopupWindow;
    EventTypeListAdapter eventTypeListAdapter;
    static int counter=1;
    String newDateFormatDOB, newDateFormatEvent;
    String ProfileName,emailId,dateOfBirth, mobileNumber;
    LinearLayout llOccasionDate,llOccasion;
    String eventId,eventType,eventDate, addressLine1,addressLine2,city,postalCode,state,country;
    private static String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};
    private static final int REQUEST_CAMERA_PERMISSION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        mContext = UpdateProfile.this;
        initView();
        myToolbar();
        addListener();
        setTextUpdate();
     //   requestCameraPermissions();

        eventTypeListAdapter = new EventTypeListAdapter(mContext);
        eventPopupWindow = new ListPopupWindow(mContext);
        itemClickListner();

    }


    @Override
    protected void initView() {
        ivProfilePhoto = (ImageViewRounded) findViewById(R.id.iv_update_photo);
        tvUploadPhoto = (TextView) findViewById(R.id.tv_upload_photo);
        etUserName = (EditText) findViewById(R.id.et_username);
        etEmail = (EditText) findViewById(R.id.et_email);
        etDOB = (EditText) findViewById(R.id.et_dob);
        etMobileNumber= (EditText) findViewById(R.id.et_mobileNumber);
        etAddessLine1= (EditText) findViewById(R.id.et_addressLine1);
        etAddressLine2= (EditText) findViewById(R.id.et_addressLine2);
        etPostalCode= (EditText) findViewById(R.id.et_postalCode);
        etCity = (EditText) findViewById(R.id.et_city);
        etCountry = (EditText) findViewById(R.id.et_country);
        btnSave = (Button) findViewById(R.id.btn_save_continue);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
     //   rlDob= (RelativeLayout) findViewById(R.id.rl_dob);
        eventAdd= (ImageView) findViewById(R.id.iv_addEvent);
        linearLayoutEvent = (LinearLayout) findViewById(R.id.relativeLayoutUpdate);


        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewAdd = mInflater.inflate(R.layout.event_layout,null);
        llOccasion = (LinearLayout) viewAdd.findViewById(R.id.llOccasion);
        llOccasionDate = (LinearLayout) viewAdd.findViewById(R.id.llOccasionDate);
        edOccasion= (EditText) viewAdd.findViewById(R.id.edOccasion);
        edOccasionDate= (EditText) viewAdd.findViewById(R.id.edOccasionDate);
        eventTypeListApi();
        ViewClickListner();
        linearLayoutEvent.addView(viewAdd);


    }

    private void ViewClickListner() {
        edOccasion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventPopupWindow.setAnchorView(edOccasion);
                eventPopupWindow.setModal(true);
                eventPopupWindow.show();
            }
        });
        edOccasionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFormat = "2";
                dateOfBirth();
            }
        });
    }

    private void itemClickListner() {
        eventPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listId = eventTypeListAdapter.getItem(position).getId();
                listTittle=eventTypeListAdapter.getItem(position).getTittle();
                edOccasion.setText(listTittle);
                eventPopupWindow.dismiss();
            }
        });
    }



    @Override
    protected void addListener() {
        btnSave.setOnClickListener(this);
       // rlDob.setOnClickListener(this);
       // etSpecialDates.setOnClickListener(this);
        ivIconLeft.setOnClickListener(this);
        ivIconRight.setOnClickListener(this);
        tvUploadPhoto.setOnClickListener(this);
        eventAdd.setOnClickListener(this);
        etDOB.setOnClickListener(this);

    }

    @Override
    protected void myToolbar() {
        ivIconLeft.setVisibility(View.VISIBLE);
        ivIconRight.setVisibility(View.VISIBLE);
        titleName.setText(R.string.updateProfile);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_save_continue:
              if(validateUpdate()){
                  updateApi(strUserId, strTokenId ,listId);
                }
                break;
        /*    case R.id.rl_dob:
                dateFormat="1";
                dateOfBirth();
                break;*/
            case R.id.et_dob:
                dateFormat = "1";
                dateOfBirth();
                break;
            case R.id.ivIconLeft:
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ivIconRight:
                intent = new Intent(mContext, NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tv_upload_photo:
                choosePopup();
                break;
            case R.id.iv_addEvent:
           /*     if (counter <eventTypeListAdapter.getCount()) {
                    viewAdd = mInflater.inflate(R.layout.event_layout, null);
                    rlOccasion = (RelativeLayout) viewAdd.findViewById(R.id.rlOccasion);
                    rlOccasionDate = (RelativeLayout) viewAdd.findViewById(R.id.rlOccasionDate);
                    linearLayoutEvent.addView(viewAdd);
                    counter++;
                }*/

             /*   String date=edOccasionDate.getText().toString();
                CommonUtils.showToast(mContext,date);*/
                break;
            default:
                break;
        }
    }

    private void setTextUpdate() {
        strUserId= SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        strTokenId= SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        StrProfilePic= SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        ProfileName= SharedPreferencesManger.getPrefValue(mContext, Constants.USERNAME, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        emailId= SharedPreferencesManger.getPrefValue(mContext, Constants.EMAILID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        dateOfBirth= SharedPreferencesManger.getPrefValue(mContext, Constants.DOB, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        mobileNumber= SharedPreferencesManger.getPrefValue(mContext, Constants.MOBILENUMBER, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        eventType= SharedPreferencesManger.getPrefValue(mContext, Constants.EVENTTYPE, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        eventId= SharedPreferencesManger.getPrefValue(mContext, Constants.EVENTID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        eventDate= SharedPreferencesManger.getPrefValue(mContext, Constants.EVENTDATE, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        addressLine1= SharedPreferencesManger.getPrefValue(mContext, Constants.ADDRESSLINE1, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        addressLine2= SharedPreferencesManger.getPrefValue(mContext, Constants.ADDRESSLINE2, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        city= SharedPreferencesManger.getPrefValue(mContext, Constants.CITY, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        postalCode= SharedPreferencesManger.getPrefValue(mContext, Constants.POSTALCODE, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        country= SharedPreferencesManger.getPrefValue(mContext, Constants.COUNTRY, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        listId=eventId;

        etUserName.setText(ProfileName);
        etEmail.setText(emailId);
        etMobileNumber.setText(mobileNumber);
        edOccasion.setText(eventType);
        edOccasionDate.setText(eventDate);
        etAddessLine1.setText(addressLine1);
        etAddressLine2.setText(addressLine2);
        etCity.setText(city);
        etPostalCode.setText(postalCode);
        etCountry.setText(country);


        //change date formate
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date oneWayTripDate = input.parse(dateOfBirth);
            etDOB.setText(output.format(oneWayTripDate));
        }catch (ParseException e) {
            e.printStackTrace();
        }

        //change date formate
        SimpleDateFormat input1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output1 = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date oneWayTripDate = input1.parse(eventDate);
            edOccasionDate.setText(output1.format(oneWayTripDate));
        }catch (ParseException e) {
            e.printStackTrace();
        }


        if (!StrProfilePic.isEmpty()){
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(StrProfilePic, ivProfilePhoto);
        }else {
            ivProfilePhoto.setImageResource(R.drawable.profile);
        }
    }


    private void updateApi(String strUserId, String strTokenId,String listId) {

        Calendar newDate = Calendar.getInstance();
        newDateFormatDOB = etDOB.getText().toString();
        newDateFormatDOB = newDateFomate.format(newDate.getTime());

        SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date oneWayTripDate = input.parse(edOccasionDate.getText().toString());
            newDateFormatEvent=output.format(oneWayTripDate).toString();
        }catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "updateProfile");
                jObject.put("userID", strUserId);
                jObject.put("image", encodedImage);
                jObject.put("name",  etUserName.getText().toString());
                jObject.put("email", CommonUtils.convertUTF(etEmail.getText().toString()));
                jObject.put("dob", newDateFormatDOB);
                jObject.put("mobileNumber", etMobileNumber.getText().toString());
                jObject.put("addressLine1", CommonUtils.convertUTF(etAddessLine1.getText().toString()));
                jObject.put("addressLine2", CommonUtils.convertUTF(etAddressLine2.getText().toString()));
                jObject.put("city",CommonUtils.convertUTF(etCity.getText().toString()));
                jObject.put("postalcode",etPostalCode.getText().toString());
                jObject.put("country", CommonUtils.convertUTF(etCountry.getText().toString()));

                JSONArray eventArray = new JSONArray();
                if(!edOccasionDate.getText().toString().isEmpty() && !edOccasion.getText().toString().isEmpty()) {
                    JSONObject eventObject = new JSONObject();
                    eventObject.put("eventID", listId);
                    eventObject.put("eventDate", newDateFormatEvent);
                    eventArray.put(eventObject);
                }
                jObject.put("event", eventArray);

                jObject.put("tokenID", strTokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.UPDATEPROFILE, this);
                webService.execute();
            } else {
                CommonUtils.showToast(mContext, getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eventTypeListApi() {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "eventTypeList");
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.EVENTTYPELIST, this);
                webService.execute();
            } else {
                CommonUtils.showToast(mContext, getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type){
            case UPDATEPROFILE:
                if(result!=null) {
                    try {
                        System.out.println("Result: " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                        if (code == 200) {
                            JSONObject userData = JSONUtils.getJSONObjectFromJSON(jsonObject, "data");
                            SharedPreferencesManger.setPrefValue(mContext, Constants.USERID, JSONUtils.getStringFromJSON(userData, "userID"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.USERNAME, JSONUtils.getStringFromJSON(userData, "name"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.EMAILID, JSONUtils.getStringFromJSON(userData, "email"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.DOB, JSONUtils.getStringFromJSON(userData, "dob"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.IMAGES, JSONUtils.getStringFromJSON(userData, "image"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.ADDRESS, JSONUtils.getStringFromJSON(userData, "address"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.TOKENID, JSONUtils.getStringFromJSON(userData, "tokenID"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.MOBILENUMBER, JSONUtils.getStringFromJSON(userData, "mobileNumber"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);

                            JSONObject addressObject = JSONUtils.getJSONObjectFromJSON(userData, "addressParts");
                            SharedPreferencesManger.setPrefValue(mContext, Constants.ADDRESSLINE1, JSONUtils.getStringFromJSON(addressObject, "addressLine1"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.ADDRESSLINE2, JSONUtils.getStringFromJSON(addressObject, "addressLine2"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.CITY, JSONUtils.getStringFromJSON(addressObject, "city"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.POSTALCODE, JSONUtils.getStringFromJSON(addressObject, "postalcode"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.STATE, JSONUtils.getStringFromJSON(addressObject, "state"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.COUNTRY, JSONUtils.getStringFromJSON(addressObject, "country"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);


                            JSONArray eventArray = JSONUtils.getJSONArrayFromJSON(userData, "event");
                            for (int i = 0; i < eventArray.length(); i++) {
                                JSONObject eventObject = eventArray.getJSONObject(i);
                                // eventId=JSONUtils.getStringFromJSON(eventObject,"id");
                                SharedPreferencesManger.setPrefValue(mContext, Constants.EVENTID, JSONUtils.getStringFromJSON(eventObject, "id"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                                SharedPreferencesManger.setPrefValue(mContext, Constants.EVENTTYPE, JSONUtils.getStringFromJSON(eventObject, "eventType"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                                SharedPreferencesManger.setPrefValue(mContext, Constants.EVENTDATE, JSONUtils.getStringFromJSON(eventObject, "eventDate"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            }

                            CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                            Intent intent = new Intent(UpdateProfile.this, MyProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(0, R.anim.exit_slide_right);

                        } else {
                            CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    CommonUtils.showToast(mContext,getString(R.string.dataNotFound));
                }
                break;
            case EVENTTYPELIST:
                if(result!=null) {
                    try {
                        System.out.println("Result: " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                        if (code == 200) {
                            JSONArray jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "eventTypeList");
                            JSONObject eventListObject;
                            EventTypeListModel model;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                eventListObject = jsonArray.getJSONObject(i);
                                model = new EventTypeListModel();
                                model.setId(JSONUtils.getStringFromJSON(eventListObject, "id"));
                                model.setTittle(JSONUtils.getStringFromJSON(eventListObject, "title"));
                                eventTypeListAdapter.addToArrayList(model);
                            }
                            eventPopupWindow.setAdapter(eventTypeListAdapter);
                            eventTypeListAdapter.notifyDataSetChanged();

                        } else {
                            CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    CommonUtils.showToast(mContext,getString(R.string.dataNotFound));
                }
                break;
        }

    }

    public void choosePopup() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_update_photo, null);

        alertD = new AlertDialog.Builder(this).create();


        Button btnGallery = (Button) promptView.findViewById(R.id.btn_gallery);

        Button btnCamera = (Button) promptView.findViewById(R.id.btn_camera);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getGallery();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getCamera();

            }
        });
        alertD.setView(promptView);
        alertD.show();
    }

    public void getGallery() {
        alertD.dismiss();
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image*//*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_KITKAT_GALLERY);
        }
    }

    public void getCamera() {
        alertD.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = null;
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (data == null || data.getData() == null)
                    return;
                imagepath = getRealPathFromURI(selectedImageUri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                // options.inJustDecodeBounds = true;
                options.inScaled = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                bMap = BitmapFactory.decodeFile(imagepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();

                ivProfilePhoto.setImageBitmap(bMap);
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                break;
            case REQUEST_KITKAT_GALLERY:
                if (data == null || data.getData() == null)
                    return;
                selectedImageUri = data.getData();
                imagepath = getRealPathFromURI(selectedImageUri);
                BitmapFactory.Options options1 = new BitmapFactory.Options();
                options1.inSampleSize = 2;
                // options.inJustDecodeBounds = true;
                options1.inScaled = false;
                options1.inDither = false;
                options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bMap = BitmapFactory.decodeFile(imagepath);

                ByteArrayOutputStream byteArrayOutputStreamaos = new ByteArrayOutputStream();

                bMap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamaos);
                byte[] imageBytes1 = byteArrayOutputStreamaos.toByteArray();

                ivProfilePhoto.setImageBitmap(bMap);
                encodedImage = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                break;
            case REQUEST_CAMERA:
                try {
                    if (resultCode == Activity.RESULT_CANCELED) {
                        return;
                    }
                    bMap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

                    bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                    byte[] imageBytes2 = baos2.toByteArray();

                    encodedImage = Base64.encodeToString(imageBytes2, Base64.DEFAULT);
                    ivProfilePhoto.setImageBitmap(bMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void dateOfBirth() {
        dobDatePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                if(dateFormat.equalsIgnoreCase("1")) {
                    strDOB = dateFormatResult.format(newDate.getTime());
                    etDOB.setText(strDOB);
                }
                else if(dateFormat.equalsIgnoreCase("2")){
                    strEvent = dateFormatResult.format(newDate.getTime());
                    edOccasionDate.setText(strEvent);

                }
            }

        },
                newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dobDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        dobDatePicker.show();
    }

    private void requestCameraPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA_PERMISSION);
            } else {
                CommonUtils.showToast(mContext, getString(R.string.already_granted));
            }
        } else {
            CommonUtils.showToast(mContext, getString(R.string.already_granted));

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // do something here..
            Toast.makeText(mContext, "Camera permissions granted.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "Camera permissions were not granted.", Toast.LENGTH_LONG).show();
        }
    }

    //validation
    private boolean validateUpdate() {

        if (etUserName.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.userValidation));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            if (etEmail.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.emailValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.validate_email));
            return false;
        }

        if (etMobileNumber.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.mobileNumberValidation));
            return false;
        }

        if (!etMobileNumber.getText().toString().isEmpty()) {
            if ((etMobileNumber.getText().toString().trim().length() < 6) || (etMobileNumber.getText()
                    .toString().trim().length() > 15)) {
                CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_mobile));
                return false;
            }
        }

        if (etDOB.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.dobValidate));
            return false;
        }



/*        if (etAddessLine1.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.validate_emptyField));
            return false;
        }
        if (etAddressLine2.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.validate_emptyField));
            return false;
        }
        if (etPostalCode.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.validate_emptyField));
            return false;
        }

        if (etCity.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.validate_emptyField));
            return false;
        }
        if (etCountry.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.validate_emptyField));
            return false;
        }*/
        return true;

    }


}
