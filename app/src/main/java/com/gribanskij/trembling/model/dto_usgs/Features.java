package com.gribanskij.trembling.model.dto_usgs;

public class Features {

    private String id;
    private String type;
    private Properties properties;
    private Geometry geometry;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", properties = " + properties + ", type = " + type + ", geometry = " + geometry + "]";
    }
}
