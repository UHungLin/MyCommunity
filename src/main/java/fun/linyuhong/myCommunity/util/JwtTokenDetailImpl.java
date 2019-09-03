package fun.linyuhong.myCommunity.util;

/**
 * @author linyuhong
 * @date 2019/9/1
 */
public class JwtTokenDetailImpl implements JwtTokenDetail {

    private final String username;

    public JwtTokenDetailImpl(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
