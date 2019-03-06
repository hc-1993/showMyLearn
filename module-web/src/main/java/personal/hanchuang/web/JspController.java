package personal.hanchuang.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JspController {

    @GetMapping("/jsp/index")
    public String nmae(Model model) {

        model.addAttribute("info","我的第一个spring boot jsp 页面");
        return "index";
    }
}
