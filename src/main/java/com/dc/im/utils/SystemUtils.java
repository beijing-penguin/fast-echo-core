package com.dc.im.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 
 * @Description: 
 * @author duanchao
 * @date 2019年9月27日
 */
public class SystemUtils {
    /**
     * 将字符串转化为java bean驼峰命名规范
     * @param str
     * @return
     */
    public static String separatorToJavaBean(String str) {
        String[] fields = str.toLowerCase().split("_");
        StringBuilder sbuilder = new StringBuilder(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            char[] cs = fields[i].toCharArray();
            cs[0] -= 32;
            sbuilder.append(String.valueOf(cs));
        }
        return sbuilder.toString();
    }
    
    
    
    public static Object getValueByFieldType(Object value, Class<?> fieldType) throws Exception {
        if (value == null) {
            return null;
        }
        String v = String.valueOf(value);
        String type = fieldType.getSimpleName();
        if (type.equals("String")) {
            return v;
        }else if(v.trim().length()==0) {
            return null;
        } else if (type.equals("Integer") || type.equals("int")) {
            return Integer.parseInt(v);
        } else if (type.equals("Long") || type.equals("long")) {
            return Long.parseLong(v);
        } else if (type.equals("Double") || type.equals("double")) {
            return Double.parseDouble(v);
        } else if (type.equals("Short") || type.equals("short")) {
            return Short.parseShort(v);
        } else if (type.equals("Float") || type.equals("float")) {
            return Float.parseFloat(v);
        } else if (type.equals("Byte") || type.equals("byte")) {
            return Byte.parseByte(v);
        } else if (type.equals("Boolean") || type.equals("boolean")) {
            return Boolean.parseBoolean(v);
        } else if (type.equals("BigDecimal")) {
            return new BigDecimal(v);
        } else if (type.equals("BigInteger")) {
            return new BigInteger(v);
        } else if (type.equals("Date")) {
            SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat(v));
            //不允许底层java自动日期进行计算，直接抛出异常
            sdf.setLenient(false);
            Date date = sdf.parse(v);
            return date;
        }
        throw new Exception(type + " is unsupported");
    }


    /**
     * 常规自动日期格式识别
     * 
     * @param str 时间字符串
     * @return Date
     * @author dc
     */
    public static String getDateFormat(String str) {
        boolean year = false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        if (pattern.matcher(str.substring(0, 4)).matches()) {
            year = true;
        }
        StringBuilder sb = new StringBuilder();
        int index = 0;
        if (!year) {
            if (str.contains("月") || str.contains("-") || str.contains("/")) {
                if (Character.isDigit(str.charAt(0))) {
                    index = 1;
                }
            } else {
                index = 3;
            }
        }
        for (int i = 0; i < str.length(); i++) {
            char chr = str.charAt(i);
            if (Character.isDigit(chr)) {
                if (index == 0) {
                    sb.append("y");
                } else if (index == 1) {
                    sb.append("M");
                } else if (index == 2) {
                    sb.append("d");
                } else if (index == 3) {
                    sb.append("H");
                } else if (index == 4) {
                    sb.append("m");
                } else if (index == 5) {
                    sb.append("s");
                } else if (index == 6) {
                    sb.append("S");
                }
            } else {
                if (i > 0) {
                    char lastChar = str.charAt(i - 1);
                    if (Character.isDigit(lastChar)) {
                        index++;
                    }
                }
                sb.append(chr);
            }
        }
        return sb.toString();
    }
}
