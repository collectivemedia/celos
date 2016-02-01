package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class Slot {

    private Slot(String status, String url, List<String> timestamps, Integer quantity) {
        this.status = status;
        this.url = url;
        this.timestamps = timestamps;
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public Slot withUrl(String url) {
        return new Slot(this.status, url, this.timestamps, this.quantity);
    }

    public Slot withTimestamps(List<String> timestamps) {
        return new Slot(this.status, this.url, timestamps, this.quantity);
    }

    public Slot withQuantity(Integer quantity) {
        return new Slot(this.status, this.url, this.timestamps, quantity);
    }

    public Slot(String status) {
        this.status = status;
        this.url = null;
        this.timestamps = new ArrayList<>();
        this.quantity = 1;
    }

    private final String status;
    private final String url;
    private final List<String> timestamps;
    private final Integer quantity;

}
