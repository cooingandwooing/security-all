package com.github.qingyejiazhu.securitycore.support;

/**
 * @author gaoxiaofeng
 * @description
 * @date 2019-06-23 21:43
 */
public class SimpleResponse {
    private Object content;

    public SimpleResponse(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}