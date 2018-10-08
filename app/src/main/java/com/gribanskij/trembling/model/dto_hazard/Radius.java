package com.gribanskij.trembling.model.dto_hazard;

import com.tickaroo.tikxml.annotation.Attribute;
import com.tickaroo.tikxml.annotation.TextContent;
import com.tickaroo.tikxml.annotation.Xml;

@Xml(name = "radius")
public class Radius {
    @Attribute
    private String unit;
    @TextContent
    private String description;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Unit: = " + unit + "Radius: = " + description;
    }
}
