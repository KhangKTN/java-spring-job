package vn.khangktn.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.khangktn.jobhunter.domain.Role;
import vn.khangktn.jobhunter.domain.response.ResRole;
import vn.khangktn.jobhunter.service.RoleService;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;
import vn.khangktn.jobhunter.util.errorException.IdException;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PostMapping("")
    @ApiMessage("Create role succeed!")
    public ResponseEntity<ResRole> createRole(@Valid @RequestBody Role role) throws IdException{
        if(roleService.existedRoleName(role.getName())) throw new IdException("Role name can not duplicate!");
        return ResponseEntity.created(null).body(roleService.createRole(role));
    }
}
