package fun.linyuhong.myCommunity.controller.portal;

import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author linyuhong
 * @date 2019/9/2
 */
@Controller
public class RegisterController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "/site/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(User user, Model model) {

        Map<String, Object> map = iUserService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(value = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(@PathVariable("userId") Integer userId, @PathVariable("code") String code, Model model) {

        // 用 Integer 类型可以接收空值null，避免使用 int 报错
        if (userId == null || StringUtils.isBlank(code)) {
            model.addAttribute("msg", "参数错误");
            model.addAttribute("target", "/index");
        }
        int result = iUserService.activation(userId, code);
        if (result == Const.isExist.NOEXIST) {
            model.addAttribute("msg", "用户不存在");
            model.addAttribute("target", "/index");
        }else if (result == Const.active.ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == Const.active.ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }
}
