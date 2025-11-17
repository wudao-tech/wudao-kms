package com.wudao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(basePackages = {
        "com.wudao.**.mapper"
})
@SpringBootApplication(scanBasePackages = "com.wudao")
@EnableScheduling
public class KmsApplication{
    public static void main(String[] args) {
        SpringApplication.run(KmsApplication.class, args);
        System.out.println("服务启动成功!!!");
    }
}
