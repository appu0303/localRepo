package com.MSIL.MSIL_Project.repository;

import com.MSIL.MSIL_Project.entity.User;
import com.MSIL.MSIL_Project.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByAuthUserId(String authUserId);
    List<User> findByStatus(UserStatus status);
    List<User> findByUserGroup(String userGroup);
    List<User> findByOrganization(String organization);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.role.id = :roleId AND ur.isActive = true")
    List<User> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u JOIN u.userRoles ur WHERE ur.role.role = :roleName AND ur.isActive = true")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoles ur JOIN ur.role.rolePermissions rp " +
            "WHERE rp.permission.module.module = :moduleName AND rp.permission.action = :action " +
            "AND ur.isActive = true AND rp.isActive = true")
    List<User> findUsersWithPermission(@Param("moduleName") String moduleName,
                                       @Param("action") String action);
}