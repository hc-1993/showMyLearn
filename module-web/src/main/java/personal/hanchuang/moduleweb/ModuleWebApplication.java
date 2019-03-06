package personal.hanchuang.moduleweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"personal.hanchuang.web"})
@MapperScan("personal.hanchuang.dao")
public class ModuleWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleWebApplication.class, args);
	}

}
