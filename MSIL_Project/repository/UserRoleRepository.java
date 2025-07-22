package com.MSIL.MSIL_Project.repository;

import com.MSIL.MSIL_Project.entity.Role;
import com.MSIL.MSIL_Project.entity.User;
import com.MSIL.MSIL_Project.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    List<UserRole> findByIsActive(Boolean isActive);
    Optional<UserRole> findByUserAndRole(User user, Role role);
    List<UserRole> findByUserAndIsActive(User user, Boolean isActive);
    List<UserRole> findByRoleAndIsActive(Role role, Boolean isActive);

    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = :userId AND ur.isActive = true")
    List<UserRole> findActiveRolesByUserId(@Param("userId") Long userId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.role.id = :roleId AND ur.isActive = true")
    List<UserRole> findActiveUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.expiresAt < :currentTime AND ur.isActive = true")
    List<UserRole> findExpiredUserRoles(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(ur) > 0 FROM UserRole ur WHERE ur.user.id = :userId AND ur.role.role = :roleName AND ur.isActive = true")
    boolean hasUserRole(@Param("userId") Long userId, @Param("roleName") String roleName);
}
