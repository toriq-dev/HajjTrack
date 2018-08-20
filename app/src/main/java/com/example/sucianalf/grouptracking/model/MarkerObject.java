package com.example.sucianalf.grouptracking.model;

public class MarkerObject {
    private String id;
    private String value;
    private int weight;
    private String polyLine;
    private String time;
    private String set_by;
    private String lokasi;
    private String placeName;
    private String set_time;
    private String image;
    private int index;

    public MarkerObject() {

    }

    public MarkerObject(String id, String value, int weight) {
        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    public MarkerObject(String id, String value, int weight, String polyLine) {
        this.id = id;
        this.value = value;
        this.weight = weight;
        this.polyLine = polyLine;
    }

    public MarkerObject(String id, int index, String value, int weight, String polyLine) {
        this.id = id;
        this.value = value;
        this.weight = weight;
        this.index = index;
        this.polyLine = polyLine;
    }

    public MarkerObject(String id, String value, String time, String image) {
        this.id = id;
        this.value = value;
        this.time = time;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(String polyLine) {
        this.polyLine = polyLine;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSet_by() {
        return set_by;
    }

    public void setSet_by(String set_by) {
        this.set_by = set_by;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getSet_time() {
        return set_time;
    }

    public void setSet_time(String set_time) {
        this.set_time = set_time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}