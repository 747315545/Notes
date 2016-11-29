package com.zui.notes.model;


import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Created by huangfei on 2016/11/24.
 */

public class MyList extends ArrayList<View> {
    private List<String> list = new LinkedList<>();
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        String str;
        list.clear();
        for (int i = 0; i < this.size(); i++) {
            str = this.get(i).toString();
            if(i==0){
                list.add(str);
            }else if(str.charAt(0)=='0'&&str.length()>3){
                list.add(str.substring(3));
            }
            string.append(str + ":");
        }
        if (string.length() > 0)
            string.deleteCharAt(string.length() - 1);
        return string.toString();
    }
    public  List<String> getList() {
        return list;
    }
}
