package fun.linyuhong.myCommunity.service.Impl;

import fun.linyuhong.myCommunity.async.EventModel;
import fun.linyuhong.myCommunity.async.EventProducer;
import fun.linyuhong.myCommunity.async.EventType;
import fun.linyuhong.myCommunity.common.Const;
import fun.linyuhong.myCommunity.dao.UserMapper;
import fun.linyuhong.myCommunity.entity.LoginTicket;
import fun.linyuhong.myCommunity.entity.Message;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.service.IUserService;
import fun.linyuhong.myCommunity.util.*;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author linyuhong
 * @date 2019/9/1
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private EventProducer eventProducer;

    // 域名
    @Value("${domain}")
    private String domain;

    // 项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public Map<String, Object> login(String username, String password) {

        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }

        // 验证密码
        password = MD5Encoder.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys));
        loginTicket.setTicket(GetGenerateUUID.generateUUID());
        loginTicket.setStatus(Const.loginStatus.VALID); // ticket 是否有效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + Const.loginStatus.DEFAULT_EXPIRED_SECONDS * 1000));

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put(Const.ticket.TICKET, loginTicket.getTicket());
        return map;

    }

    @Override
    public LoginTicket findLoginTicket(String loginTicket) {
        // ticket = ticket:UUID
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket);
        LoginTicket loginTicket1 = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);

        return loginTicket1;
    }

    @Override
    public UserVo findUserById(int userId) {
//        User user = userMapper.selectByPrimaryKey(userId);
//        if (user != null) {
//            return assembleUser(user);
//        }
//        return null;
        UserVo userVo = getCache(userId);
        if (userVo == null) {
            userVo = initCache(userId);
        }
        return userVo;
    }

    private UserVo assembleUser(User user) {
        UserVo userVo = new UserVo();
        // 加密
        userVo.setId(XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys));
        userVo.setUsername(user.getUsername());
        userVo.setHeaderUrl(user.getHeaderUrl());
        userVo.setType(user.getType());
        userVo.setEmail(user.getEmail());
        userVo.setCreateTime(user.getCreateTime());
        return userVo;
    }

    @Override
    public void logout(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(Const.loginStatus.INVALID);  // 不删除，保存用户的登录记录
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    @Override
    public Map<String, Object> register(User user) {

        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证邮箱格式
        if (!EmailUtil.isEmail(user.getEmail())){
            map.put("emailMsg", "邮箱格式不正确");
            return map;
        }

        User u = userMapper.selectByUsername(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该用户名已存在");
            return map;
        }


        // 验证邮箱
        u = userMapper.selectByUserEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 参数验证通过，插入数据库
        user.setSalt(GetGenerateUUID.generateUUID().substring(0, 5));
        user.setPassword(MD5Encoder.md5(user.getPassword() + user.getSalt()));
        user.setStatus(Const.active.INACTIVE);
        user.setType(Const.Role.ROLE_USER.getType());
        user.setActivationCode(GetGenerateUUID.generateUUID());
        user.setHeaderUrl(HttpUtil.sendGet(Const.avatarUrl.AVATARURL));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

//        // 发送激活邮件
//        Context context = new Context();
//        context.setVariable("email", user.getEmail());
//        // http://localhost:8080/activation/10110110/code
//        String url = domain + contextPath + "/activation/" + XORUtil.encryptId(user.getId(), Const.getIdEncodeKeys.userIdKeys)
//                + "/" + user.getActivationCode();
//        context.setVariable("url", url);
//        String content = templateEngine.process("/mail/activation", context);
//        mailClient.sendMail(user.getEmail(), "激活账号", content);

        /**
         * 触发注册事件
         */
        EventModel eventModel = new EventModel(EventType.REGISTER)
                .setActorId(Const.systemuser.SYSTEM_USER_ID)
                .setEntityUserId(user.getId())
                .setEntityType(Const.entityType.ENTITY_TYPE_USER)
                .setEntityUserId(user.getId())
                .setData("msg", "欢迎您注册溢出网");
        eventProducer.fireEvent(eventModel);

        return map;
    }

    @Override
    public int activation(int userId, String code) {
        userId = XORUtil.encryptId(userId, Const.getIdEncodeKeys.userIdKeys);
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return Const.isExist.NOEXIST;
        }
        if (user.getStatus() == 1){  // 避免重复激活
            return Const.active.ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, Const.active.ACTIVE);
            clearCache(userId);

            return Const.active.ACTIVATION_SUCCESS;
        }else {
            return Const.active.ACTIVATION_FAILURE;
        }
    }

    @Override
    public UserVo findUserByName(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }
       return assembleUser(user);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }


    // 1.优先从缓存中取值
    private UserVo getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (UserVo) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private UserVo initCache(int userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        UserVo userVo = assembleUser(user);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, userVo, 3600, TimeUnit.SECONDS);
        return userVo;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }


    // 获取用户权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        UserVo user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return Const.Role.ROLE_ADMIN.getRole();
                    default:
                        return Const.Role.ROLE_USER.getRole();
                }
            }
        });
        return list;
    }

}
