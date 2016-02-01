package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class Slot {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public Slot setUrl(String url) {
        this.url = url;
        return this;
    }

    public Slot setTimestamps(List<String> timestamps) {
        this.timestamps = timestamps;
        return this;
    }

    public Slot setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    private String status;
    private String url;
    private List<String> timestamps;
    private Integer quantity;

    public Slot(String status) {
        this.status = status;
        this.url = null;
        this.timestamps = new ArrayList<>();
        this.quantity = 1;
    }

}
