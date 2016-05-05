package edu.ksu.canvas.aviation.entity;

import edu.ksu.canvas.aviation.entity.Attendance;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "aviation_student")
public class AviationStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "student_id")
    private Long studentId; //aviation project's local student Id

    // Canvas has the authoritative data.
    @Column(name = "sis_user_id", nullable = false)
    private String sisUserId; //institution's student ID, e.g. WID

    // Canvas has the authoritative data.
    @Column(name = "student_name")
    private String name;

    @Column(name = "course_id", nullable=false)
    private Long courseId;
    
    // Canvas has the authoritative data.
    @Column(name = "section_id", nullable=false)
    private Long sectionId;

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendances;

    @Transient
    private Double percentageOfCourseMissed;

    
    
    public Double getPercentageOfCourseMissed() {
        return percentageOfCourseMissed;
    }

    public void setPercentageOfCourseMissed(double percentageOfCourseMissed) {
        this.percentageOfCourseMissed = percentageOfCourseMissed;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public String getSisUserId() {
        return sisUserId;
    }

    public void setSisUserId(String sisUserId) {
        this.sisUserId = sisUserId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    
    @Override
    public String toString() {
        return "Student [studentId=" + studentId + ", sisUserId=" + sisUserId + ", name=" + name + ", courseId="
                + courseId + ", sectionId=" + sectionId + ", percentageOfCourseMissed="
                + percentageOfCourseMissed + "]";
    }
    
    
}
