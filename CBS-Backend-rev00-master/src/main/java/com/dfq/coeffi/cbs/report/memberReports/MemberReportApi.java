package com.dfq.coeffi.cbs.report.memberReports;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.member.entity.*;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportDto;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.GeneratePdfReport;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MemberReportApi extends BaseController {
    private final MemberReportService memberReportService;
    private final PDFExcelFunction pdfExcelFunction;
    private final MemberService memberService;
    private final LoanService loanService;
    @Autowired
    private MemberReportApi(MemberReportService memberReportService, PDFExcelFunction pdfExcelFunction,
                            MemberService memberService, LoanService loanService) {
        this.memberReportService = memberReportService;
        this.pdfExcelFunction = pdfExcelFunction;
        this.memberService = memberService;
        this.loanService = loanService;
    }

    @PostMapping("report/member")
    public ResponseEntity<List<Member>> getMemberListByDate(@RequestBody DepositReportDto depositReportDto) {
        List<Member> memberList = memberReportService.getMembersReportByDate(depositReportDto.inputDate);
        if (CollectionUtils.isEmpty(memberList)) {
            return new ResponseEntity<>(memberList, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }

    @PostMapping("report/member/export-to-excel")
    public ResponseEntity getMembersReport(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        try {
            List<Member> members = memberReportService.getMembersReportByDate(memberReportDto.inputDate);
            String fileName = "Member-List_" + memberReportDto.inputDate;
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet s = w.createSheet("Member-List", 0);
            s.addCell(new Label(0, 0, "No"));
            s.addCell(new Label(1, 0, "Member No"));
            s.addCell(new Label(2, 0, "Customer ID"));
            s.addCell(new Label(3, 0, "Name"));
          /*  s.addCell(new Label(4, 0, "Family Member Name"));
            s.addCell(new Label(5, 0, "Residential Address"));*/
            s.addCell(new Label(6, 0, "Application#"));
            s.addCell(new Label(7, 0, "Date"));
            s.addCell(new Label(8, 0, "Share Amount"));
            for (int i = 0; i < members.size(); i++) {
                int j = 0;
                s.addCell(new Label(j, i + 1, "" + members.get(i).getId()));
                s.addCell(new Label(j + 1, i + 1, "" + members.get(i).getMemberNumber()));
                s.addCell(new Label(j + 2, i + 1, "" + members.get(i).getCustomer().getId()));
                s.addCell(new Label(j + 3, i + 1, "" + members.get(i).getName()));
               /* s.addCell(new Label(j + 4, i + 1, "" + members.get(i).getMemberFamilyDetails().get(0).getName()));
                s.addCell(new Label(j + 5, i + 1, "" + members.get(i).getMemberPersonalDetail().getVillage()));*/
                s.addCell(new Label(j + 6, i + 1, "" + members.get(i).getApplicationNumber()));
                String getDateWithFormat = DateUtil.convertToDateString(members.get(i).getApplicationDate());
                s.addCell(new Label(j + 7, i + 1, "" + getDateWithFormat));
                s.addCell(new Label(j + 8, i + 1, "" + members.get(i).getSharesValue()));
                j++;
            }
            w.write();
            w.close();
        } catch (Exception e) {
            throw new ServletException("Exception in Excel download", e);
        } finally {
            if (out != null)
                out.close();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "report/member/export-to-pdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    ResponseEntity<InputStreamResource> loanPDFReport(@RequestBody DepositReportDto depositReportDto) throws IOException, DocumentException, ParseException {
        List<Member> members = memberReportService.getMembersReportByDate(depositReportDto.inputDate);
        ByteArrayInputStream bis = GeneratePdfReport.memberReport(members);//IncomePdfReport.incomePdfReport(incomes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=income.pdf");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 50, 25);
        PdfWriter writer = PdfWriter.getInstance(document, bos);
        GeneratePdfReport event = new GeneratePdfReport();
        writer.setPageEvent(event);
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bis));
    }

    /**
     * REPORTS Module Application Details 1.2.1
     *
     * @param memberReportDto
     * @param request
     * @param response
     * @return
     */

    @PostMapping("report/member/application-details")
    public ResponseEntity<InputStreamResource> getMemberApplicationDetailsByDate(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            /*List<Member> memberApplicationDetails = memberReportService.getMemberApplicationDetailsByDate(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.numFrom, memberReportDto.numTo,
                    memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.inputDate);*/
            List<Member> memberApplicationDetails = memberReportService.getMemberApplicationDetailsByDate(memberReportDto.applicationFrom,memberReportDto.applicationTo);
            if (CollectionUtils.isEmpty(memberApplicationDetails)) {
                return new ResponseEntity(memberApplicationDetails, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(memberApplicationDetails, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/caste-details")
    public ResponseEntity<InputStreamResource> getMemberApplicationCaseteDetailsByDate(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            List<Member> memberCasteDetailsByDate = memberReportService.getMemberCasteDetailsByDate(memberReportDto.inputDate);
            /*List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i <memberCasteDetailsByDate.size() ; i++) {
                map.put(memberCasteDetailsByDate.get(i).getCasteCode(), memberCasteDetailsByDate.get(i).getName());
                list.add(map);
            }*/
            System.out.println(memberCasteDetailsByDate.get(0).toString());
            if (CollectionUtils.isEmpty(memberCasteDetailsByDate)) {
                return new ResponseEntity(memberCasteDetailsByDate, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(memberCasteDetailsByDate, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/share-details")
    public ResponseEntity<InputStreamResource> getMemberShareDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            System.out.println(memberReportDto.dateFrom + "  " + memberReportDto.dateTo);

            List<AdditionalShare> additionalShares = memberService.getAdditionalSharesByDate(memberReportDto.getDateFrom(), memberReportDto.getDateTo());
//            List<Member> memberApplicationDetails = memberReportService.getMemberApplicationDetailsByDate(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.numFrom, memberReportDto.numTo,
//                    memberReportDto.applicationFrom, memberReportDto.applicationTo, memberReportDto.inputDate);
            if (CollectionUtils.isEmpty(additionalShares)) {
                return new ResponseEntity(additionalShares, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(additionalShares, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/share-refund-details")
    public ResponseEntity<InputStreamResource> getMemberShareRefundDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            List<RefundShare> memberApplicationDetails = memberReportService.getMemberShareRefundDetails(memberReportDto.memberNumberFrom, memberReportDto.memberNumberTo);
            if (CollectionUtils.isEmpty(memberApplicationDetails)) {
                return new ResponseEntity(memberApplicationDetails, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(memberApplicationDetails, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/dividend-register")
    public ResponseEntity<InputStreamResource> getDividendRegisterDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {

        List<DividendIssue> dividendIssues = null;
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            dividendIssues = memberReportService.getDividendIssueByDividendYear(memberReportDto.year);
            if (CollectionUtils.isEmpty(dividendIssues)) {
                return new ResponseEntity(dividendIssues, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(dividendIssues, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/loanee-members")
    public ResponseEntity<InputStreamResource> getLoaneeMembersDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<Member> memberList = memberService.members();
        List<Loan> goldLoanList = loanService.appliedGoldLoans();
       /* if (memberReportDto.reportType.equalsIgnoreCase("list")){
            List<Member> memberApplicationDetails= memberReportService.getLoaneeMember();
            if (CollectionUtils.isEmpty(memberApplicationDetails)) {
                return new ResponseEntity(memberApplicationDetails, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(memberApplicationDetails, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.memberApplicationDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberApplicationDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return null;
    }

    @PostMapping("report/member/member-details")
    public ResponseEntity<InputStreamResource> getMembersDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            List<Member> memberList = memberReportService.getMemberDetailsByMemberNumber(memberReportDto.memberNumberFrom, memberReportDto.memberNumberTo, MemberType.MEMBER);
            if (CollectionUtils.isEmpty(memberList)) {
                return new ResponseEntity(memberList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(memberList, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getMemberDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getMemberDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/member-classification")
    public ResponseEntity<InputStreamResource> getMemberClassificationDetails(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) throws ParseException, DocumentException, IOException {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            List<Member> memberList = memberReportService.getMemberByOccupation(memberReportDto.occupationCode, memberReportDto.memberNumberFrom, memberReportDto.memberNumberTo);
            if (CollectionUtils.isEmpty(memberList)) {
                return new ResponseEntity(memberList, HttpStatus.OK);
            }
            return new ResponseEntity(memberList, HttpStatus.OK);
        }
        return null;
    }

    @PostMapping("report/member/nominal-member-details")
    public ResponseEntity<InputStreamResource> getNominalMemberDetailsByDate(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {

        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            List<Member> nominalMemberList = memberReportService.getNominalMemberList(memberReportDto.nominalMemberNumberFrom, memberReportDto.nominalMemberNumberTo);
            if (CollectionUtils.isEmpty(nominalMemberList)) {
                return new ResponseEntity(nominalMemberList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(nominalMemberList, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.nominalMemberDetailsPDF(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getNominalMemberDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/customer-details")
    public ResponseEntity<InputStreamResource> getCustomerDetailsByDate(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (memberReportDto.reportType.equalsIgnoreCase("list")) {
            System.out.println(memberReportDto.dateFrom + "  " + memberReportDto.dateTo);
            List<Customer> customerList = memberReportService.getCustomerDetailsListByDate(memberReportDto.dateFrom, memberReportDto.dateTo, memberReportDto.customerIdFrom, memberReportDto.customerIdTo);
            if (CollectionUtils.isEmpty(customerList)) {
                return new ResponseEntity(customerList, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(customerList, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("pdf")) {
            try {
                return pdfExcelFunction.getCustomerDetailsPdf(memberReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (memberReportDto.reportType.equalsIgnoreCase("excel")) {
            try {
                return pdfExcelFunction.getCustomerDetailsExcel(memberReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @PostMapping("report/member/loanee-unloanee")
    public ResponseEntity<InputStreamResource> getLoaneeAndUnLoaneeMembers(@RequestBody MemberReportDto memberReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<Member> unLoaneeMembers = memberService.getApprovedMembers();
        List<Member> loanMembers = new ArrayList<>();
            List<Loan> allLoans = loanService.appliedGoldLoans();
            if (CollectionUtils.isEmpty(allLoans)) {
                throw new EntityNotFoundException("Loans are not found");
            }
            if (allLoans != null && allLoans.size() > 0) {
                for (Loan loan : allLoans) {
                    if (loan.getLoanDetail().getLadDetails() != null) {
                        Member ladMember = loan.getLoanDetail().getLadDetails().getFixedDeposit().getMember();
                        if (ladMember != null) {
                            if (!loanMembers.contains(ladMember)) {
                                loanMembers.add(ladMember);
                            }
                        }
                    }
                    Customer customer = loan.getLoanDetail().getCustomer();
                    Member goldLoanMember = memberService.getMemberByCustomer(customer);
                    if (goldLoanMember != null) {
                        if (!loanMembers.contains(goldLoanMember)) {
                            loanMembers.add(goldLoanMember);
                        }
                    }
                    if (loan.getLoanDetail().getTermDetails() != null) {
                        Member termLoanMember = loan.getLoanDetail().getTermDetails().getMember();
                        if (termLoanMember != null) {
                            if (!loanMembers.contains(termLoanMember)) {
                                loanMembers.add(termLoanMember);
                            }
                        }
                    }
                }
            }

        if (memberReportDto.reportType.equalsIgnoreCase("Loanee")) {
            return new ResponseEntity(loanMembers, HttpStatus.OK);
        } else if (memberReportDto.reportType.equalsIgnoreCase("Unloanee")) {
            unLoaneeMembers.removeAll(loanMembers);
            return new ResponseEntity(unLoaneeMembers, HttpStatus.OK);
        }
        return null;
    }
}