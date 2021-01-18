package com.dfq.coeffi.cbs.member.service;

import com.dfq.coeffi.cbs.member.entity.BoardMeeting;
import com.dfq.coeffi.cbs.member.repository.BoardMeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
@Transactional
public class BoardMeetingServiceImpl implements BoardMeetingService {

    @Autowired
    private BoardMeetingRepository boardMeetingRepository;

    @Override
    public List<BoardMeeting> getBoardMeetings() {
        return boardMeetingRepository.findAll();
    }

    @Override
    public Optional<BoardMeeting> getBoardMeeting(long id) {
        return ofNullable(boardMeetingRepository.findOne(id));
    }

    @Override
    public BoardMeeting createBoardMeeting(BoardMeeting boardMeeting) {
        return boardMeetingRepository.save(boardMeeting);
    }

    @Override
    public List<BoardMeeting> getBoardMeetingByNumber(String boardMeetingNumberFrom, String boardMeetingNumberTo) {
        return boardMeetingRepository.getBoardmeetingByNumber(boardMeetingNumberFrom, boardMeetingNumberTo);
    }

    @Override
    public List<BoardMeeting> getBoardMeetingByDate(Date dateFrom, Date dateTo) {
        return boardMeetingRepository.getBoardMembersByDate(dateFrom,dateTo);
    }

    @Override
    public Optional<BoardMeeting> getMemberByBdrNumber(String boardMeetingNumber) {
        return ofNullable(boardMeetingRepository.findByBoardMeetingNumber(boardMeetingNumber));
    }

    @Override
    public List<BoardMeeting> getAsOnDateBoardMeeting(Date asOnDate) {
        return boardMeetingRepository.getAsOnDateBoardMeeting(asOnDate);
    }
}