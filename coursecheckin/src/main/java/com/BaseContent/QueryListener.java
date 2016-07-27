package com.BaseContent;

import java.io.Serializable;
import java.util.List;

public interface QueryListener {
    public void data(List<Serializable> data);

    public void error(String msg);
}