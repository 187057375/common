package org.wsh.common.dao.blog;

import org.apache.ibatis.annotations.Param;
import org.wsh.common.model.blog.BlogCounterDO;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
* author: wsh
* JDK-version:  JDK1.8
* comments:  BlogCounter持久层
* since Date： 2017-02-08 16:57:38
*/
@Repository
public interface BlogCounterDao {

	/**
	 * 根据ID查询表数据
	 * @param id Long
	 * @return BlogCounterDO
	 */
	BlogCounterDO selectById(Long id);

	/**
	* 多条件查询表信息
	* @param blogCounterDO BlogCounterDO
	* @param rowBounds RowBounds
	* @return List<BlogCounterDO>
	*/
	List<BlogCounterDO> selectListByParams(BlogCounterDO blogCounterDO, RowBounds rowBounds);
	int selectCountByParams(BlogCounterDO blogCounterDO);
	
	/**
	 * 插入信息
	 * @param blogCounterDO BlogCounterDO
	 * @return int
	 */
	int insert(BlogCounterDO blogCounterDO);

	/**
	* 根据ID更新信息
	* @param blogCounterDO BlogCounterDO
	* @return int
	*/
	int updateById(BlogCounterDO blogCounterDO);

	/**
	* 根据ID更新删除状态
	* @param blogCounterDO BlogCounterDO
	* @return int
	*/
	int updateIsDeleteById(BlogCounterDO blogCounterDO);
	
	/**
	 * 根据ID删除信息
	 * @param id Long
	 * @return int
	 */
	int deleteById(Long id);
	
	/**
	 * 批量添加信息
	 * @param blogCounterDOList List<BlogCounterDO>
	 */
	void insertList(List<BlogCounterDO> blogCounterDOList);

	/**
	 * 根据Ids获取博客计数信息
	 * @param idList List<Long>
	 * @return List<BlogCounterDO>
     */
	List<BlogCounterDO> selectListByIds(@Param("list") List<Long> idList);

	/**
	 * 根据博客ID查询
	 * @param blogId Long
	 * @return BlogCounterDO
     */
	BlogCounterDO selectByBlogId(Long blogId);

	/**
	 * 更新浏览量
	 * @param blogCounterDO BlogCounterDO
	 * @return int
     */
	int updateViewNumById(BlogCounterDO blogCounterDO);

	/**
	 * 更新评论数量
	 * @param blogCounterDO BlogCounterDO
	 * @return int
     */
	int updateCommentNumById(BlogCounterDO blogCounterDO);

	/**
	 * 更新赞数量
	 * @param blogCounterDO BlogCounterDO
	 * @return int
	 */
	int updatepraiseNumById(BlogCounterDO blogCounterDO);
}
