package com.coursehunter.studentService.service;

import com.coursehunter.studentService.dto.StudentDto;
import com.coursehunter.studentService.entity.Students;
import com.coursehunter.studentService.repository.StudentsRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateProfileService {
    private StudentsRepository studentsRepository;

    public CreateProfileService(StudentsRepository studentsRepository) {
        this.studentsRepository = studentsRepository;
    }

    public int createProfile(StudentDto studentDto) {
        Students student = new Students();
        student.setName(studentDto.getName());
        student.setEmail(studentDto.getEmail());

        return studentsRepository.save(student).getId();
    }
}
