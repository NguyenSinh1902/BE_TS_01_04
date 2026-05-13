package iuh.fit.se.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // 1. Cấu hình Session Stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 2. Xử lý lỗi trả về JSON
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Phiên đăng nhập không hợp lệ hoặc đã hết hạn!\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"Bạn không có quyền truy cập chức năng này!\"}");
                        })
                )
                // 3. Phân quyền chi tiết
                .authorizeHttpRequests(auth -> auth
                        // Auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // Nhân viên
                        .requestMatchers(HttpMethod.PATCH, "/api/nhan-vien/*/trang-thai").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/nhan-vien/*/vai-tro").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/nhan-vien/*").hasRole("ADMIN")
                        .requestMatchers("/api/nhan-vien/**").authenticated()

                        // Bàn
                        .requestMatchers(HttpMethod.GET, "/api/ban/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/ban/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ban/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/ban/**").hasRole("ADMIN")

                        // Danh mục & Sản phẩm
                        .requestMatchers(HttpMethod.GET, "/api/danh-muc/**", "/api/san-pham/**").authenticated()
                        .requestMatchers("/api/danh-muc/**", "/api/san-pham/**").hasRole("ADMIN")

                        // Khách hàng
                        .requestMatchers(HttpMethod.GET, "/api/khach-hang/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/khach-hang/**").hasAnyRole("ADMIN", "THU_NGAN")
                        .requestMatchers(HttpMethod.PUT, "/api/khach-hang/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/khach-hang/**").hasRole("ADMIN")

                        // Khuyến mãi & Thuế phí
                        .requestMatchers(HttpMethod.GET, "/api/khuyen-mai/**", "/api/thue-phi/**").authenticated()
                        .requestMatchers("/api/khuyen-mai/**", "/api/thue-phi/**").hasRole("ADMIN")

                        // AI Strategy
                        .requestMatchers("/api/ai-strategy/**").hasRole("ADMIN")

                        // Phiếu đặt, Hóa đơn, Thống kê, Gợi ý món
                        .requestMatchers("/api/phieu-dat-ban/**", "/api/hoa-don/**", "/api/thong-ke/**", "/api/goi-y/**").authenticated()

                        // Mặc định các request khác phải đăng nhập
                        .anyRequest().authenticated()
                )
                // 4. Thêm bộ lọc JWT trước bộ lọc UsernamePassword của Spring
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}