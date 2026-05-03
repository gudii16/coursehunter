package com.coursehunter.course.query.repository;

import com.coursehunter.course.query.entity.CourseCatalogView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseCatalogViewRepository extends JpaRepository<CourseCatalogView, UUID> {

    List<CourseCatalogView> findByStatus(String status);

    @Query(value = "SELECT * FROM course_inventory.course_catalog_view WHERE :tag = ANY(tags)", nativeQuery = true)
    List<CourseCatalogView> findByTag(@Param("tag") String tag);

    @Query(value = "SELECT * FROM course_inventory.course_catalog_view WHERE title ILIKE %:keyword%", nativeQuery = true)
    List<CourseCatalogView> searchByTitle(@Param("keyword") String keyword);
}
