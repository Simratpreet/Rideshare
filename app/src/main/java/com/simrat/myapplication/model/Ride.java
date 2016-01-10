package com.simrat.myapplication.model;

/**
 * Created by simrat on 11/1/16.
 */
public class Ride {
    int id;
    int user_id;
    int car_id;
    String source;
    String destination;
    String datetime;
    String price;
    String allowed_pass;

    public Ride(){

    }
    public Ride(int user_id, int car_id, String source, String destination, String datetime, String price, String allowed_pass){
        this.user_id = user_id;
        this.car_id = car_id;
        this.source = source;
        this.destination = destination;
        this.datetime = datetime;
        this.price = price;
        this.allowed_pass = allowed_pass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getPrice() {
        return price;
    }

    public String getAllowed_pass() {
        return allowed_pass;
    }

    public void setAllowed_pass(String allowed_pass) {
        this.allowed_pass = allowed_pass;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
