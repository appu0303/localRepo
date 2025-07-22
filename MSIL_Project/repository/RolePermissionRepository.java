package com.MSIL.MSIL_Project.repository;

import com.MSIL.MSIL_Project.entity.Permission;
import com.MSIL.MSIL_Project.entity.Role;
import com.MSIL.MSIL_Project.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(Role role);
    List<RolePermission> findByPermission(Permission permission);
    List<RolePermission> findByIsActive(Boolean isActive);
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
    List<RolePermission> findByRoleAndIsActive(Role role, Boolean isActive);
    List<RolePermission> findByPermissionAndIsActive(Permission permission, Boolean isActive);

    @Query("SELECT rp FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.isActive = true")
    List<RolePermission> findActivePermissionsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT rp FROM RolePermission rp WHERE rp.permission.id = :permissionId AND rp.isActive = true")
    List<RolePermission> findActiveRolesByPermissionId(@Param("permissionId") Long permissionId);

    @Query("SELECT COUNT(rp) > 0 FROM RolePermission rp WHERE rp.role.id = :roleId AND " +
            "rp.permission.module.module = :moduleName AND rp.permission.action = :action AND rp.isActive = true")
    boolean hasRolePermission(@Param("roleId") Long roleId,
                              @Param("moduleName") String moduleName,
                              @Param("action") String action);

    @Query("SELECT DISTINCT rp FROM RolePermission rp JOIN rp.role.userRoles ur " +
            "WHERE ur.user.id = :userId AND ur.isActive = true AND rp.isActive = true")
    List<RolePermission> findPermissionsByUserId(@Param("userId") Long userId);
}
