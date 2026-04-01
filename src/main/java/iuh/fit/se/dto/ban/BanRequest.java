package iuh.fit.se.dto.ban;

import jakarta.validation.constraints.*;

public record BanRequest(
        @NotBlank(message = "Tên bàn không được để trống")
        @Size(max = 50, message = "Tên bàn không được quá 50 ký tự")
        String tenBan,

        @NotNull(message = "Sức chứa không được để trống")
        @Min(value = 1, message = "Sức chứa phải ít nhất là 1 người")
        @Max(value = 20, message = "Sức chứa tối đa là 20 người")
        Integer sucChua
) {}