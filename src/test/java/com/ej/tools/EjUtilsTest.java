package com.ej.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ej.tools.utils.EjUtils;
import org.junit.Test;

public class EjUtilsTest {

    @Test
    public void sort(){
        EjUtils ejUtils = new EjUtils();
        JSONArray array = JSON.parseArray("[\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2418.57\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"180.00\",\n" +
                "\t\t\"agreeRepayDate\":\"20191005\",\n" +
                "\t\t\"installmentNo\":1\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2433.08\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"165.49\",\n" +
                "\t\t\"agreeRepayDate\":\"20191105\",\n" +
                "\t\t\"installmentNo\":2\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2447.68\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"150.89\",\n" +
                "\t\t\"agreeRepayDate\":\"20191205\",\n" +
                "\t\t\"installmentNo\":3\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2462.37\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"136.20\",\n" +
                "\t\t\"agreeRepayDate\":\"20200105\",\n" +
                "\t\t\"installmentNo\":4\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2477.14\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"121.43\",\n" +
                "\t\t\"agreeRepayDate\":\"20200205\",\n" +
                "\t\t\"installmentNo\":5\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2492.00\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"106.57\",\n" +
                "\t\t\"agreeRepayDate\":\"20200305\",\n" +
                "\t\t\"installmentNo\":6\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2506.96\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"91.61\",\n" +
                "\t\t\"agreeRepayDate\":\"20200405\",\n" +
                "\t\t\"installmentNo\":7\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2522.00\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"76.57\",\n" +
                "\t\t\"agreeRepayDate\":\"20200505\",\n" +
                "\t\t\"installmentNo\":8\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2537.13\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"61.44\",\n" +
                "\t\t\"agreeRepayDate\":\"20200605\",\n" +
                "\t\t\"installmentNo\":9\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2552.35\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"46.22\",\n" +
                "\t\t\"agreeRepayDate\":\"20200705\",\n" +
                "\t\t\"installmentNo\":10\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2567.67\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"30.90\",\n" +
                "\t\t\"agreeRepayDate\":\"20200805\",\n" +
                "\t\t\"installmentNo\":11\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"principal\":\"2583.05\",\n" +
                "\t\t\"otherAmount\":\"0.00\",\n" +
                "\t\t\"interest\":\"15.50\",\n" +
                "\t\t\"agreeRepayDate\":\"20200905\",\n" +
                "\t\t\"installmentNo\":12\n" +
                "\t}\n" +
                "]");
        JSONArray sort = ejUtils.sort(array, "installmentNo", true);
        System.out.println(JSON.toJSONString(sort,true));
    }
}
