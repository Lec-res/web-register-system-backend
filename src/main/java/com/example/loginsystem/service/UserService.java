package com.example.loginsystem.service;

import com.example.loginsystem.dto.RegisterRequest;
import com.example.loginsystem.entity.User;
import com.example.loginsystem.entity.UserRole;
import com.example.loginsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 用户信息，验证失败返回null
     */
    public User login(String username, String password) {
        logger.info("尝试登录用户: {}", username);

        try {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (passwordEncoder.matches(password, user.getPassword())) {
                    logger.info("用户 {} 登录成功", username);
                    return user;
                } else {
                    logger.warn("用户 {} 密码错误", username);
                }
            } else {
                logger.warn("用户 {} 不存在", username);
            }
        } catch (Exception e) {
            logger.error("登录过程发生异常: ", e);
        }

        return null;
    }

    /**
     * 用户注册
     * @param request 注册请求信息
     * @return 创建的用户信息
     * @throws RuntimeException 用户名已存在时抛出异常
     */
    public User register(RegisterRequest request) {
        logger.info("尝试注册新用户: {}", request.getUsername());

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("用户名 {} 已存在", request.getUsername());
            throw new RuntimeException("用户名已存在");
        }

        try {
            // 创建新用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);

            User savedUser = userRepository.save(user);
            logger.info("新用户 {} 注册成功，ID: {}", savedUser.getUsername(), savedUser.getId());

            return savedUser;
        } catch (Exception e) {
            logger.error("注册用户过程发生异常: ", e);
            throw new RuntimeException("注册失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        logger.info("获取所有用户列表");
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        logger.info("获取用户信息，ID: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户信息
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        logger.info("根据用户名获取用户信息: {}", username);
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param updatedUser 更新的用户信息
     * @return 更新后的用户信息
     * @throws RuntimeException 用户不存在或用户名冲突时抛出异常
     */
    public User updateUser(Long id, User updatedUser) {
        logger.info("尝试更新用户信息，ID: {}", id);

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            logger.warn("要更新的用户不存在，ID: {}", id);
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();

        try {
            // 检查用户名是否被其他用户占用
            if (StringUtils.hasText(updatedUser.getUsername())
                    && !user.getUsername().equals(updatedUser.getUsername())) {

                if (userRepository.existsByUsernameAndIdNot(updatedUser.getUsername(), id)) {
                    logger.warn("用户名 {} 已被其他用户占用", updatedUser.getUsername());
                    throw new RuntimeException("用户名已被占用");
                }
                user.setUsername(updatedUser.getUsername());
            }

            // 更新密码（如果提供了新密码）
            if (StringUtils.hasText(updatedUser.getPassword())) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // 更新角色
            if (updatedUser.getRole() != null) {
                user.setRole(updatedUser.getRole());
            }

            // 更新时间戳
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            logger.info("用户 {} 信息更新成功", savedUser.getUsername());

            return savedUser;
        } catch (Exception e) {
            logger.error("更新用户信息过程发生异常: ", e);
            throw new RuntimeException("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除是否成功
     */
    public boolean deleteUser(Long id) {
        logger.info("尝试删除用户，ID: {}", id);

        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                logger.info("用户删除成功，ID: {}", id);
                return true;
            } else {
                logger.warn("要删除的用户不存在，ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("删除用户过程发生异常: ", e);
            throw new RuntimeException("删除失败：" + e.getMessage());
        }
    }

    /**
     * 根据角色获取用户列表
     * @param role 用户角色
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(UserRole role) {
        logger.info("根据角色获取用户列表: {}", role);
        return userRepository.findByRoleOrderByCreatedAtDesc(role);
    }

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 根据角色统计用户数量
     * @param role 用户角色
     * @return 用户数量
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * 搜索用户（根据用户名模糊查询）
     * @param keyword 搜索关键词
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String keyword) {
        logger.info("搜索用户，关键词: {}", keyword);
        if (!StringUtils.hasText(keyword)) {
            return getAllUsers();
        }
        return userRepository.findByUsernameContaining(keyword);
    }
}