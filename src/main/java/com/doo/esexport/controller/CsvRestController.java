package com.doo.esexport.controller;

import com.doo.esexport.model.response.RestApiResponse;
import com.doo.esexport.service.CsvService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"1. homeplus statistics"})
@RestController
@RequestMapping("api/output")
@RequiredArgsConstructor
public class CsvRestController {

    private final CsvService csvService;

    @ApiOperation(value = "Elasticsearch to CSV", notes = "ES 데이터 csv 파일 추출")
    @CrossOrigin("*")
    @PostMapping("/csv")
    public RestApiResponse getCsvData() {
        return new RestApiResponse<>(csvService.postLog());
    }
}
