package com.metreat.utils;

/**
 * Created by Jawed on 8/8/16.
 */
public final class Constants {



    public enum SERVICE_TYPE {
        LOGIN, FORGETPASSWORD, CHANGEPASSWORD, REGISTRATION, OTP, EVENTTYPELIST, RESENDOTP, UPDATEPROFILE,
        SYNCCONTACTS, COUPONLIST, COUPONCAT, RECIEVEDCOUPON, GIFTEDCOUPON, SETTING, NOTIFICATION,
        CHECKOUT, RESETPASSWORD, SENDREQUEST, ACCEPTREQUEST, CANCELFFRIEND, REMOVEFRIEND, REJECTREQUEST, LOGOUT;

    }

    //setting
    public static final String TOGGLEBUTTON = "toggleButton";

    //login
    public static final String USERID = "userId";
    public static final String USERNAME = "userName";
    public static final String EMAILID = "emailId";
    public static final String IMAGES = "image";
    public static final String EVENTTYPE = "eventType";
    public static final String EVENTDATE = "eventDate";
    public static final String EVENTID = "eventId";
    public static final String ADDRESS = "address";
    public static final String ADDRESSLINE1 = "addressLine1";
    public static final String ADDRESSLINE2 = "addressLine2";
    public static final String CITY = "city";
    public static final String POSTALCODE = "postalCode";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String MOBILENUMBER = "mobileNumber";
    public static final String TOKENID = "tokenId";
    public static final String DOB = "dateOfBirth";
    public static final String EVENT = "EVENT";
    public static final String DEVICETOKEN = "devicetoken";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PAYPAL_CLIENT_ID = "YOUR PAYPAL CLIENT ID";


}
