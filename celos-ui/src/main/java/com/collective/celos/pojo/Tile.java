package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tile {

    private final String status;
    private final String url;
    private final List<String> timestamps;
    private final Integer quantity;

    private Tile(String status, String url, List<String> timestamps, Integer quantity) {
        this.status = status;
        this.url = url;
        this.timestamps = Collections.unmodifiableList(timestamps);
        this.quantity = quantity;
    }

    public Tile(String status) {
        this(status, null, Collections.emptyList(), 1);
    }

    public String getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public Tile withUrl(String url) {
        return new Tile(this.status, url, this.timestamps, this.quantity);
    }

    public Tile withTimestamps(List<String> timestamps) {
        return new Tile(this.status, this.url, timestamps, this.quantity);
    }

    public Tile withQuantity(Integer quantity) {
        return new Tile(this.status, this.url, this.timestamps, quantity);
    }

}
