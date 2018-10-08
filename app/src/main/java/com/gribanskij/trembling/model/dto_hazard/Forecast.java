package com.gribanskij.trembling.model.dto_hazard;

import com.tickaroo.tikxml.annotation.Element;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

@Xml()
public class Forecast {

    @Element
    private Window window;

    @PropertyElement
    private String mag;
    @PropertyElement
    private String rate;
    @PropertyElement
    private String prob;

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public String getMag() {
        return mag;
    }

    public void setMag(String mag) {
        this.mag = mag;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getProb() {
        return prob;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    @Override
    public String toString() {
        return "Magnitude: = " + mag + "Probability: =" + prob + "Window: =" + window.toString();
    }
}
