package vn.khangktn.jobhunter.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.User;
import vn.khangktn.jobhunter.domain.request.ReqLoginDTO;
import vn.khangktn.jobhunter.domain.response.ResLoginDTO;
import vn.khangktn.jobhunter.domain.response.ResUser;
import vn.khangktn.jobhunter.service.UserService;
import vn.khangktn.jobhunter.util.SecurityUtil;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;
import vn.khangktn.jobhunter.util.errorException.IdException;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder){
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<ResUser> registerUser(@Valid @RequestBody User user) throws IdException {
        if(userService.getUserByEmail(user.getEmail()) != null) throw new IdException("Email is exist!");
        String hashPw = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPw);
        return ResponseEntity.created(null).body(ResUser.convertToResUser(userService.handleCreateUser(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO){
        // Push info user to verify
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        // Authentication user => loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Get more user information
        User userDetail = this.userService.getUserByEmail(loginDTO.getUsername());
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        if(userDetail != null){
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(0, userDetail.getEmail(), userDetail.getName(), userDetail.getRole());
            resLoginDTO.setUser(userLogin);
        }

        // Create access token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), resLoginDTO);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        resLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), resLoginDTO);

        // Update refreshToken into user
        this.userService.updateRefreshToken(refreshToken, loginDTO.getUsername());

        // Set cookies
        ResponseCookie cookie = ResponseCookie.from("rfToken", refreshToken)
            .maxAge(24*60*60)
            .httpOnly(true)
            .path("/")
            .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(resLoginDTO);
    } 

    @GetMapping("/account")
    @ApiMessage("Get account information")
    public ResponseEntity<ResLoginDTO.UserAccount> getUserAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User userCurrent = this.userService.getUserByEmail(email);
        
        ResLoginDTO.UserAccount userAccount = null;
        if(userCurrent != null){
            userAccount = new ResLoginDTO.UserAccount(new ResLoginDTO.UserLogin(userCurrent.getId(), userCurrent.getEmail(), userCurrent.getName(), userCurrent.getRole()));
        }
        return ResponseEntity.ok(userAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get new token succeed!")
    public ResponseEntity<ResLoginDTO> getMethodName(@CookieValue(name = "rfToken", defaultValue = "") String rfToken) throws Exception {
        // Decode token and check again in db
        Jwt decodedJwt = this.securityUtil.checkValidRfToken(rfToken);
        if(decodedJwt == null) throw new IdException("Token cannot encode!");
        String email = decodedJwt.getSubject();
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(rfToken, email);
        if(currentUser == null) throw new IdException("Token isn't correct!");

        // Get new access token
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getName(), currentUser.getRole());
        resLoginDTO.setUser(userLogin);
        String accessToken = this.securityUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        return ResponseEntity.ok(resLoginDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> getMethodName() throws Exception {
        // Get user by username
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
            SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.getUserByEmail(email);
        if(currentUser == null) ResponseEntity.badRequest();
        
        // Handle remove refresh token
        this.userService.updateRefreshToken(null, email); // Remove in db
        ResponseCookie deleteSpringCookie = ResponseCookie.from("rfToken", null)
            .maxAge(0)
            .path("/")
            .build(); // Remove cookies
            
        return ResponseEntity.created(null)
            .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
            .body(null);
    }
}
