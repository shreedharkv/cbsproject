package com.dfq.coeffi.cbs.member.repository;

import com.dfq.coeffi.cbs.deposit.entity.ChildrensDeposit;
import com.dfq.coeffi.cbs.deposit.entity.DepositType;
import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.member.entity.AdditionalShare;
import com.dfq.coeffi.cbs.member.entity.BoardMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BoardMeetingRepository extends JpaRepository<BoardMeeting, Long> {

    @Query("SELECT boardMeeting FROM BoardMeeting boardMeeting " +
            "WHERE (:boardMeetingNumberFrom is null or :boardMeetingNumberTo is null or boardMeeting.boardMeetingNumber BETWEEN :boardMeetingNumberFrom AND :boardMeetingNumberTo)")
    List<BoardMeeting> getBoardmeetingByNumber(@Param("boardMeetingNumberFrom") String boardMeetingNumberFrom, @Param("boardMeetingNumberTo") String boardMeetingNumberTo);

    @Query("SELECT boardMeeting FROM BoardMeeting boardMeeting WHERE " +
            "(date(boardMeeting.meetingDate) BETWEEN :dateFrom AND :dateTo)")
    List<BoardMeeting> getBoardMembersByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    BoardMeeting findByBoardMeetingNumber(String boardMeetingNumber);

    @Query("SELECT boardMeeting FROM BoardMeeting boardMeeting WHERE (date(boardMeeting.credtedOn) <= :inputDate) ")
    List<BoardMeeting> getAsOnDateBoardMeeting(@Param("inputDate") Date inputDate);
}