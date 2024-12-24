package vn.khangktn.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.khangktn.jobhunter.domain.Permission;


@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    public boolean existsByApiPathAndMethodAndModule(String apiPath, String method, String module);
    List<Permission> findByIdIn(List<Long> ids);
}
