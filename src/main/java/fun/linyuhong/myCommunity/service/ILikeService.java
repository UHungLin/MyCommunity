package fun.linyuhong.myCommunity.service;

/**
 * @author linyuhong
 * @date 2019/9/5
 */
public interface ILikeService {

    void like(int userId, int entityType, int entityId, int entityUserId);

    Long findEntityLikeCount(int entityType, int entityId);

    int findEntityLikeStatus(int userId, int entityType, int entityId);

}
