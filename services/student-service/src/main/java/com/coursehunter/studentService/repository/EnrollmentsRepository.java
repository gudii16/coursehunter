package com.coursehunter.studentService.repository;

import com.coursehunter.studentService.entity.Enrollments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentsRepository extends JpaRepository<Enrollments, Integer> {
}
