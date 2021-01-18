package com.dfq.coeffi.cbs.member.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.member.entity.BoardMeeting;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.NumberFormat;
import com.dfq.coeffi.cbs.member.service.BoardMeetingService;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class BoardMeetingApi extends BaseController {

    private final MemberService memberService;
    private final ApplicationLogService applicationLogService;
    private final BoardMeetingService boardMeetingService;
    private final BODDateService bodDateService;
    private final LoanService loanService;

    @Autowired
    private BoardMeetingApi(final MemberService memberService,final ApplicationLogService applicationLogService,
                      final BoardMeetingService boardMeetingService,final BODDateService bodDateService,
                            final LoanService loanService){
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.boardMeetingService = boardMeetingService;
        this.bodDateService = bodDateService;
        this.loanService = loanService;
    }

    @GetMapping("/board-meeting")
    public ResponseEntity<List<BoardMeeting>> getBoardMeeting() {
        List<BoardMeeting> boardMeetings = boardMeetingService.getBoardMeetings();
        if (CollectionUtils.isEmpty(boardMeetings)) {
            throw new EntityNotFoundException("No active board meeting found");
        }
        return new ResponseEntity<>(boardMeetings, HttpStatus.OK);
    }

    @PostMapping("/board-meeting")
    public ResponseEntity<BoardMeeting> createNewBoardMeeting(@RequestBody BoardMeeting boardMeeting, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);

        NumberFormat numberFormat = memberService.getNumberFormatByType("Board_Member");
        String boardMeetingNumber = numberFormat.getPrefix() + "-" + (numberFormat.getBoardMeetingNumber() + 1);

        List<Member> members = new ArrayList<>();
        if(boardMeeting.getBoardAttendedMembers() != null){
            for(Member member : boardMeeting.getBoardAttendedMembers()){
                Optional<Member> persistedMember = memberService.getMember(member.getId());
                Member member1 = persistedMember.get();
                members.add(member1);
            }
            boardMeeting.setBoardAttendedMembers(members);
        }
        List<Member> approvedMembers = new ArrayList<>();

        if(boardMeeting.getMemberApproval() != null){
            for(Member member : boardMeeting.getMemberApproval()){
                Member memberForApproval = memberService.getUnApprovedMember(member.getId());
                memberForApproval.setApprovedStatus(true);
                memberService.saveMember(memberForApproval);
                approvedMembers.add(memberForApproval);
            }
            boardMeeting.setMemberApproval(approvedMembers);
        }
        List<Loan> loanApplications = new ArrayList<>();

        if(boardMeeting.getLoanApplications() != null){
            for(Loan loan : boardMeeting.getLoanApplications()){
                Optional<Loan> loanObj = loanService.appliedGoldLoan(loan.getId());
                Loan loanApplication = loanObj.get();
                loanApplications.add(loanApplication);
            }
            boardMeeting.setLoanApplications(loanApplications);
        }

        boardMeeting.setBoardMeetingNumber(boardMeetingNumber);
        BoardMeeting persistedBoardMeeting = boardMeetingService.createBoardMeeting(boardMeeting);

        if(persistedBoardMeeting != null){
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member Board meeting  no. " + persistedBoardMeeting.getId() + " created",
                    "MEMBER BOARD MEETING", loggedUser.getId());

            numberFormat.setBoardMeetingNumber(numberFormat.getBoardMeetingNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        }
        return new ResponseEntity<>(persistedBoardMeeting, HttpStatus.OK);
    }
}
