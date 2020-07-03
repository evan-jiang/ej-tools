package com.ej.tools;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroovyThread {

    public static void main(String[] args) throws InterruptedException {

        int thread = 16;
        ExecutorService executorService = Executors.newFixedThreadPool(thread);
        final CountDownLatch countDownLatch = new CountDownLatch(thread);
        for (int i = 0; i < thread; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(incAndGet(10000,10000));
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

    }


    private static final GroovyShell GS = new GroovyShell();

    private static final Map<String, Class<? extends Script>> CACHE = new ConcurrentHashMap<>();

    public static Class<? extends Script> getScriptClass(Long id, String version, String scriptText) {
        String alias = id + "______" + version;
        if (CACHE.containsKey(alias)) {
            return CACHE.get(alias);
        } else {
            Class<? extends Script> clazz = GS.parse(scriptText).getClass();
            CACHE.put(alias, clazz);
            return clazz;
        }
    }

    public static int incAndGet(int source, int times) {
        long id = 1;
        String version = "1.0.0";
        String scriptText = "for(int idx=0;idx<times;idx++){source++;}; return source;";
        Binding groovyBinding = new Binding();
        groovyBinding.setVariable("times", times);
        groovyBinding.setVariable("source", source);
        Class<? extends Script> scriptClass = getScriptClass(id, version, scriptText);
        Script script = InvokerHelper.createScript(scriptClass, groovyBinding);
        Object object = script.run();
        return (int) object;
    }

}
