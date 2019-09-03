package fun.linyuhong.myCommunity.util;

/**
 * @author linyuhong
 * @date 2019/9/1
 */
public interface JwtTokenDetail {

    //TODO: 这里封装了一层，不直接使用 username 做参数的原因是可以方便未来增加其他要封装到 token 中的信息

    String getUsername();

}
