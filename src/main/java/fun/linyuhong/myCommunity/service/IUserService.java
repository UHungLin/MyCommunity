package fun.linyuhong.myCommunity.service;

import fun.linyuhong.myCommunity.entity.LoginTicket;
import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.vo.UserVo;

import java.util.Map;

/**
 * @author linyuhong
 * @date 2019/9/1
 */
public interface IUserService {

    Map<String, Object> login(String username, String password);

    LoginTicket findLoginTicket(String key);

    UserVo findUserById(int id);

    void logout(String ticket);

    Map<String, Object> register(User user);

    int activation(int userId, String code);

}
