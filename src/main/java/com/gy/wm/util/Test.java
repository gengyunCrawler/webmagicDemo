package com.gy.wm.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public class Test {
    public static void main(String[] args) {
        List<String> seeds = new ArrayList<>();
        seeds.add("aaa");
        seeds.add("bbb");
        seeds.add("ccc");
        String urls = "";
        for (String seed : seeds) {
            urls+=seed+",";
        }
    }

}
