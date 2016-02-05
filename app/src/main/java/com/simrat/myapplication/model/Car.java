package com.simrat.myapplication.model;

/**
 * Created by simrat on 15/11/15.
 */
public class Car {

    int id;
    int user_id;
    String name;
    int seats;
    String reg_no;

    public Car(){

    }
    public Car(int user_id, String name, int seats, String reg_no){
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.seats = seats;
        this.reg_no = reg_no;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return this.user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeats() {
        return this.seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getReg_no() {
        return this.reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }
}
