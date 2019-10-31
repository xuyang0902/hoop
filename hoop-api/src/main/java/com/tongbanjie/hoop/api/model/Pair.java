package com.tongbanjie.hoop.api.model;

/**
 * @author xu.qiang
 * @date 18/8/22
 */
public class Pair<KEY,VALUE> {

    private KEY key;

    private VALUE value;

    public Pair(KEY key, VALUE value) {
        this.key = key;
        this.value = value;
    }

    public KEY getKey() {
        return key;
    }

    public void setKey(KEY key) {
        this.key = key;
    }

    public VALUE getValue() {
        return value;
    }

    public void setValue(VALUE value) {
        this.value = value;
    }
}
