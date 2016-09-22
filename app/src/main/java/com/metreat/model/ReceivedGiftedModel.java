package com.metreat.model;

/**
 * Created by jawed on 8/8/16.
 */
public class ReceivedGiftedModel {

    String couponId;
    String cName;
    String cPrice;
    String cBoughtDate;
    String giftedTo;
    String birthDayAniv;


    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcPrice() {
        return cPrice;
    }

    public void setcPrice(String cPrice) {
        this.cPrice = cPrice;
    }

    public String getcBoughtDate() {
        return cBoughtDate;
    }

    public void setcBoughtDate(String cBoughtDate) {
        this.cBoughtDate = cBoughtDate;
    }

    public String getGiftedTo() {
        return giftedTo;
    }

    public void setGiftedTo(String giftedTo) {
        this.giftedTo = giftedTo;
    }

    public String getBirthDayAniv() {
        return birthDayAniv;
    }

    public void setBirthDayAniv(String birthDayAniv) {
        this.birthDayAniv = birthDayAniv;
    }
}
