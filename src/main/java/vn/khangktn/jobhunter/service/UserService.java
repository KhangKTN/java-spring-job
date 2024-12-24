package vn.khangktn.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.khangktn.jobhunter.domain.Company;
import vn.khangktn.jobhunter.domain.User;
import vn.khangktn.jobhunter.domain.response.ResUser;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.repository.RoleRepository;
import vn.khangktn.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleRepository = roleRepository;
    }

    public User handleCreateUser(User user){
        if(user.getCompany() != null){
            Company company = this.companyService.getCompanyById(user.getCompany().getId());
            user.setCompany(company == null ? null : company);
        }
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(Long userId){
        this.userRepository.deleteById(userId);
    }

    public ResUser getUserById(Long userId){
        Optional<User> user = this.userRepository.findById(userId);
        if(user.isPresent()) return ResUser.convertToResUser(user.get());
        return null;
    }

    public User getUserByEmail(String username){
        return this.userRepository.findByEmail(username);
    }

    public User getUserByRefreshTokenAndEmail(String rfToken, String email){
        return this.userRepository.findByRefreshTokenAndEmail(rfToken, email);
    }

    public ResultPaginationDTO getUserList(Specification<User> spec, Pageable pageable){
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();
        Page<User> userPage = this.userRepository.findAll(spec, pageable);
        meta.setPage(userPage.getNumber());
        meta.setPageSize(userPage.getSize());
        meta.setTotalItem(userPage.getTotalElements());
        meta.setTotalPage(userPage.getTotalPages());

        List<ResUser> userList = new ArrayList<>();

        userList = userPage.getContent()
            .stream().map(item -> ResUser.convertToResUser(item))
            .collect(Collectors.toList());
        
        result.setMeta(meta);
        result.setResult(userList);
        return result;
    }

    public ResUser updateUser(User user){
        try {
            Optional<User> optinalUser = userRepository.findById(user.getId());
            if(!optinalUser.isPresent()) return null;
            User updateUser = optinalUser.get();
            updateUser.setName(user.getName());
            updateUser.setAddress(user.getAddress());
            updateUser.setGender(user.getGender());
            updateUser.setAge(user.getAge());

            // Check role
            if(roleRepository.findById(user.getRole().getId()).isPresent()){
                updateUser.setRole(user.getRole());
            }
            updateUser.setRole(null);

            // Check exist company
            if(user.getCompany() != null){
                Company company = this.companyService.getCompanyById(user.getCompany().getId());
                updateUser.setCompany(company == null ? null : company);
            }
            return ResUser.convertToResUser(this.userRepository.save(updateUser));
        } catch (Exception e) {
            return null;
        }
    }

    public void updateRefreshToken(String token, String email){
        User user = this.getUserByEmail(email);
        if(user != null){
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }
}
