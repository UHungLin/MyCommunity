//package fun.linyuhong.myCommunity.entity;
//
//import fun.linyuhong.myCommunity.common.Const;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Arrays;
//import java.util.Collection;
//
///**
// * @author linyuhong
// * @date 2019/9/1
// */
//public class JwtUser implements UserDetails {
//
//    private User user;
//
//    public JwtUser(User user) {
//        this.user = user;
//    }
//
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        //返回当前用户的权限
//        return Arrays.asList(new SimpleGrantedAuthority(Const.Role.getRole(user.getType())));
//    }
//
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return user.getUsername();
//    }
//
//    //账户是否未过期
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    //账户是否未被锁
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    //是否启用
//    @Override
//    public boolean isEnabled() {
//        return user.getStatus() == 1;
//    }
//}
