package vn.khangktn.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.khangktn.jobhunter.domain.User;
import vn.khangktn.jobhunter.util.constant.GenderConstant;

@Getter @Setter
public class ResUser {
    private Long id;
    private String name;
    private GenderConstant gender;
    private String address;
    private int age;
    private CompanyUser company;
    private RoleUser role;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class CompanyUser{
        private Long id;
        private String name;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class RoleUser{
        private Long id;
        private String role;
    }

    public static ResUser convertToResUser(User user){
        ResUser resUser = new ResUser();
        resUser.setId(user.getId());
        resUser.setName(user.getName());
        resUser.setGender(user.getGender());
        resUser.setAddress(user.getAddress());
        resUser.setAge(user.getAge());
        
        ResUser.CompanyUser companyUser = null;
        if(user.getCompany() != null) companyUser = new CompanyUser(user.getCompany().getId(), user.getCompany().getName());
        ResUser.RoleUser roleUser = new ResUser.RoleUser(user.getRole().getId(), user.getRole().getName());

        resUser.setCompany(companyUser);
        resUser.setRole(roleUser);

        return resUser;
    }
}
