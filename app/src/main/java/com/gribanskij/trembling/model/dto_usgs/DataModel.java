package com.gribanskij.trembling.model.dto_usgs;

import java.util.List;

public class DataModel {

    private String type;
    private Metadata metadata;
    private String[] bbox;
    private List<Features> features;


    public String[] getBbox() {
        return bbox;
    }

    public void setBbox(String[] bbox) {
        this.bbox = bbox;
    }

    public List<Features> getFeatures() {
        return features;
    }

    public void setFeatures(List<Features> features) {
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "ClassPojo [bbox = " + bbox + ", features = " + features + ", type = " + type + ", metadata = " + metadata + "]";
    }
}
