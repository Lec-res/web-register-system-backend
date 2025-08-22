package com.example.loginsystem.repository;

import com.example.loginsystem.entity.User;
import com.example.loginsystem.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根据角色查找用户列表
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> findByRole(UserRole role);

    /**
     * 根据角色统计用户数量
     * @param role 用户角色
     * @return 用户数量
     */
    long countByRole(UserRole role);

    /**
     * 根据用户名模糊查询
     * @param username 用户名关键词
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);

    /**
     * 查找除指定ID外的用户名是否存在（用于更新时检查）
     * @param username 用户名
     * @param id 排除的用户ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :id")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("id") Long id);

    /**
     * 根据角色查找用户，按创建时间排序
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> findByRoleOrderByCreatedAtDesc(UserRole role);

    /**
     * 查找所有用户，按创建时间降序排列
     * @return 用户列表
     */
    List<User> findAllByOrderByCreatedAtDesc();
}