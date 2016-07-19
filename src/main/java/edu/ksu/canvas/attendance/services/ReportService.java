package edu.ksu.canvas.attendance.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ksu.canvas.attendance.model.AttendanceSummaryModel;
import edu.ksu.canvas.attendance.repository.ReportRepository;


@Component
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;


    public List<AttendanceSummaryModel> getAttendanceSummaryReport(long sectionId) {
        return reportRepository.getAttendanceSummary(sectionId);
    }

}