package personal.hanchuang.moudelmybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("personal.hanchuang.dao")
@ComponentScan(basePackages = {"personal.hanchuang.web"})
@EnableTransactionManagement//开启事务支持
public class MoudelMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoudelMybatisApplication.class, args);
    }

}
