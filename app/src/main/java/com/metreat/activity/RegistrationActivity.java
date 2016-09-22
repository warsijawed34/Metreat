package com.metreat.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.adapter.EventTypeListAdapter;
import com.metreat.model.EventTypeListModel;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by vinove on 3/8/16.
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener, OnWebServiceResult {

    EditText username, emailId, password,
            dob, edOccasionDate, edOccasion, edPhoneNumber, edLocationAdd1, edLocationAdd2, edCity, edPostalCode, edCountry;
    private CheckBox showAddressCheckBox, termsCheckBox, showAgeCheckBox;
    private TextView tvAddressText, tvTermsConditions;
    private ImageView showPassword, eventAdd;
    private Button login, register;
    private String strDOB, strEvent;
    public RelativeLayout rlOccasionDate, rlOccasion;
    DatePickerDialog dobDatePicker;
    boolean showPwd;
    String dateFormat, socialId,socialName,socialEmailId;
    private Context mContext;
    private ListPopupWindow eventPopupWindow;
    private LinearLayout llAddress;
    // String[] select_event = {"Wedding", "Birthday", "Anniversary"};
    CallWebService webService;
    String strYesNo, newDateFormatDOB, newDateFormatEvent, listId, listTittle,deviceToken;
    EventTypeListAdapter eventTypeListAdapter;
    LinearLayout linearLayoutEvent;
    LayoutInflater mInflater;
    View viewAdd;
    public LinearLayout llOccasion,llOccasionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_screen);
        mContext = RegistrationActivity.this;

        initView();
        myToolbar();
        addListener();
        Intent intent = getIntent();
        socialId = intent.getStringExtra("socialId");
        socialName=intent.getStringExtra("socialName");
        socialEmailId=intent.getStringExtra("socialEmailId");
        username.setText(socialName);
        emailId.setText(socialEmailId);
        deviceToken= SharedPreferencesManger.getTokenPrefValue(mContext, Constants.DEVICETOKEN, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        eventTypeListAdapter = new EventTypeListAdapter(mContext);
        eventPopupWindow = new ListPopupWindow(mContext);

        itemClickListner();

        if (showAgeCheckBox.isChecked()) {
            strYesNo = "yes";

        } else {
            strYesNo = "no";
        }
        tvTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "Please agree with<a href='http://i.vinove.com/a_me-treat/backend/web/site/terms-and-conditions'> Terms &amp; Conditions</a>";
        tvTermsConditions.setText(Html.fromHtml(text));
    }


    @Override
    protected void initView() {
        username = (EditText) findViewById(R.id.edUsername);
        emailId = (EditText) findViewById(R.id.edEmail);
        password = (EditText) findViewById(R.id.edPassword);
        dob = (EditText) findViewById(R.id.edDob);
        edPhoneNumber = (EditText) findViewById(R.id.edPhoneNo);
        tvAddressText = (TextView) findViewById(R.id.tvAddressText);
        showAddressCheckBox = (CheckBox) findViewById(R.id.cbaddAddress);
        termsCheckBox = (CheckBox) findViewById(R.id.cbTerms);
        showAgeCheckBox = (CheckBox) findViewById(R.id.cbaddAge);
        showPassword = (ImageView) findViewById(R.id.ivShowPassword);
      //  rlDob = (RelativeLayout) findViewById(R.id.rlDob);
        login = (Button) findViewById(R.id.bLoginReg);
        register = (Button) findViewById(R.id.bRegistration);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        llAddress = (LinearLayout) findViewById(R.id.llAddress);
        edLocationAdd1 = (EditText) findViewById(R.id.edLocationAdd1);
        edLocationAdd2 = (EditText) findViewById(R.id.edLocationAdd2);
        edCity = (EditText) findViewById(R.id.edCity);
        edPostalCode = (EditText) findViewById(R.id.edPostalCode);
        edCountry = (EditText) findViewById(R.id.edCountry);
        eventAdd = (ImageView) findViewById(R.id.iv_addEvent);
        tvTermsConditions= (TextView) findViewById(R.id.tvTerms);
        linearLayoutEvent = (LinearLayout) findViewById(R.id.relativeLayout);


        //add view here...
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewAdd = mInflater.inflate(R.layout.event_layout, null);
        llOccasion = (LinearLayout) viewAdd.findViewById(R.id.llOccasion);
        llOccasionDate = (LinearLayout) viewAdd.findViewById(R.id.llOccasionDate);
        edOccasion = (EditText) viewAdd.findViewById(R.id.edOccasion);
        edOccasionDate = (EditText) viewAdd.findViewById(R.id.edOccasionDate);
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

    @Override
    protected void addListener() {
        showPassword.setOnClickListener(this);
        //rlDob.setOnClickListener(this);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        dob.setOnClickListener(this);
        showAgeCheckBox.setOnClickListener(this);
        showAddressCheckBox.setOnClickListener(this);
        termsCheckBox.setOnClickListener(this);
        ivIconLeft.setOnClickListener(this);
        eventAdd.setOnClickListener(this);
        tvTermsConditions.setOnClickListener(this);

    }


    public void itemClickListner() {

        eventPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listId = eventTypeListAdapter.getItem(position).getId();
                listTittle = eventTypeListAdapter.getItem(position).getTittle();
                edOccasion.setText(listTittle);
                eventPopupWindow.dismiss();
            }
        });
    }

    @Override
    protected void myToolbar() {
        titleName.setVisibility(View.VISIBLE);
        ivIconLeft.setImageResource(R.drawable.back);
        ivIconRight.setVisibility(View.INVISIBLE);
        titleName.setText(R.string.registrationTitle);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ivIconLeft:
                onBackPressed();
                break;
            case R.id.ivShowPassword:
                if (password.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                        password.setSelection(password.getText().length());
                    } else {
                        showPwd = false;
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setSelection(password.getText().length());
                    }
                }
                break;
      /*      case R.id.rlDob:
                dateFormat = "1";
                dateOfBirth();
                break;*/
            case R.id.edDob:
                dateFormat = "1";
                dateOfBirth();
                break;
 /*           case R.id.rlOccasion:
                eventPopupWindow.setAnchorView(edOccasion);
                eventPopupWindow.setModal(true);
                eventPopupWindow.show();
                break;
            case R.id.rlOccasionDate:
                dateFormat = "2";
                dateOfEvent();
                break;*/
            case R.id.iv_addEvent:
           /*     if (counter <eventTypeListAdapter.getCount()) {
                    viewAdd = mInflater.inflate(R.layout.event_layout, null);
                    rlOccasion = (RelativeLayout) viewAdd.findViewById(R.id.rlOccasion);
                    rlOccasionDate = (RelativeLayout) viewAdd.findViewById(R.id.rlOccasionDate);
                    linearLayoutEvent.addView(viewAdd);
                    counter++;
                }*/

             /*  String date= edOccasionDate.getText().toString();
                CommonUtils.showToast(mContext,date);*/
                break;
            case R.id.bLoginReg:
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.bRegistration:
                if (validateSignUpForm()) {
                    registrationApi(strYesNo, listId, socialId, deviceToken);
                }
                break;
            case R.id.cbaddAge:
                if (showAgeCheckBox.isChecked()) {
                    strYesNo = "yes";
                    showAgeCheckBox.setButtonDrawable(R.drawable.check_box);
                } else {
                    strYesNo = "no";
                    showAgeCheckBox.setButtonDrawable(R.drawable.uncheck_box);
                }

                break;

            case R.id.cbaddAddress:
                if (showAddressCheckBox.isChecked()) {
                    tvAddressText.setVisibility(View.VISIBLE);
                    llAddress.setVisibility(View.VISIBLE);
                    showAddressCheckBox.setButtonDrawable(R.drawable.check_box);
                } else {
                    tvAddressText.setVisibility(View.GONE);
                    llAddress.setVisibility(View.GONE);
                    showAddressCheckBox.setButtonDrawable(R.drawable.uncheck_box);
                }
                break;
            case R.id.cbTerms:
                if (termsCheckBox.isChecked()) {
                    termsCheckBox.setButtonDrawable(R.drawable.check_box);
                } else {
                    termsCheckBox.setButtonDrawable(R.drawable.uncheck_box);
                }
                break;
            default:
                break;
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

    private void registrationApi(String strYesNo, String listId, String socialId, String deviceToken) {

        SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date oneWayTripDate = input.parse(dob.getText().toString());
            newDateFormatDOB=output.format(oneWayTripDate).toString();
        }catch (ParseException e) {
            e.printStackTrace();
        }


        SimpleDateFormat input1 = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat output1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date oneWayTripDate1 = input1.parse(edOccasionDate.getText().toString());
            newDateFormatEvent=output1.format(oneWayTripDate1).toString();
        }catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "registration");
                jObject.put("name", username.getText().toString());
                jObject.put("email", CommonUtils.convertUTF(emailId.getText().toString()));
                jObject.put("mobileNumber", edPhoneNumber.getText().toString());
                jObject.put("socialID", socialId);
                jObject.put("password", password.getText().toString().trim());
                jObject.put("dob", newDateFormatDOB);
                jObject.put("showAge", strYesNo);
                jObject.put("deviceToken", deviceToken);

                JSONArray eventArray = new JSONArray();
                if(!edOccasion.getText().toString().isEmpty()&& !edOccasionDate.getText().toString().isEmpty()) {
                    JSONObject eventObject = new JSONObject();
                    eventObject.put("eventID", listId);
                    eventObject.put("eventDate", newDateFormatEvent);
                    eventArray.put(eventObject);
                }

                jObject.put("event", eventArray);

                jObject.put("addressLine1", CommonUtils.convertUTF(edLocationAdd1.getText().toString()));
                jObject.put("addressLine2", CommonUtils.convertUTF(edLocationAdd2.getText().toString()));
                jObject.put("city", CommonUtils.convertUTF(edCity.getText().toString()));
                jObject.put("postalcode", edPostalCode.getText().toString());
                jObject.put("country", CommonUtils.convertUTF(edCountry.getText().toString()));
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.REGISTRATION, this);
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

        switch (type) {
            case REGISTRATION:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERID, JSONUtils.getStringFromJSON(jsonObject, "userId"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.MOBILENUMBER, JSONUtils.getStringFromJSON(jsonObject, "mobileNumber"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        Intent intent = new Intent(RegistrationActivity.this, OtpActivity.class);
                        intent.putExtra("otpType", "register");
                        intent.putExtra("emailID",emailId.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        //Account doesn't exists
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case EVENTTYPELIST:
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
                break;

        }
    }

   /*
     do not delete this code..
   private void PopupWindow() {
        eventPopupWindow = new ListPopupWindow(mContext);
        eventPopupWindow.setAdapter(new ArrayAdapter(mContext, R.layout.list_item, R.id.rowTextView, select_event));
        eventPopupWindow.setAnchorView(edOccasion);
        eventPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        eventPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        //timePopupWindow.setHeight(500);T);
        eventPopupWindow.setModal(true);
        eventPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edOccasion.setText(select_event[position]);
                eventPopupWindow.dismiss();
            }
        });
    }*/

    //date picker for  past dates
    private void dateOfBirth() {
        dobDatePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if(dateFormat.equalsIgnoreCase("1")) {
                    strDOB = dateFormatResult.format(newDate.getTime());
                    dob.setText(strDOB);
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

    private void dateOfEvent() {
        dobDatePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                    strEvent = dateFormatResultOccasion.format(newDate.getTime());
                    edOccasionDate.setText(strEvent);
            }
        },
                newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dobDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        dobDatePicker.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }


    private boolean validateSignUpForm() {
        if (username.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, getResources().getString(R.string.userValidation));
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.getText().toString()).matches()) {
            if (emailId.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.emailValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.validate_email));
            return false;
        }

        if (password.getText().toString().trim().isEmpty() || password.length() < 6 || password.length() > 15) {
            if (password.getText().toString().trim().isEmpty()) {
                CommonUtils.showToast(mContext, getResources().getString(R.string.passwordValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getResources().getString(R.string.validate_password));
            return false;
        }

        if (edPhoneNumber.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, getResources().getString(R.string.mobileNumberValidation));
            return false;
        }

        if (!edPhoneNumber.getText().toString().isEmpty()) {
            if ((edPhoneNumber.getText().toString().trim().length() < 6) || (edPhoneNumber.getText()
                    .toString().trim().length() > 15)) {
                CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.msg_mobile));
                return false;
            }
        }

        if (dob.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, getResources().getString(R.string.dobValidate));
            return false;
        }

      /*  if (edOccasion.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, getResources().getString(R.string.otherEventsValidation));
            return false;
        }


        if (edOccasionDate.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, getResources().getString(R.string.eventsDateValidation));
            return false;
        }*/

        if (showAddressCheckBox.isChecked()) {

            if (edLocationAdd1.getText().toString().trim().isEmpty()) {
                CommonUtils.showToast(mContext, getResources().getString(R.string.address1Validation));
                return false;
            }
            if (edLocationAdd2.getText().toString().trim().isEmpty()) {
                CommonUtils.showToast(mContext, getResources().getString(R.string.address2Validation));
                return false;
            }
            if (edCity.getText().toString().trim().isEmpty()) {
                CommonUtils.showToast(mContext, getResources().getString(R.string.cityValidation));
                return false;
            }
            if (edPostalCode.getText().toString().trim().isEmpty()) {
                CommonUtils.showToast(mContext, getResources().getString(R.string.postalCodeValidation));
                return false;
            }
            if (edCountry.getText().toString().trim().isEmpty()) {
                CommonUtils.showToast(mContext, getResources().getString(R.string.countryValidation));
                return false;
            }
        }



        if (termsCheckBox.isChecked() != true) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.checkbox_msg_validation));
            return false;

        }
        return true;
    }


}
