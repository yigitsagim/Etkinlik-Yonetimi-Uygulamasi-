package com.works.etkinlikyonetimiuygulamasi.service;

import com.works.etkinlikyonetimiuygulamasi.dto.UsersLoginDto;
import com.works.etkinlikyonetimiuygulamasi.dto.UsersRegisterDto;
import com.works.etkinlikyonetimiuygulamasi.entity.Users;
import com.works.etkinlikyonetimiuygulamasi.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersService {

    final UsersRepository usersRepository;
    final ModelMapper modelMapper;
    final HttpServletRequest request;

  //register
    public ResponseEntity register(UsersRegisterDto usersRegisterDto){
        Optional<Users> usersOptional = usersRepository.findByEmailEqualsIgnoreCase(usersRegisterDto.getEmail());
        if (usersOptional.isEmpty()){
            Users users = modelMapper.map(usersRegisterDto , Users.class);
            usersRepository.save(users);
            return  ResponseEntity.ok().body(users);
        }
        Map<String,Object> hm = Map.of("success",false,"message","This email is already in use");
        return ResponseEntity.badRequest().body(hm);
    }

    //login
    public ResponseEntity login(UsersLoginDto usersLoginDto) {

        Optional<Users> usersOptional = usersRepository.findByEmailEqualsIgnoreCase(usersLoginDto.getEmail());

        if (usersOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        }

        Users users = usersOptional.get();

        if (!users.getPassword().equals(usersLoginDto.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Incorrect password"));
        }

        request.getSession().setAttribute("user", users);

        return ResponseEntity.ok().body(users);
    }

    // logout
    public ResponseEntity logOut() {
        request.getSession().invalidate();
        return ResponseEntity.ok().body("Logout successfully.");
    }
}
