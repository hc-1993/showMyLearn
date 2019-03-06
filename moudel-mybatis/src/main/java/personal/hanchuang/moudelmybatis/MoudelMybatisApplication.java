package personal.hanchuang.moudelmybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("personal.hanchuang.dao")
@ComponentScan(basePackages = {"personal.hanchuang.web"})
public class MoudelMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoudelMybatisApplication.class, args);
    }

}
