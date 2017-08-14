package edu.ksu.canvas.attendance.services;

import edu.ksu.canvas.attendance.entity.AttendanceCourse;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.enums.AttendanceType;
import edu.ksu.canvas.attendance.form.CourseConfigurationForm;
import edu.ksu.canvas.attendance.repository.AttendanceCourseRepository;
import edu.ksu.canvas.attendance.repository.AttendanceSectionRepository;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AttendanceCourseService {

    @Autowired
    private AttendanceCourseRepository attendanceCourseRepository;

    @Autowired
    private AttendanceSectionRepository attendanceSectionRepository;

    private static final Logger LOG = Logger.getLogger(AttendanceCourseService.class);

    /**
     * @throws RuntimeException when courseForm is null
     */
    public void save(CourseConfigurationForm courseForm, long canvasSectionId) {
        Validate.notNull(courseForm, "courseForm must not be null");

        AttendanceSection attendanceSection = attendanceSectionRepository.findByCanvasSectionId(canvasSectionId);
        if (attendanceSection == null) {
            attendanceSection = new AttendanceSection(canvasSectionId, courseForm.getTotalClassMinutes(), courseForm.getDefaultMinutesPerSession());
        } else {

            attendanceSection.setShowNotesToStudents(courseForm.getShowNotesToStudents());

            if (courseForm.getSimpleAttendance() != null && courseForm.getSimpleAttendance()) {
                attendanceSection.setAttendanceType(AttendanceType.SIMPLE);
            } else {
                attendanceSection.setAttendanceType(AttendanceType.MINUTES);
                attendanceSection.setDefaultMinutesPerSession(courseForm.getDefaultMinutesPerSession());
                attendanceSection.setTotalMinutes(courseForm.getTotalClassMinutes());
            }
        }

        attendanceSectionRepository.save(attendanceSection);
    }


    /**
     * @throws RuntimeException if course does not exist or if the courseForm is null
     */
    public void loadIntoForm(CourseConfigurationForm courseForm, long canvasSectionId) {
        Validate.notNull(courseForm, "courseForm must not be null");
        LOG.info("...................................... Section ID: " + canvasSectionId);

        AttendanceSection attendanceSection = attendanceSectionRepository.findByCanvasSectionId(canvasSectionId);
        LOG.info("Section: " + attendanceSection.toString());
        if(attendanceSection == null) {
            RuntimeException e = new IllegalArgumentException("Cannot load data into courseForm for non-existent section");
            throw new ContextedRuntimeException(e).addContextValue("canvasSectionId", canvasSectionId);
        }
        if(courseForm.getSimpleAttendance()) {
            courseForm.setShowNotesToStudents(attendanceSection.getShowNotesToStudents());
        } else {
            courseForm.setShowNotesToStudents(attendanceSection.getShowNotesToStudents());
            courseForm.setTotalClassMinutes(attendanceSection.getTotalMinutes());
            courseForm.setDefaultMinutesPerSession(attendanceSection.getDefaultMinutesPerSession());
        }

//        courseForm.setTotalClassMinutes(attendanceSection.getTotalMinutes());
//        courseForm.setDefaultMinutesPerSession(attendanceSection.getDefaultMinutesPerSession());
//        courseForm.setSimpleAttendance(attendanceSection.getAttendanceType().equals(AttendanceType.SIMPLE));
//        courseForm.setShowNotesToStudents(attendanceSection.getShowNotesToStudents());
    }

    public AttendanceCourse findByCanvasCourseId(Long courseId) {
        return attendanceCourseRepository.findByCanvasCourseId(courseId);
    }

}
