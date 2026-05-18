package com.works.etkinlikyonetimiuygulamasi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.works.etkinlikyonetimiuygulamasi.entity.Users}
 */
@Value
public class UsersLoginDto implements Serializable {

    String email;
    @NotNull
    @Size(min = 8, max = 20)
    @NotEmpty
    @NotBlank
    String password;
}