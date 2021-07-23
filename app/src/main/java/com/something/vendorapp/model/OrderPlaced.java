package com.something.vendorapp.model;

public class OrderPlaced {

    String order_address, order_items, order_key, order_status, order_date;

    public OrderPlaced() {}

    public OrderPlaced(String order_address, String order_items, String order_key, String order_status, String order_date) {
        this.order_address = order_address;
        this.order_items = order_items;
        this.order_key = order_key;
        this.order_status = order_status;
        this.order_date = order_date;
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

}
