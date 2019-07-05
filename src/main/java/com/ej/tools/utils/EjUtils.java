package com.ej.tools.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ej.tools.annotation.ToolsMethod;
import com.ej.tools.annotation.ToolsParams;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.stream.Stream;


@Component
public class EjUtils {

    @ToolsMethod("排序")
    public JSONArray sort(@ToolsParams("待排序对象") Object params, @ToolsParams("排序字段") String fieldName, @ToolsParams("是否倒序[true:倒序;false:正序]") boolean reverse) {
        if (params instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) params;
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
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的排序!");
    }
    @ToolsMethod("等值筛选")
    public JSONArray equal(@ToolsParams("待筛选数据") Object params, @ToolsParams("筛选字段") String fieldName, @ToolsParams("筛选所需要等于的值") String value) {
        if (params instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) params;
            JSONArray newArray = new JSONArray();
            for (int idx = 0; idx < oldArray.size(); idx++) {
                JSONObject jsonObject = oldArray.getJSONObject(idx);
                if (equal(jsonObject, fieldName, value)) {
                    newArray.add(jsonObject);
                }
            }
            return newArray;
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的数据筛选!");
    }
    @ToolsMethod("包含值筛选")
    public JSONArray contains(@ToolsParams("待筛选数据") Object params, @ToolsParams("筛选字段") String fieldName, @ToolsParams("筛选所需要包含的值") String value) {
        if (value == null) {
            throw new RuntimeException("不能通过包含值为null进行筛选，建议使用equal方法！");
        }
        if (params instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) params;
            JSONArray newArray = new JSONArray();
            for (int idx = 0; idx < oldArray.size(); idx++) {
                JSONObject jsonObject = oldArray.getJSONObject(idx);
                if (contains(jsonObject, fieldName, value)) {
                    newArray.add(jsonObject);
                }
            }
            return newArray;
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的数据筛选！");
    }
    @ToolsMethod("统计数据量")
    public Integer count(@ToolsParams("待统计数据") Object params) {
        if (params instanceof JSONArray) {
            return ((JSONArray) params).size();
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的数据量统计！");
    }

    @ToolsMethod("求和")
    public BigDecimal sum(@ToolsParams("待求和数据") Object params, @ToolsParams("需要求和的字段名列表") String... fieldNames) {
        if (params instanceof JSONObject) {
            return sum((JSONObject) params, fieldNames);
        } else if (params instanceof JSONArray) {
            JSONArray array = (JSONArray) params;
            BigDecimal sum = BigDecimal.ZERO;
            for (int idx = 0; idx < array.size(); idx++) {
                sum = sum.add(sum(array.getJSONObject(idx), fieldNames));
            }
            return sum;
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的求和！");
    }

    private boolean contains(JSONObject params, String fieldName, String value) {
        String str = params.getString(fieldName);
        return str != null && str.contains(value);
    }

    private boolean equal(JSONObject params, String fieldName, String value) {
        return (value == null && params.getString(fieldName) == null) || (value != null && value.equals(params.getString(fieldName)));
    }

    private BigDecimal sum(JSONObject params, String... fieldNames) {
        BigDecimal sum = BigDecimal.ZERO;
        if (fieldNames != null && fieldNames.length > 0) {
            for (String fieldName : fieldNames) {
                BigDecimal value = params.getBigDecimal(fieldName);
                if (value != null) {
                    sum = sum.add(value);
                }
            }
        }
        return sum;
    }
}
