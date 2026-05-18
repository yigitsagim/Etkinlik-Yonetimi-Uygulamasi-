package com.works.etkinlikyonetimiuygulamasi.controller;


import com.works.etkinlikyonetimiuygulamasi.dto.UsersLoginDto;
import com.works.etkinlikyonetimiuygulamasi.dto.UsersRegisterDto;
import com.works.etkinlikyonetimiuygulamasi.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("users")
@RestController
@RequiredArgsConstructor
public class UsersRestController {
    final UsersService usersService;

    @PostMapping("register")
    public ResponseEntity register(@Valid @RequestBody UsersRegisterDto usersRegisterDto){
        return usersService.register(usersRegisterDto);
    }
    @PostMapping("login")
    public ResponseEntity login(@Valid @RequestBody UsersLoginDto usersLoginDto){
        return usersService.login(usersLoginDto);
    }

    @PostMapping("logout")
    public ResponseEntity logout() {
        return usersService.logOut();
    }
}
