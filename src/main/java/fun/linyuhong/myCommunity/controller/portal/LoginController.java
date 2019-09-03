package fun.linyuhong.myCommunity.controller.portal;

import com.google.code.kaptcha.Producer;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.GetGenerateUUID;
import fun.linyuhong.myCommunity.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Producer kaptchaProduct;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse servletResponse /*, HttpSession session*/){
        // 生成验证码
        String text = kaptchaProduct.createText();
        BufferedImage image = kaptchaProduct.createImage(text);


        // 验证码归属
        String kaptchaOwner = GetGenerateUUID.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        servletResponse.addCookie(cookie);
        // 将验证码存入redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        servletResponse.setContentType("image/png");
        // os 流的关闭由Spring自动管理
        try( OutputStream os = servletResponse.getOutputStream()) {
            ImageIO.write(image, "png", os);
        }catch (IOException e){
            logger.error("响应验证码失败:" + e.getMessage());
        }
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "/site/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam("username") String username, @RequestParam("password") String password,
                        @RequestParam("code") String code, @CookieValue("kaptchaOwner") String kaptchaOwner, Model model, HttpServletResponse response) {

        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码不正确~");
            return "/site/login";
        }

        Map<String, Object> map = iUserService.login(username, password);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get(Const.ticket.TICKET).toString());
            cookie.setPath(contextPath);  // cookie 的生效范围
            cookie.setMaxAge(Const.loginStatus.DEFAULT_EXPIRED_SECONDS);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue(Const.ticket.TICKET) String ticket) {

        if (StringUtils.isNotBlank(ticket)){
            iUserService.logout(ticket);
        }
        return "redirect:/login";
    }

}
