package iuh.fit.se.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                Integer idNhanVien = jwtUtil.extractIdNhanVien(token);

                // 1. Lấy vai trò từ token (Giả sử hàm này ông đã viết trong JwtUtil)
                String vaiTro = jwtUtil.extractRole(token);

                // 2. Tạo danh sách quyền với tiền tố ROLE_
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (vaiTro != null) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + vaiTro));
                }

                // 3. Nạp authorities vào đây thay vì để ArrayList trống
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, idNhanVien, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}