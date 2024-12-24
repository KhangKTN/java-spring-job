package vn.khangktn.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.khangktn.jobhunter.domain.Permission;
import vn.khangktn.jobhunter.domain.Role;
import vn.khangktn.jobhunter.domain.response.ResRole;
import vn.khangktn.jobhunter.repository.PermissionRepository;
import vn.khangktn.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository){
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existedRoleName(String name){
        return roleRepository.existsByName(name);
    }

    public ResRole createRole(Role role){
        // Check permission
        List<Long> ids = role.getPermissions().stream().map(permission -> permission.getId()).collect(Collectors.toList());
        role.getPermissions().forEach(permission -> ids.add(permission.getId()));
        List<Permission> permissions = permissionRepository.findByIdIn(ids);
        role.setPermissions(permissions);

        return ResRole.convertToResRole(roleRepository.save(role));
    }
}
