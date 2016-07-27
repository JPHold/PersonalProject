package com.hjp.vo;

import java.util.Collection;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by HJP on 2015/12/11 0011.
 */
public class ComputerCourse extends BmobObject  {
    private List<String> courseNames;

    public ComputerCourse() {
        super();
    }

    public ComputerCourse(String tableName) {
        super(tableName);
    }

    @Override
    public void add(String key, Object value) {
        super.add(key, value);
    }

    @Override
    public void addAll(String key, Collection<?> values) {
        super.addAll(key, values);
    }

    @Override
    public void addAllUnique(String key, Collection<?> values) {
        super.addAllUnique(key, values);
    }

    @Override
    public void addUnique(String key, Object value) {
        super.addUnique(key, value);
    }


    public List<String> getcourseNames() {
        return courseNames;
    }

}
