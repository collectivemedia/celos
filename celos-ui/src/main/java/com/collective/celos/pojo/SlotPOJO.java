package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class SlotPOJO {

    // fields
    public String status;
    public String url;
    public List<String> timestamps;
    public Integer quantity;

    public SlotPOJO setUrl(String url) {
        this.url = url;
        return this;
    }

    public SlotPOJO setTimestamps(List<String> timestamps) {
        this.timestamps = timestamps;
        return this;
    }

    public SlotPOJO setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    // constructor
    public SlotPOJO(String status) {
        this.status = status;
        this.url = null;
        this.timestamps = new ArrayList<>();
        this.quantity = 1;
    }

}
