package jiyue.xsl.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement  //开启事务的支持
public class ReggiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggiApplication.class, args);
//        lombok 打印日志
        log.info("项目启动成功");
    }
}
