package com.ej.tools.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.stream.Stream;


@Component
public class EjUtils {

    public Object sort(Object obj, String fieldName,boolean reverse) {
        if (obj instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) obj;
            JSONArray newArray = new JSONArray();
            Stream<Object> stream = oldArray.stream();
            if(reverse){
                stream = stream.sorted(Comparator.comparing(o ->
                        ((JSONObject) o).getString(fieldName)
                ).reversed());
            }else{
                stream = stream.sorted(Comparator.comparing(o ->
                        ((JSONObject) o).getString(fieldName)
                ));
            }
            stream.forEach(o -> {
                newArray.add(o);
            });
            return newArray;
        }
        throw new RuntimeException("不支持该数据类型[" + obj.getClass().getName() + "]的排序!");
    }

    public Object equal(Object obj, String fieldName, String value) {
        if (obj instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) obj;
            JSONArray newArray = new JSONArray();
            for (int idx = 0; idx < oldArray.size(); idx++) {
                JSONObject jsonObject = oldArray.getJSONObject(idx);
                if (equal(jsonObject, fieldName, value)) {
                    newArray.add(jsonObject);
                }
            }
            return newArray;
        }
        throw new RuntimeException("不支持该数据类型[" + obj.getClass().getName() + "]的数据筛选!");
    }

    public Object contains(Object obj, String fieldName, String value) {
        if (value == null) {
            throw new RuntimeException("不能通过包含值为null进行筛选，建议使用equal方法！");
        }
        if (obj instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) obj;
            JSONArray newArray = new JSONArray();
            for (int idx = 0; idx < oldArray.size(); idx++) {
                JSONObject jsonObject = oldArray.getJSONObject(idx);
                if (contains(jsonObject, fieldName, value)) {
                    newArray.add(jsonObject);
                }
            }
            return newArray;
        }
        throw new RuntimeException("不支持该数据类型[" + obj.getClass().getName() + "]的数据筛选！");
    }

    public int count(Object obj) {
        if (obj instanceof JSONArray) {
            return ((JSONArray) obj).size();
        }
        throw new RuntimeException("不支持该数据类型[" + obj.getClass().getName() + "]的数据量统计！");
    }

    public BigDecimal sum(Object obj, String... fieldNames) {
        if (obj instanceof JSONObject) {
            return sum((JSONObject) obj, fieldNames);
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            BigDecimal sum = BigDecimal.ZERO;
            for (int idx = 0; idx < array.size(); idx++) {
                sum = sum.add(sum(array.getJSONObject(idx), fieldNames));
            }
            return sum;
        }
        throw new RuntimeException("不支持该数据类型[" + obj.getClass().getName() + "]的求和！");
    }

    private boolean contains(JSONObject obj, String fieldName, String value) {
        String str = obj.getString(fieldName);
        return str != null && str.contains(value);
    }

    private boolean equal(JSONObject obj, String fieldName, String value) {
        return (value == null && obj.getString(fieldName) == null) || (value != null && value.equals(obj.getString(fieldName)));
    }

    private BigDecimal sum(JSONObject obj, String... fieldNames) {
        BigDecimal sum = BigDecimal.ZERO;
        if (fieldNames != null && fieldNames.length > 0) {
            for (String fieldName : fieldNames) {
                BigDecimal value = obj.getBigDecimal(fieldName);
                if (value != null) {
                    sum = sum.add(value);
                }
            }
        }
        return sum;
    }

}
