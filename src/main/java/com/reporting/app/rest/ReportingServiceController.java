package com.reporting.app.rest;


import com.reporting.app.model.Result;
import com.reporting.app.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/rest/report")
public class ReportingServiceController {

    @Autowired
    ReportingService reportingService;

    @RequestMapping(
            value = "/create-customer-report",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> createCustomerReport()throws Exception {
        return reportingService.createRapor();
    }

    @RequestMapping(
            value = "/ftp-control",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> ftpControl()throws Exception {
        return reportingService.ftpControl();
    }
}
