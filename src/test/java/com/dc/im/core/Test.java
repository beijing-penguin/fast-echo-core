package com.dc.im.core;

import java.util.Map;
import java.util.TreeMap;

public class Test {
    public static void main(String[] args) {
        Map<String, Object> map = new TreeMap<String, Object>();
        map.put("aaa1b2", "2");
        map.put("aaa1b1", "3");
        map.put("aaa1bb", "1");
        map.put("aaa1ba", "1");
        
        System.err.println(map);
    }
}
