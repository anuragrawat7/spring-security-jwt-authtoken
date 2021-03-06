package spring.security.jwtauth.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.security.jwtauth.dto.request.UserRequestDTO;
import spring.security.jwtauth.dto.response.UserResponseDTO;
import spring.security.jwtauth.entity.User;
import spring.security.jwtauth.repository.UserRepo;
import spring.security.jwtauth.utility.ResponseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private ResponseUtil responseUtil;
    private List<UserResponseDTO> userResponseDTO;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //Service Impl of Create user API
    public ResponseEntity<?> createUser(UserRequestDTO userRequest){
        var user = new User();
        try{

            user.setEmailId(userRequest.getEmailId());
            user.setPassword(customUserDetailsService.passwordEncoder().encode(userRequest.getPassword()));
            user.setUuid(UUID.randomUUID());
            user.setCreatedAt(new Date());
            userRepo.save(user);

            if(user != null){
               return responseUtil.accountCreationResponse(user);
            }else {
                return new ResponseEntity("Exception", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Service Impl of Login API
    public ResponseEntity<?> login(UserRequestDTO userRequest){
        var user = userRepo.findByEmailId(userRequest.getEmailId())
                .orElseThrow(() -> new UsernameNotFoundException("Bad Credentials"));
        if(passwordEncoder.matches(userRequest.getPassword(), user.getPassword())){
            System.out.println("Logged In successfully");
            return responseUtil.loginSuccessResponse(user);
        }
        System.out.println("Invalid username or password");
        throw new UsernameNotFoundException("Bad credentials");
    }

    public ResponseEntity<?> getAllUsers(){
        var users = userRepo.findAll();
        userResponseDTO = new ArrayList<>();
        for (User user: users)
            userResponseDTO.add(new UserResponseDTO(user.getUuid(),user.getEmailId(),user.getCreatedAt()));
        return ResponseEntity.ok(userResponseDTO);
    }
}
