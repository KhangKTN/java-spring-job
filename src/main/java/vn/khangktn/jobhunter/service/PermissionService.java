package vn.khangktn.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.khangktn.jobhunter.domain.Permission;
import vn.khangktn.jobhunter.domain.response.ResPermission;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    
    PermissionService(PermissionRepository permissionRepository){
        this.permissionRepository = permissionRepository;
    }

    public ResPermission createPermission(Permission permission){
        if(permissionRepository.existsByApiPathAndMethodAndModule(permission.getApiPath(), permission.getMethod(), permission.getModule())){
            return null;
        }
        return ResPermission.convertToResPermission(permissionRepository.save(permission));
    }

    public ResPermission updatePermission(Permission permission){
        Optional<Permission> currentPermission = permissionRepository.findById(permission.getId());
        if(currentPermission.isPresent()){
            Permission updatPermission = currentPermission.get();
            updatPermission.setMethod(permission.getMethod());
            updatPermission.setApiPath(permission.getApiPath());
            updatPermission.setModule(permission.getModule());
            return ResPermission.convertToResPermission(permissionRepository.save(updatPermission));
        }

        return null;
    }

    public boolean existPermission(Permission permission){
        return permissionRepository.existsByApiPathAndMethodAndModule(permission.getApiPath(), permission.getMethod(), permission.getModule());
    }

    public ResPermission getPermissionbyId(Long id){
        Optional<Permission> permissOptional = permissionRepository.findById(id);
        if(permissOptional.isPresent()) return ResPermission.convertToResPermission(permissOptional.get());
        return null;
    }

    public ResultPaginationDTO getListPermission(Specification<Permission> spec, Pageable pageable){
        ResultPaginationDTO result = new ResultPaginationDTO();
        return result;
    }


}
