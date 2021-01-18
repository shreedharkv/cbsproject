package com.dfq.coeffi.cbs.member.service;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.member.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BoardMeetingService {

    List<BoardMeeting> getBoardMeetings();
    Optional<BoardMeeting> getBoardMeeting(long id);
    BoardMeeting createBoardMeeting( BoardMeeting boardMeeting);
    List<BoardMeeting> getBoardMeetingByNumber(String boardMeetingNumberFrom, String boardMeetingNumberTo);
    List<BoardMeeting> getBoardMeetingByDate(Date dateFrom, Date dateTo);
    Optional<BoardMeeting> getMemberByBdrNumber(String boardMeetingNumber);

    List<BoardMeeting> getAsOnDateBoardMeeting(Date asOnDate);
}