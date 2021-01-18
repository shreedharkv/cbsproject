package com.dfq.coeffi.cbs.report.monthlySchedule;

import com.dfq.coeffi.cbs.loan.entity.loan.Repayment;
import com.dfq.coeffi.cbs.member.entity.RefundShare;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@ToString
public class MonthlyScheduleDto {

    public long customerId;
    public long number;
    public String memberNumber;
    public String exgMemberNumber;
    public String name;
    public BigDecimal depositAmount;
    public String depositMonth;
    public String accountNumber;

    public BigDecimal shareAmount;
    public BigDecimal sanctionedAmount;
    public List<RefundShare> refundShares;

    private List<Repayment> repayments;
}
