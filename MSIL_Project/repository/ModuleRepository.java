package com.MSIL.MSIL_Project.repository;

import com.MSIL.MSIL_Project.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByModule(String module);
    List<Module> findByIsActive(Boolean isActive);
    List<Module> findByParentModuleIsNull();
    List<Module> findByParentModule(Module parentModule);
    boolean existsByModule(String module);

    @Query("SELECT m FROM Module m WHERE m.isActive = true ORDER BY m.sortOrder, m.displayName")
    List<Module> findAllActiveModulesOrderedBySortOrder();

    @Query("SELECT m FROM Module m WHERE m.parentModule IS NULL AND m.isActive = true ORDER BY m.sortOrder")
    List<Module> findActiveParentModules();

    @Query("SELECT m FROM Module m WHERE m.parentModule.id = :parentId AND m.isActive = true ORDER BY m.sortOrder")
    List<Module> findActiveSubModules(@Param("parentId") Long parentId);
}
