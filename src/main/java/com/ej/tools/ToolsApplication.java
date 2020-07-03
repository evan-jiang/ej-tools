package com.ej.tools;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class, BatchAutoConfiguration.class })
@ComponentScan(value = {"com.ej.tools"})
public class ToolsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToolsApplication.class, args);
    }
}
