//package fun.linyuhong.myCommunity.service.Impl;
//
//import fun.linyuhong.myCommunity.dao.UserMapper;
//import fun.linyuhong.myCommunity.entity.JwtUser;
//import fun.linyuhong.myCommunity.entity.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
///**
// * @author linyuhong
// * @date 2019/9/1
// */
//
//@Service
//public class JwtUserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userMapper.selectByUsername(username);
//        if (user == null){
//            throw new UsernameNotFoundException("用户不存在！");
//        }
//
//        return new JwtUser(user);
//    }
//}
