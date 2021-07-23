package com.something.vendorapp.model;

public class OrderPlaced {

    String order_address, order_items, order_key, order_status, order_date, all_order_key, user_key;

    public OrderPlaced() {}

    public OrderPlaced(String order_address, String order_items, String order_key, String order_status, String order_date) {
        this.order_address = order_address;
        this.order_items = order_items;
        this.order_key = order_key;
        this.order_status = order_status;
        this.order_date = order_date;
    }

    public OrderPlaced(String order_address, String order_items, String order_key, String order_status, String order_date, String all_order_key, String user_key) {
        this.order_address = order_address;
        this.order_items = order_items;
        this.order_key = order_key;
        this.order_status = order_status;
        this.order_date = order_date;
        this.all_order_key = all_order_key;
        this.user_key = user_key;
    }

    public String getOrder_address() {
        return order_address;
    }

    public void setOrder_address(String order_address) {
        this.order_address = order_address;
    }

    public String getOrder_items() {
        return order_items;
    }

    public void setOrder_items(String order_items) {
        this.order_items = order_items;
    }

    public String getOrder_key() {
        return order_key;
    }

    public void setOrder_key(String order_key) {
        this.order_key = order_key;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getAll_order_key() {
        return all_order_key;
    }

    public void setAll_order_key(String all_order_key) {
        this.all_order_key = all_order_key;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }
}
