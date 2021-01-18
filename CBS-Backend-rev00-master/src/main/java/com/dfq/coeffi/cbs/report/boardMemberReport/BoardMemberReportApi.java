package com.dfq.coeffi.cbs.report.boardMemberReport;

import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.member.entity.BoardMeeting;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.service.BoardMeetingService;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportDto;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@RestController
public class BoardMemberReportApi extends BaseController {

    private final BoardMeetingService boardMeetingService;
    private final PDFExcelFunction pdfExcelFunction;

    @Autowired
    private BoardMemberReportApi(final BoardMeetingService boardMeetingService, PDFExcelFunction pdfExcelFunction) {
        this.boardMeetingService = boardMeetingService;
        this.pdfExcelFunction = pdfExcelFunction;
    }

    @PostMapping("/board-meeting-report")
    public ResponseEntity<List<BoardMeeting>> getBoardMeetingAttendedMember(@RequestBody MeetingNumberDto meetingNumberDto) {
        List<BoardMeeting> boardMeetings = boardMeetingService.getBoardMeetingByNumber(meetingNumberDto.getBoardMeetingNumberFrom(), meetingNumberDto.getBoardMeetingNumberTo());
        if (CollectionUtils.isEmpty(boardMeetings)) {
            throw new EntityNotFoundException("No active board meeting found");
        }
        return new ResponseEntity<>(boardMeetings, HttpStatus.OK);
    }

    @PostMapping("/board-meeting-report-by-date")
    public ResponseEntity<List<BoardMeeting>> getBoardMeetingByDate(@RequestBody MeetingNumberDto meetingNumberDto) {
        List<BoardMeeting> boardMeetings = boardMeetingService.getBoardMeetingByDate(meetingNumberDto.getDateFrom(), meetingNumberDto.getDateTo());
        if (CollectionUtils.isEmpty(boardMeetings)) {
            throw new EntityNotFoundException("No active board meeting found");
        }
        return new ResponseEntity<>(boardMeetings, HttpStatus.OK);
    }

    @PostMapping("/board-meeting/date")
    public ResponseEntity<List<BoardMeeting>> getAsOnDateBoardMeeting(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) throws DocumentException, ParseException, IOException {

        System.out.println("DATE "+depositReportDto.getAsOnDate());

        List<BoardMeeting> boardMeetings = boardMeetingService.getAsOnDateBoardMeeting(depositReportDto.getAsOnDate());

        System.out.println("AAAAAAAAAAAAAAAAAAAAA "+boardMeetings);
        if (CollectionUtils.isEmpty(boardMeetings)) {
            throw new EntityNotFoundException("No active board meeting found");
        }
        return new ResponseEntity<>(boardMeetings, HttpStatus.OK);
    }

}