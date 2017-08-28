package edu.ksu.canvas.attendance.submitter;


import edu.ksu.canvas.attendance.entity.AttendanceAssignment;
import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.exception.AttendanceAssignmentException;
import edu.ksu.canvas.attendance.repository.AttendanceAssignmentRepository;
import edu.ksu.canvas.attendance.services.AttendanceAssignmentService;
import edu.ksu.canvas.attendance.services.AttendanceSectionService;
import edu.ksu.canvas.attendance.services.CanvasApiWrapperService;
import edu.ksu.canvas.model.assignment.Assignment;
import edu.ksu.canvas.oauth.OauthToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Scope(value="session")
public class CanvasAssignmentAssistant {

    private static final Logger LOG = Logger.getLogger(CanvasAssignmentAssistant.class);

    @Autowired
    private AttendanceAssignmentService assignmentService;

    @Autowired
    private AttendanceSectionService sectionService;

    @Autowired
    private AttendanceSectionService attendanceSectionService;

    @Autowired
    private AttendanceAssignmentRepository assignmentRepository;

    @Autowired
    private CanvasApiWrapperService canvasApiWrapperService;

    public Assignment createAssignmentInCanvas(Long courseId, Long canvasSectionId, AttendanceAssignment attendanceAssignment, Long groupId, OauthToken oauthToken) throws AttendanceAssignmentException {
        Optional<Assignment> canvasAssignmentOptional;
        Assignment assignment = generateCanvasAssignment(courseId, attendanceAssignment, groupId);

        System.out.println("Generate: " + courseId + ", " + attendanceAssignment + ", " + groupId);

        try {
            canvasAssignmentOptional = canvasApiWrapperService.createAssignment(courseId, assignment, oauthToken);
        } catch (IOException e) {
            LOG.error("Error while creating canvas assignment for section: " +  attendanceAssignment.getAttendanceSection().getSectionId(), e);
            throw new AttendanceAssignmentException(AttendanceAssignmentException.Error.CREATION_ERROR);
        }

        if(!canvasAssignmentOptional.isPresent())  {
            LOG.error("Error while creating canvas assignment for section: " +  attendanceAssignment.getAttendanceSection().getSectionId());
            throw new AttendanceAssignmentException(AttendanceAssignmentException.Error.CREATION_ERROR);
        }
        Assignment canvasAssignment = canvasAssignmentOptional.get();
        LOG.info("Created canvas assignment: " + canvasAssignment.getId());

        System.out.println("CSI: " + canvasSectionId + " & CA: " + canvasAssignment.getId());

        saveCanvasAssignmentId(canvasSectionId, canvasAssignment);
        return canvasAssignment;
    }

    private Assignment generateCanvasAssignment(Long courseId, AttendanceAssignment attendanceAssignment, Long groupId) {
        Assignment assignment = new Assignment();
        assignment.setAssignmentGroupId(groupId);
        assignment.setName(attendanceAssignment.getAssignmentName());
        assignment.setPointsPossible(Double.valueOf(attendanceAssignment.getAssignmentPoints()));
        assignment.setCourseId(courseId.toString());
        assignment.setPublished(true);
        assignment.setUnpublishable(false);
        assignment.setMuted("true");
        return assignment;
    }

    /**
     * This function saves the canvas assignment id into the attendance assignment of all sections.
     * This works this way because for the user this works in the course level. But in the back-end
     * this works section based.
     */
    private void saveCanvasAssignmentId(Long canvasSectionId, Assignment canvasAssignment) {
        AttendanceAssignment attendanceAssignment = sectionService.getSection(canvasSectionId).getAttendanceAssignment();
        LOG.warn(attendanceAssignment);
        attendanceAssignment.setCanvasAssignmentId(Long.valueOf(canvasAssignment.getId()));
        assignmentRepository.save(attendanceAssignment);

    }


    public void editAssignmentInCanvas(Long courseId, AttendanceAssignment attendanceAssignment, OauthToken oauthToken) throws AttendanceAssignmentException{

        Optional<Assignment> canvasAssignmentOptional;
        try {
            canvasAssignmentOptional = canvasApiWrapperService.getSingleAssignment(courseId, oauthToken, attendanceAssignment.getCanvasAssignmentId().toString());
        } catch (IOException e) {
            LOG.error("Error while getting assignment from canvas for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            throw new AttendanceAssignmentException(AttendanceAssignmentException.Error.NO_CONNECTION);
        }

        if(!canvasAssignmentOptional.isPresent()) {
            throw new AttendanceAssignmentException(AttendanceAssignmentException.Error.NO_ASSIGNMENT_FOUND);
        }

        Assignment canvasAssignment = canvasAssignmentOptional.get();
        canvasAssignment.setName(attendanceAssignment.getAssignmentName());
        canvasAssignment.setPointsPossible(Double.valueOf(attendanceAssignment.getAssignmentPoints()));
        try {
            canvasApiWrapperService.editAssignment(courseId.toString(), canvasAssignment, oauthToken);
        } catch (IOException e) {
            LOG.error("Error while editing canvas assignment for section: " + attendanceAssignment.getAttendanceSection().getSectionId(), e);
            throw new AttendanceAssignmentException(AttendanceAssignmentException.Error.EDITING_ERROR);
        }

        LOG.info("Canvas assignment edited based on information in the section configuration.");
    }

    public void deleteAssignmentInCanvas(List<AttendanceSection> sectionList, OauthToken oauthToken) throws AttendanceAssignmentException{

        for (AttendanceSection section: sectionList) {
            AttendanceAssignment assignment = assignmentRepository.findByAttendanceSection(section);

            if (assignment == null) {
                LOG.error("Attendance assignment not found for section: " + section.getName());
                return;
            } else if (assignment.getCanvasAssignmentId() == null) {
                LOG.info("No Canvas assignment associated to Attendance assignment to be deleted for section: " + section.getName());
                return;
            }

            try {
                canvasApiWrapperService.deleteAssignment(section.getCanvasCourseId().toString(), assignment.getCanvasAssignmentId().toString(), oauthToken);
            } catch (IOException e) {
                LOG.error("Error while deleting canvas assignment: " + assignment.getCanvasAssignmentId(), e);
                throw new AttendanceAssignmentException(AttendanceAssignmentException.Error.DELETION_ERROR);
            }
            LOG.info("Canvas assignment " + assignment.getCanvasAssignmentId() + " was successfully deleted.");
        }
    }
}
