package personal.hanchuang.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @Value("hc")//将值赋值给变量
    private String hc;

    @Value("${hc}")//将配置文件的值赋值给变量
    //如果配置文件中没有这个配置,则启动异常
    private String hc_name;


    @RequestMapping("/hello")
    public @ResponseBody
    String index222() {
        return "Hello spring";
    }


    @RequestMapping("/name")
    public @ResponseBody
    String nmae() {
        return "Hello "+ hc_name ;
    }

}
