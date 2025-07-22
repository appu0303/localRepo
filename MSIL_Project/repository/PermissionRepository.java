package com.MSIL.MSIL_Project.repository;

import com.MSIL.MSIL_Project.entity.ActionType;
import com.MSIL.MSIL_Project.entity.Module;
import com.MSIL.MSIL_Project.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByModule(Module module);
    List<Permission> findByAction(ActionType action);
    List<Permission> findByIsActive(Boolean isActive);
    Optional<Permission> findByModuleAndAction(Module module, ActionType action);
    Optional<Permission> findByPermission(String permission);
    boolean existsByModuleAndAction(Module module, ActionType action);

    @Query("SELECT p FROM Permission p WHERE p.module.module = :moduleName AND p.action = :action AND p.isActive = true")
    Optional<Permission> findByModuleNameAndAction(@Param("moduleName") String moduleName,
                                                   @Param("action") ActionType action);

    @Query("SELECT p FROM Permission p WHERE p.isActive = true ORDER BY p.module.sortOrder, p.action")
    List<Permission> findAllActivePermissions();

    @Query("SELECT p FROM Permission p JOIN p.rolePermissions rp WHERE rp.role.id = :roleId AND rp.isActive = true")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT DISTINCT p FROM Permission p JOIN p.rolePermissions rp JOIN rp.role.userRoles ur " +
            "WHERE ur.user.id = :userId AND ur.isActive = true AND rp.isActive = true AND p.isActive = true")
    List<Permission> findByUserId(@Param("userId") Long userId);
}