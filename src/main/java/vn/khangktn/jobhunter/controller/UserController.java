package vn.khangktn.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.User;
import vn.khangktn.jobhunter.domain.response.ResUser;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.service.UserService;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;
import vn.khangktn.jobhunter.util.errorException.IdException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("")
    @ApiMessage("Create new user succeed!")
    public ResponseEntity<User> createNewUser(@Valid @RequestBody User user) throws IdException{
        User isExisted = this.userService.getUserByEmail(user.getEmail());
        if(isExisted != null) throw new IdException("Your email is exist!");
        
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);
        newUser.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("")
    public ResponseEntity<ResUser> updateUser(@RequestBody User user) throws IdException{
        ResUser existUser = this.userService.getUserById(user.getId());
        if(existUser == null) throw new IdException("User isn't exist!");

        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        return ResponseEntity.ok(this.userService.updateUser(user));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResUser> getUser(@PathVariable("userId") String userId) throws IdException{
        long userIdLong;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (Exception e) {
            throw new IdException("Id must is numberic!");
        }
        ResUser user = this.userService.getUserById(userIdLong);
        if(user == null) throw new IdException("This user isn't exist!");
        return ResponseEntity.ok(user);
    }

    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO> getUserList(@Filter Specification<User> spec, Pageable pageable){
        return ResponseEntity.ok(this.userService.getUserList(spec, pageable));
    }
    
    @DeleteMapping("/{userId}")
    @ApiMessage("Delete user succeed!")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") String userId) throws IdException{
        long userIdLong;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (Exception e) {
            throw new IdException("Id must is numberic!");
        }
        ResUser existUser = this.userService.getUserById(userIdLong);
        if(existUser == null) throw new IdException("This user isn't exist!");
        this.userService.handleDeleteUser(userIdLong);
        return ResponseEntity.ok(null);
    }
}
