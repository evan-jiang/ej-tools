package com.ej.tools.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ej.tools.annotation.ToolsMethod;
import com.ej.tools.annotation.ToolsParams;
import com.ej.tools.constants.EJConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class EjUtils {
    public static final List<Map<String, Object>> HELP_CACHE;
    private static final String SPLIT_STR = "######";

    static {
        List<Method> methods = Arrays.asList(EjUtils.class.getDeclaredMethods());
        HELP_CACHE = methods.stream()
                .filter(method -> method.getAnnotation(ToolsMethod.class) != null)
                .map(method -> {
                    ToolsMethod toolsMethod = method.getAnnotation(ToolsMethod.class);
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("name", toolsMethod.value());
                    String temp = "%s %s.%s(%s)";
                    String returnType = method.getReturnType().getName();
                    String methodName = method.getName();
                    List<String> show = Arrays.asList(method.getParameters()).stream()
                            .map(parameter -> parameter.getType().getTypeName() + " " + parameter.getName())
                            .collect(Collectors.toList());
                    map.put("method", String.format(temp, returnType, EJConstants.EJUTILS, methodName, String.join(","
                            , show)));
                    final Map<String, Object> info = new LinkedHashMap();
                    Arrays.asList(method.getParameters()).stream().forEach(parameter -> {
                        ToolsParams toolsParams = parameter.getAnnotation(ToolsParams.class);
                        info.put(parameter.getName(), toolsParams.value());
                    });
                    map.put("paramsInfo", info);
                    return map;
                }).collect(Collectors.toList());
    }

    @ToolsMethod("去除重复")
    public JSONArray distinct(@ToolsParams("待去重数据") Object params, @ToolsParams("需要去重的字段名列表") String... fieldNames) {
//        if (fieldNames == null || fieldNames.length == 0) {
//            throw new RuntimeException("需要去重的字段名列表不能为空!");
//        }
        if (params instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) params;
            JSONArray newArray = new JSONArray();
            if (fieldNames == null || fieldNames.length == 0) {
                newArray.addAll(new HashSet<>(oldArray));
                return newArray;
            }
            Map<String, Object> distinct = new LinkedHashMap<>();
            for (int idx = 0; idx < oldArray.size(); idx++) {
                JSONObject oldJobj = oldArray.getJSONObject(idx);
                JSONObject newJobj = new JSONObject();
                Object newValue = null;
                List<String> vs = new ArrayList<>();
                for (String field : fieldNames) {
                    Object fieldValue = oldJobj.get(field);
                    if (fieldValue == null || fieldValue instanceof String || fieldValue instanceof Number) {
                        vs.add(fieldValue == null ? null : fieldValue.toString());
                        if (fieldNames.length == 1) {
                            newValue = fieldValue;
                        } else {
                            newJobj.put(field, fieldValue);
                        }
                    } else {
                        throw new RuntimeException("不支持对字符串或数字以外的类型去重!");
                    }
                }
                String key = String.join(SPLIT_STR, vs);
                if (!distinct.containsKey(key)) {
                    if (fieldNames.length == 1) {
                        distinct.put(key, newValue);
                    } else {
                        distinct.put(key, newJobj);
                    }
                }
            }
            for (Map.Entry<String, Object> e : distinct.entrySet()) {
                newArray.add(e.getValue());
            }
            return newArray;
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的去重!");
    }

    @ToolsMethod("分组")
    public JSONObject group(@ToolsParams("待分组对象") Object params, @ToolsParams("分组字段") String fieldName, @ToolsParams(
            "分组后需要显示字段名列表") String... showFieldNames) {
        if (params instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) params;
            JSONObject jobj = new JSONObject();
            for (int idx = 0; idx < oldArray.size(); idx++) {
                Object newValue = null;
                JSONObject oldJobj = oldArray.getJSONObject(idx);
                if (showFieldNames != null && showFieldNames.length == 1) {
                    newValue = oldJobj.getObject(showFieldNames[0], Object.class);

                } else {
                    JSONObject newJobj = new JSONObject();
                    if (showFieldNames == null || showFieldNames.length == 0) {
                        newJobj.putAll(oldJobj);
                        newJobj.remove(fieldName);
                    } else if (showFieldNames.length > 1) {
                        for (String fn : showFieldNames) {
                            newJobj.put(fn, oldJobj.getObject(fn, Object.class));
                        }
                    }
                    newValue = newJobj;
                }
                String value = oldJobj.getString(fieldName);
                if (jobj.containsKey(value)) {
                    jobj.getJSONArray(value).add(newValue);
                } else {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.add(newValue);
                    jobj.put(value, jsonArray);
                }

            }
            return jobj;
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的分组!");
    }

    @ToolsMethod("排序")
    public JSONArray sort(@ToolsParams("待排序对象") Object params, @ToolsParams("排序字段") String fieldName, @ToolsParams(
            "是否倒序[true:倒序;false:正序]") boolean reverse) {
        if (params instanceof JSONArray) {
            JSONArray oldArray = (JSONArray) params;
            JSONArray newArray = new JSONArray();
            Stream<Object> stream = oldArray.stream();
            if (reverse) {
                stream = stream.sorted(Comparator.comparing(o ->
                        (Comparable) ((JSONObject) o).get(fieldName)
                ).reversed());
            } else {
                stream = stream.sorted(Comparator.comparing(o ->
                        (Comparable) ((JSONObject) o).get(fieldName)
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
    public JSONArray equal(@ToolsParams("待筛选数据") Object params, @ToolsParams("筛选字段") String fieldName, @ToolsParams(
            "筛选所需要等于的值") String value) {
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
    public JSONArray contains(@ToolsParams("待筛选数据") Object params, @ToolsParams("筛选字段") String fieldName,
                              @ToolsParams("筛选所需要包含的值") String value) {
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

    @ToolsMethod("提炼")
    public JSONArray refine(@ToolsParams("待提炼数据") Object params, @ToolsParams("需要提炼的字段名列表") String... fieldNames) {
        JSONArray newArray = new JSONArray();
        if (fieldNames == null || fieldNames.length == 0) {
            return newArray;
        }
        if (params instanceof JSONArray) {
            JSONArray array = (JSONArray) params;
            for (int idx = 0; idx < array.size(); idx++) {
                if (fieldNames.length == 1) {
                    String fieldName = fieldNames[0];
                    newArray.add(array.getJSONObject(idx).get(fieldName));
                } else {
                    JSONObject jobj = new JSONObject();
                    for (String fieldName : fieldNames) {
                        jobj.put(fieldName, array.getJSONObject(idx).get(fieldName));
                    }
                    newArray.add(jobj);
                }
            }
            return newArray;
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的提炼！");
    }

    @ToolsMethod("转化成MarkDown")
    public String toMD(@ToolsParams("待转数据") Object params) {
        if (params instanceof JSONArray) {
            StringBuilder sb = new StringBuilder();
            JSONArray array = (JSONArray) params;
            Map<String, Boolean> head = new TreeMap<>();
            int size = array.size();
            for (int idx = 0; idx < size; idx++) {
                JSONObject jsonObject = array.getJSONObject(idx);
                for (Map.Entry<String, Object> e : jsonObject.entrySet()) {
                    head.put(e.getKey(), Boolean.TRUE);
                }
            }
            List<String> headList = new ArrayList<>();
            List<String> headSplit = new ArrayList<>();
            for (Map.Entry<String, Boolean> e : head.entrySet()) {
                headList.add(e.getKey());
                headSplit.add("---");
            }
            sb.append("| ").append(String.join(" | ", headList)).append(" |\n");
            sb.append("| ").append(String.join(" | ", headSplit)).append(" |\n");
            for (int idx = 0; idx < size; idx++) {
                List<String> row = new ArrayList<>();
                JSONObject jsonObject = array.getJSONObject(idx);
                for (Map.Entry<String, Boolean> e : head.entrySet()) {
                    String data = jsonObject.getString(e.getKey());
                    row.add(StringUtils.isEmpty(data) ? "" : data);
                }
                sb.append("| ").append(String.join(" | ", row)).append(" |\n");
            }
            return sb.toString();
        }
        throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的转化！");
    }

    private static final String SQL_TEMP = "insert into %s \n(`%s`)\nvalues \n%s;";
    private static final String SQL_KEY_SPLIT_TEMP = "`,`";
    private static final String SQL_VALUE_TEMP = "'%s'";
    private static final String SQL_VALUES_TEMP = "(%s)";
    private static final String SQL_SPLIT = ",";
    private static final String SQL_SPLIT_N = ",\n";

    @ToolsMethod("转化成SQL")
    public String toSQL(@ToolsParams("待转数据") Object params,
                        @ToolsParams("数据表名") String tableName,
                        @ToolsParams("自增字段名") String autoIncrementKey,
                        @ToolsParams("自增字段起始值") Long autoIncrementStartValue) {
        Set<String> keys = keys(params);
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        if (autoIncrementKey != null && (autoIncrementKey = autoIncrementKey.trim()).length() > 0 && !keys.contains(autoIncrementKey)) {
            keys.add(autoIncrementKey);
            if (autoIncrementStartValue == null) {
                autoIncrementStartValue = 0L;
            }
        }
        JSONArray jsonArray = null;
        if (params instanceof JSONObject) {
            jsonArray = new JSONArray();
            jsonArray.add(params);
        } else if (params instanceof JSONArray) {
            jsonArray = (JSONArray) params;
        } else {
            throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的转化！");
        }
        List<String> vs = new ArrayList<>(jsonArray.size());
        for (int idx = 0; idx < jsonArray.size(); idx++) {
            JSONObject jsonObject = jsonArray.getJSONObject(idx);
            List<String> singleValues = new ArrayList<>(keys.size());
            for (String key : keys) {
                if (key.equals(autoIncrementKey)) {
                    singleValues.add(String.valueOf(autoIncrementStartValue++));
                } else {
                    singleValues.add(jsonObject.get(key) == null ? null : String.format(SQL_VALUE_TEMP,
                            jsonObject.get(key).toString()));
                }
            }
            String value = String.join(SQL_SPLIT, singleValues);
            vs.add(String.format(SQL_VALUES_TEMP, value));
        }
        return String.format(SQL_TEMP, tableName, String.join(SQL_KEY_SPLIT_TEMP, keys), String.join(SQL_SPLIT_N, vs));
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

    private Set<String> keys(Object params) {
        Set<String> keys = null;
        if (params instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) params;
            if (keys == null) {
                keys = new TreeSet<>();
            }
            keys.addAll(jsonObject.keySet());
        } else if (params instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) params;
            for (int idx = 0; idx < jsonArray.size(); idx++) {
                Set<String> keySet = jsonArray.getJSONObject(idx).keySet();
                if (keys == null) {
                    keys = new TreeSet<>();
                }
                keys.addAll(keySet);
            }
        } else {
            throw new RuntimeException("不支持该数据类型[" + params.getClass().getName() + "]的转化！");
        }
        return keys;
    }


    @ToolsMethod("字符串提取")
    public JSONArray substring(@ToolsParams("待提取数据") Object params,
                               @ToolsParams("目标数据前缀(不含)") String beginStr,
                               @ToolsParams("目标数据后缀(不含)") String endStr) {
        if (params == null) {
            return null;
        }
        String value = params.toString();
        JSONArray jsonArray = new JSONArray();
        int blen = beginStr == null ? 0 : beginStr.length();
        int elen = endStr == null ? 0 : endStr.length();
        int bidx = -1;
        int eidx = -1;
        while (eidx < value.length() && (bidx = beginStr == null ? 0 : value.indexOf(beginStr, eidx + elen)) >= 0) {
            eidx = endStr == null ? value.length() - 1 : value.indexOf(endStr, bidx + blen);
            if (eidx < 0) {
                break;
            }
            jsonArray.add(value.substring(bidx + blen, eidx));
        }
        return jsonArray;
    }
}
