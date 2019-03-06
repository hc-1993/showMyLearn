package personal.hanchuang.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import personal.hanchuang.dao.UserDao;
import personal.hanchuang.entity.User;

import javax.annotation.Resource;

@Controller
public class UserController {

    @Resource
    private UserDao userDao;

    @RequestMapping("/userInfo") public @ResponseBody User index222() {

        User user = userDao.selectByPrimaryKey(1);

        System.out.println(user.getAge());
        return user;
    }
}
