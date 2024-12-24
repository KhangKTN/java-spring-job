package vn.khangktn.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.Permission;
import vn.khangktn.jobhunter.domain.response.ResPermission;
import vn.khangktn.jobhunter.domain.response.ResultPaginationDTO;
import vn.khangktn.jobhunter.service.PermissionService;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;
import vn.khangktn.jobhunter.util.errorException.IdException;





@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    PermissionController(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @PostMapping("")
    @ApiMessage("Create permission succeed!")
    public ResponseEntity<ResPermission> createPermission(@Valid @RequestBody Permission permission) throws IdException {
        if(permissionService.existPermission(permission)) throw new IdException("Permission is exist!");
        ResPermission resPermission = permissionService.createPermission(permission);
        return ResponseEntity.created(null).body(resPermission);
    }

    @PutMapping("")
    @ApiMessage("Update permission succeed!")
    public ResponseEntity<ResPermission> udpatePermission(@Valid @RequestBody Permission permission) throws IdException {
        if(permission.getId() == null) throw new IdException("Missing required parameter!");
        if(permissionService.existPermission(permission)) throw new IdException("Permission is exist!");
        ResPermission resPermission = permissionService.updatePermission(permission);
        return ResponseEntity.ok(resPermission);
    }

    @GetMapping("/{id}")
    @ApiMessage("Get permission succeed!")
    public ResponseEntity<ResPermission> getMethodName(@PathVariable("id") Long id) throws IdException {
        if(id == null) throw new IdException("Missing required parameter!");
        return ResponseEntity.ok(permissionService.getPermissionbyId(id));
    }

    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO> getPermissionList(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(permissionService.getListPermission(spec, pageable));
    }
}
