package vn.khangktn.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.khangktn.jobhunter.domain.Role;

@Getter @Setter
public class ResRole {
    private Long id;
    private String name;
    private String description;
    private boolean active;

    public static ResRole convertToResRole(Role role){
        ResRole resRole = new ResRole();
        
        resRole.setId(role.getId());
        resRole.setName(role.getName());
        resRole.setActive(role.isActive());
        resRole.setDescription(role.getDescription());
        return resRole;
    }
}
