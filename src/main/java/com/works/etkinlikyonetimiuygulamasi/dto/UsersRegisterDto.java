package com.works.etkinlikyonetimiuygulamasi.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.works.etkinlikyonetimiuygulamasi.entity.Users}
 */
@Value
public class UsersRegisterDto implements Serializable {
    @NotNull
    @Size(min = 3, max = 30)
    @NotEmpty
    @NotBlank
    String username;
    @NotNull
    @Size(min = 5, max = 100)
    @Email
    @NotEmpty
    @NotBlank
    String email;
    @NotNull
    @Size(min = 8, max = 20)
    @NotEmpty
    @NotBlank
    String password;
}