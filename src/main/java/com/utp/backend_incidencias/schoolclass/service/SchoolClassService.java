package com.utp.backend_incidencias.schoolclass.service;

import com.utp.backend_incidencias.schoolclass.dto.request.CreateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.request.UpdateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.response.SchoolClassResponse;

import java.util.List;

public interface SchoolClassService {

    SchoolClassResponse createClass(CreateSchoolClassRequest req);

    SchoolClassResponse getClassById(Long id);

    List<SchoolClassResponse> getAllClasses();

    List<SchoolClassResponse> getDeletedClasses();

    List<SchoolClassResponse> getMyClasses();

    SchoolClassResponse updateClass(
            Long id,
            UpdateSchoolClassRequest req
    );

    void deleteClass(Long id);

    void restoreClass(Long id);
}
