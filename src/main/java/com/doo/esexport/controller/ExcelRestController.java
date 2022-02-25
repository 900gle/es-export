package com.doo.esexport.controller;


import com.doo.esexport.model.response.RestApiResponse;
import com.doo.esexport.service.ExcelService;
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
public class ExcelRestController {

    private final ExcelService excelService;

    @ApiOperation(value = "Elasticsearch to Excel", notes = "ES 데이터 Excel 파일 추출")
    @CrossOrigin("*")
    @PostMapping("/excel")
    public RestApiResponse getExcelData() {
        return new RestApiResponse<>(excelService.postLog());
    }
}
