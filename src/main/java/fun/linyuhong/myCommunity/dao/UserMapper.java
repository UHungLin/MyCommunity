package fun.linyuhong.myCommunity.dao;

import fun.linyuhong.myCommunity.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
//    int deleteByPrimaryKey(Integer id);
//
//    int insert(User record);
//
//    int insertSelective(User record);
//
//
//    int updateByPrimaryKeySelective(User record);
//
//    int updateByPrimaryKey(User record);

    User selectByPrimaryKey(Integer id);

    User selectByUsername(String username);

    User selectByUserEmail(String email);

    int updateStatus(int userId, int status); // 更新用户激活状态

    int insertUser(User user);

    int updateHeader(int userId, String headerUrl);

}