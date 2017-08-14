package edu.ksu.canvas.attendance.entity;

import edu.ksu.canvas.attendance.enums.AttendanceType;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "attendance_section")
public class AttendanceSection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "section_id")
    private Long sectionId; //attendance project's local section Id

    // Canvas has the authoritative data.
    @Column(name = "canvas_course_id", nullable = false)
    private Long canvasCourseId;

    @Column(name = "canvas_section_id", nullable = false)
    private Long canvasSectionId;

    // Canvas has the authoritative data.
    @Column(name = "section_name")
    private String name;

    @NotNull()
    @Min(1)
    @Column(name = "total_minutes")
    private int totalMinutes;

    @NotNull()
    @Min(1)
    @Column(name = "default_minutes_per_session")
    private int defaultMinutesPerSession;

    @Column(name = "show_notes")
    private Boolean showNotesToStudents = false;

    //TODO: nullable = false
    @Column(name = "attendance_type")
    @Enumerated(EnumType.STRING)
    private AttendanceType attendanceType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, mappedBy = "attendanceSection")
    private AttendanceAssignment attendanceAssignment;

    public AttendanceSection() {
        this.attendanceType = AttendanceType.SIMPLE;
    }

    public AttendanceSection(Long canvasSectionId, int totalClassMinutes, int defaultMinutesPerSession) {
        this.canvasSectionId = canvasSectionId;
        this.totalMinutes = totalClassMinutes;
        this.defaultMinutesPerSession = defaultMinutesPerSession;
        this.attendanceType = AttendanceType.SIMPLE;
    }
    public int getTotalMinutes() { return totalMinutes; }

    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }

    public int getDefaultMinutesPerSession() { return defaultMinutesPerSession; }

    public void setDefaultMinutesPerSession(int defaultMinutesPerSession) { this.defaultMinutesPerSession = defaultMinutesPerSession; }

    public Boolean getShowNotesToStudents() {
        return showNotesToStudents;
    }

    public void setShowNotesToStudents(Boolean showNotesToStudents) {
        this.showNotesToStudents = showNotesToStudents;
    }

    public AttendanceType getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(AttendanceType attendanceType) {
        this.attendanceType = attendanceType;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public Long getCanvasSectionId() {
        return canvasSectionId;
    }

    public void setCanvasSectionId(Long canvasSectionId) {
        this.canvasSectionId = canvasSectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttendanceAssignment getAttendanceAssignment() {
        return attendanceAssignment;
    }


    public void setAttendanceAssignment(AttendanceAssignment attendanceAssignment) {
        this.attendanceAssignment = attendanceAssignment;
    }


    @Override
    public String toString() {
        return "AttendanceSection [sectionId=" + sectionId + ", canvasCourseId=" + canvasCourseId + ", canvasSectionId="
                + canvasSectionId + ", name=" + name +"]";
    }

}
