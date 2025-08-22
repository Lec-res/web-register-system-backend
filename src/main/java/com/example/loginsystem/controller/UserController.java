package com.example.loginsystem.controller;

import com.example.loginsystem.dto.ApiResponse;
import com.example.loginsystem.dto.LoginRequest;
import com.example.loginsystem.dto.RegisterRequest;
import com.example.loginsystem.entity.User;
import com.example.loginsystem.entity.UserRole;
import com.example.loginsystem.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}) // 允许前端跨域访问
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@Valid @RequestBody LoginRequest request,
                                                   BindingResult bindingResult) {
        logger.info("收到登录请求，用户名: {}", request.getUsername());

        // 验证请求参数
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            logger.warn("登录请求参数验证失败: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数验证失败: " + errorMessage));
        }

        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            if (user != null) {
                // 不返回密码信息
                user.setPassword(null);
                logger.info("用户 {} 登录成功", user.getUsername());
                return ResponseEntity.ok(ApiResponse.success("登录成功", user));
            } else {
                logger.warn("用户 {} 登录失败", request.getUsername());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("用户名或密码错误"));
            }
        } catch (Exception e) {
            logger.error("登录过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("登录失败: " + e.getMessage()));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request,
                                                      BindingResult bindingResult) {
        logger.info("收到注册请求，用户名: {}", request.getUsername());

        // 验证请求参数
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            logger.warn("注册请求参数验证失败: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("参数验证失败: " + errorMessage));
        }

        try {
            User user = userService.register(request);
            // 不返回密码信息
            user.setPassword(null);
            logger.info("用户 {} 注册成功", user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("注册成功", user));
        } catch (RuntimeException e) {
            logger.warn("注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("注册过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("注册失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有用户（仅管理员可访问）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        logger.info("收到获取所有用户请求");

        try {
            List<User> users = userService.getAllUsers();
            // 清除密码信息
            users.forEach(user -> user.setPassword(null));
            logger.info("成功获取用户列表，共 {} 个用户", users.size());
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            logger.error("获取用户列表过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("获取用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        logger.info("收到获取用户请求，ID: {}", id);

        try {
            User user = userService.getUserById(id);
            if (user != null) {
                user.setPassword(null);
                logger.info("成功获取用户信息: {}", user.getUsername());
                return ResponseEntity.ok(ApiResponse.success(user));
            } else {
                logger.warn("用户不存在，ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("用户不存在"));
            }
        } catch (Exception e) {
            logger.error("获取用户信息过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("获取用户信息失败: " + e.getMessage()));
        }
    }

    /**
     * 更新用户信息（仅管理员可访问）
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id,
                                                        @RequestBody User user) {
        logger.info("收到更新用户请求，ID: {}", id);

        try {
            User updatedUser = userService.updateUser(id, user);
            updatedUser.setPassword(null);
            logger.info("用户 {} 信息更新成功", updatedUser.getUsername());
            return ResponseEntity.ok(ApiResponse.success("更新成功", updatedUser));
        } catch (RuntimeException e) {
            logger.warn("更新用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("更新用户过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除用户（仅管理员可访问）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        logger.info("收到删除用户请求，ID: {}", id);

        try {
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                logger.info("用户删除成功，ID: {}", id);
                return ResponseEntity.ok(ApiResponse.success("删除成功"));
            } else {
                logger.warn("要删除的用户不存在，ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("用户不存在"));
            }
        } catch (RuntimeException e) {
            logger.warn("删除用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("删除用户过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("删除失败: " + e.getMessage()));
        }
    }

    /**
     * 根据角色获取用户列表
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable UserRole role) {
        logger.info("收到根据角色获取用户请求，角色: {}", role);

        try {
            List<User> users = userService.getUsersByRole(role);
            // 清除密码信息
            users.forEach(user -> user.setPassword(null));
            logger.info("成功获取 {} 角色用户列表，共 {} 个用户", role, users.size());
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            logger.error("根据角色获取用户列表过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("获取用户列表失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam(required = false) String keyword) {
        logger.info("收到搜索用户请求，关键词: {}", keyword);

        try {
            List<User> users = userService.searchUsers(keyword);
            // 清除密码信息
            users.forEach(user -> user.setPassword(null));
            logger.info("搜索完成，找到 {} 个用户", users.size());
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            logger.error("搜索用户过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("搜索失败: " + e.getMessage()));
        }
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        logger.info("收到检查用户名请求: {}", username);

        try {
            boolean exists = userService.existsByUsername(username);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            logger.error("检查用户名过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("检查失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getUserStatistics() {
        logger.info("收到获取用户统计信息请求");

        try {
            long totalUsers = userService.getAllUsers().size();
            long adminCount = userService.countUsersByRole(UserRole.ADMIN);
            long userCount = userService.countUsersByRole(UserRole.USER);

            var statistics = new Object() {
                public final long total = totalUsers;
                public final long admins = adminCount;
                public final long users = userCount;
            };

            logger.info("成功获取用户统计信息");
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取用户统计信息过程发生异常: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.serverError("获取统计信息失败: " + e.getMessage()));
        }
    }
}