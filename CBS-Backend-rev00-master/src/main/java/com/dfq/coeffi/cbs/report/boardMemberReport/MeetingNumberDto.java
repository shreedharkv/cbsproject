package com.dfq.coeffi.cbs.report.boardMemberReport;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class MeetingNumberDto {

    private String boardMeetingNumberFrom;
    private String boardMeetingNumberTo;
    private Date dateFrom;
    private Date dateTo;
}