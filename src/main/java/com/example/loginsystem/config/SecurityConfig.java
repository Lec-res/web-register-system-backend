package com.example.loginsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 安全配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码编码器Bean
     * 使用BCrypt加密算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF保护（因为是REST API）
                .csrf(csrf -> csrf.disable())

                // 配置CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 配置请求授权
                .authorizeHttpRequests(authz -> authz
                        // 允许登录和注册接口无需认证
                        .requestMatchers("/api/users/login", "/api/users/register").permitAll()

                        // 允许检查用户名接口无需认证
                        .requestMatchers("/api/users/check-username").permitAll()

                        // 健康检查和错误页面
                        .requestMatchers("/actuator/health", "/error").permitAll()

                        // 静态资源
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                        // API文档相关（如果使用Swagger）
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // 管理员专用接口
                        .requestMatchers("/api/users/statistics").hasRole("ADMIN")
                        .requestMatchers("/api/users/role/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/search").hasRole("ADMIN")

                        // 其他用户管理接口需要管理员权限
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 配置会话管理为无状态（适用于REST API）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 禁用默认登录页面
                .formLogin(form -> form.disable())

                // 禁用默认登出
                .logout(logout -> logout.disable())

                // 禁用HTTP Basic认证
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * CORS配置源
     * 允许前端跨域访问
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源地址
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));

        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"
        ));

        // 允许的头部
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 允许发送认证信息（如cookies）
        configuration.setAllowCredentials(true);

        // 预检请求的有效期
        configuration.setMaxAge(3600L);

        // 暴露的头部（前端可以访问的响应头）
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization",
                "Content-Type"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}