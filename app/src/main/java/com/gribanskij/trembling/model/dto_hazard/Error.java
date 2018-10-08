package com.gribanskij.trembling.model.dto_hazard;

import com.tickaroo.tikxml.annotation.TextContent;
import com.tickaroo.tikxml.annotation.Xml;

@Xml()
public class Error {
    @TextContent
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ERROR: " + content;
    }
}
