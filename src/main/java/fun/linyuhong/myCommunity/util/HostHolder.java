package fun.linyuhong.myCommunity.util;

import fun.linyuhong.myCommunity.entity.User;
import fun.linyuhong.myCommunity.vo.UserVo;
import org.springframework.stereotype.Component;

/**
 * @author linyuhong
 * @date 2019/9/1
 */
@Component
public class HostHolder {

    private ThreadLocal<UserVo> users = new ThreadLocal<>();

    public void setUser(UserVo user) {
        users.set(user);
    }

    public UserVo getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
