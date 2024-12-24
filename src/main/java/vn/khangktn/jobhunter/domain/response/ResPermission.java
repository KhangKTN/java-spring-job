package vn.khangktn.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.khangktn.jobhunter.domain.Permission;

@Getter @Setter
public class ResPermission {
    private Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;

    public static ResPermission convertToResPermission(Permission permission){
        ResPermission resPermission = new ResPermission();

        resPermission.setId(permission.getId());
        resPermission.setApiPath(permission.getApiPath());
        resPermission.setMethod(permission.getMethod());
        resPermission.setModule(permission.getModule());
        resPermission.setName(permission.getName());

        return resPermission;
    }
}
