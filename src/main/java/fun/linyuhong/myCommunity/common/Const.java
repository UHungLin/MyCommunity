package fun.linyuhong.myCommunity.common;

public class Const {

    /**
     * 系统用户
     */
    public interface systemuser {
        int SYSTEM_USER_ID = 1;
    }

    /**
     * 关注实体类型
     */
    public interface follow {
        /**
         * 实体类型: 用户
         */
        int ENTITY_TYPE_USER = 3;
    }

    /**
     * 点赞实体类型
     */
    public interface  like {
        /**
         * 帖子
         */
        int ENTITY_TYPE_POST = 1;
        /**
         * 评论
         */
        int ENTITY_TYPE_COMMENT = 2;
        /**
         * 实体类型: 用户
         */
        int ENTITY_TYPE_USER = 3;

    }

    /**
     * Message 类型
     */
    public interface status {
        /**
         * 信息未读
         */
        int UNREAD = 0;

        /**
         * 信息已读
         */
        int READ = 1;

        /**
         * 信息删除
         */
        int DELETE = 2;
    }

    /**
     * Comment 类型
     */
    public interface entityType {
        /**
         * 实体类型: 帖子
         */
        int ENTITY_TYPE_POST = 1;

        /**
         * 实体类型: 评论
         */
        int ENTITY_TYPE_COMMENT = 2;

        /**
         * 实体类型: 用户
         */
        int ENTITY_TYPE_USER = 3;


    }

    /**
     * 用户是否存在
     */
    public interface isExist {
        int NOEXIST = 0;
        int EXIST = 1;
    }


    /**
     * id 加密
     */
    public interface getIdEncodeKeys {
        byte[] userIdKeys = new byte[]{1, 2};
        byte[] postIdKeys = new byte[]{2, 1};
    }


    /**
     * 用户权限
     */
    public enum Role{

        ROLE_USER(0, "ROLE_USER"),
        ROLE_ADMIN(1,"ROLE_ADMIN");

        private Integer type;
        private String role;

        Role(Integer type, String role) {
            this.type = type;
            this.role = role;
        }

        public Integer getType() {
            return type;
        }

        public String getRole() {
            return role;
        }

        public static String getRole(int type) {
            for (Role s : Role.values()){
                if (s.type == type){
                    return s.role;
                }
            }
           return ROLE_USER.role;
        }
    }

    /**
     * 登录凭证 loginTicket
     * VALID  有效
     * INVALID  无效
     * DEFAULT_EXPIRED_SECONDS * 1000 = 12小时
     */
    public interface loginStatus{
        int VALID = 0;
        int INVALID = 1;
        int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    }

    /**
     * 用户登录凭证 ticket
     */
    public interface ticket {
        String TICKET = "ticket";
    }

    /**
     * 用户账号是否激活
     */
    public interface active {

        /**
         * 账号未激活
         */
        int INACTIVE = 0;

        /**
         * 账号已激活
         */
        int ACTIVE = 1;

        /**
         * 激活成功  返回给controller判断用的
         */
        int ACTIVATION_SUCCESS = 3;

        /**
         * 重复激活
         */
        int ACTIVATION_REPEAT = 4;

        /**
         * 激活失败
         */
        int ACTIVATION_FAILURE = 5;
    }

    public interface avatarUrl {
        String AVATARURL = "https://api.uomg.com/api/rand.avatar";
    }

}
