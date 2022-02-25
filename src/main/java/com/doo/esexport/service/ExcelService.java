package com.doo.esexport.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final RestHighLevelClient client;
    String scrollId = "";

    @Getter
    @Setter
    @AllArgsConstructor
    class Store {
        private String storeId;
        private String storeName;
    }

    public String postLog() {

        List<Store> storeList = new ArrayList<>();
        storeList.add(new Store("14", "북수원점"));
        storeList.add(new Store("15", "영통점"));
        storeList.add(new Store("32", "부천상동점"));
        storeList.add(new Store("75", "잠실점"));
        storeList.add(new Store("121", "서울남현점"));
        storeList.add(new Store("163", "계산점"));
        storeList.add(new Store("167", "안양점"));
        storeList.add(new Store("178", "원천점"));

        storeList.stream().forEach(x -> excel(x));
        return "ok";
    }


    public void excel(Store store) {
        //.xls 확장자 지원
        HSSFWorkbook wb = null;
        HSSFSheet sheet = null;
        Row row = null;
        Cell cell = null;

        //.xlsx 확장자 지원
        XSSFWorkbook xssfWb = null; // .xlsx
        XSSFSheet xssfSheet = null; // .xlsx
        XSSFRow xssfRow = null; // .xlsx
        XSSFCell xssfCell = null;// .xlsx

        try {

            int rowNo = 0; // 행 갯수
            // 워크북 생성
            xssfWb = new XSSFWorkbook();
            xssfSheet = xssfWb.createSheet("엑셀 테스트"); // 워크시트 이름

            //헤더용 폰트 스타일
            XSSFFont font = xssfWb.createFont();
            font.setFontName(HSSFFont.FONT_ARIAL); //폰트스타일
            font.setFontHeightInPoints((short) 14); //폰트크기
            font.setBold(true); //Bold 유무

            //테이블 타이틀 스타일
            CellStyle cellStyle_Title = xssfWb.createCellStyle();

            xssfSheet.setColumnWidth(3, (xssfSheet.getColumnWidth(3)) + (short) 2048); // 3번째 컬럼 넓이 조절
            xssfSheet.setColumnWidth(4, (xssfSheet.getColumnWidth(4)) + (short) 2048); // 4번째 컬럼 넓이 조절
            xssfSheet.setColumnWidth(5, (xssfSheet.getColumnWidth(5)) + (short) 2048); // 5번째 컬럼 넓이 조절
            xssfSheet.setColumnWidth(8, (xssfSheet.getColumnWidth(8)) + (short) 4096); // 8번째 컬럼 넓이 조절

            cellStyle_Title.setFont(font); // cellStle에 font를 적용
            cellStyle_Title.setAlignment(HorizontalAlignment.CENTER); // 정렬

//            //셀병합
//            xssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8)); //첫행, 마지막행, 첫열, 마지막열( 0번째 행의 0~8번째 컬럼을 병합한다)
//            //타이틀 생성
//            xssfRow = xssfSheet.createRow(rowNo++); //행 객체 추가
//            xssfCell = xssfRow.createCell((short) 0); // 추가한 행에 셀 객체 추가
//            xssfCell.setCellStyle(cellStyle_Title); // 셀에 스타일 지정
//            xssfCell.setCellValue("타이틀 입니다."); // 데이터 입력

//            xssfRow = xssfSheet.createRow(rowNo++);  // 빈행 추가

            CellStyle cellStyle_Body = xssfWb.createCellStyle();
            cellStyle_Body.setAlignment(HorizontalAlignment.LEFT);

//            //헤더 생성
//            xssfSheet.addMergedRegion(new CellRangeAddress(rowNo, rowNo, 0, 1)); //첫행,마지막행,첫열,마지막열
//            xssfRow = xssfSheet.createRow(rowNo++); //헤더 01
//            xssfCell = xssfRow.createCell((short) 0);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더01 셀01");
//            xssfCell = xssfRow.createCell((short) 8);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더01 셀08");
//            xssfRow = xssfSheet.createRow(rowNo++); //헤더 02
//            xssfCell = xssfRow.createCell((short) 0);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더02 셀01");
//            xssfCell = xssfRow.createCell((short) 8);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더02 셀08");
//            xssfRow = xssfSheet.createRow(rowNo++); //헤더 03
//            xssfCell = xssfRow.createCell((short) 0);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더03 셀01");
//            xssfCell = xssfRow.createCell((short) 8);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더03 셀08");
//            xssfRow = xssfSheet.createRow(rowNo++); //헤더 04
//            xssfCell = xssfRow.createCell((short) 0);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더04 셀01");
//            xssfCell = xssfRow.createCell((short) 8);
//            xssfCell.setCellStyle(cellStyle_Body);
//            xssfCell.setCellValue("헤더04 셀08");

            //테이블 스타일 설정
            CellStyle cellStyle_Table_Center = xssfWb.createCellStyle();
            cellStyle_Table_Center.setBorderTop(BorderStyle.THIN); //테두리 위쪽
            cellStyle_Table_Center.setBorderBottom(BorderStyle.THIN); //테두리 아래쪽
            cellStyle_Table_Center.setBorderLeft(BorderStyle.THIN); //테두리 왼쪽
            cellStyle_Table_Center.setBorderRight(BorderStyle.THIN); //테두리 오른쪽
            cellStyle_Table_Center.setAlignment(HorizontalAlignment.CENTER);

            List<List<String>> columnsList = getTestData(store);
            for (List<String> columns : columnsList) {
                xssfRow = xssfSheet.createRow(rowNo++);
                for (int i = 0; i < columns.size(); i++) {
                    xssfCell = xssfRow.createCell((short) i);
                    xssfCell.setCellStyle(cellStyle_Table_Center);
                    xssfCell.setCellValue(columns.get(i));
                }
            }

            String localFile = "./" + store.getStoreName() + ".xlsx";

            File file = new File(localFile);
            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            xssfWb.write(fos);

            if (xssfWb != null) xssfWb.close();
            if (fos != null) fos.close();

            //ctx.put("FILENAME", "입고상세출력_"+ mapList.get(0).get("PRINT_DATE"));
            //if(file != null) file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {

        }
    }


    public List<List<String>> getTestData(Store store) {

        List<List<String>> columnsList = new ArrayList<>();

        int size = 10;


        System.out.println("bbbbbbbbbbb");


        SearchRequest searchRequest = new SearchRequest("hyper-item");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("itemStatus", "A"))
                        .must(QueryBuilders.termQuery("itemStoreInfo.storeId", store.getStoreId()))
                        .must(QueryBuilders.termQuery("itemDispYn", "Y"))
        );

        searchSourceBuilder.size(size);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            SearchHits hits = searchResponse.getHits();

            SearchHit[] results = searchResponse.getHits().getHits();
            Arrays.stream(results).forEach(hit -> {
                Map<String, Object> result = hit.getSourceAsMap();
                Map<String, Object> innerCategory =
                        (Map<String, Object>) result.get("category");


                System.out.println("itemNo : " + result.get("itemNo").toString());


                List<String> rows = new ArrayList<>();
                rows.add(store.getStoreId());
                rows.add(store.getStoreName());
                rows.add(result.get("itemNo").toString());
                rows.add(result.get("itemNm").toString());
                rows.add("판매중");
                rows.add(innerCategory.get("lcateNm") + "(" + innerCategory.get("lcateCd") + ")" + " > "
                        + innerCategory.get("mcateNm") + "(" + innerCategory.get("mcateCd") + ")" + " > "
                        + innerCategory.get("scateNm") + "(" + innerCategory.get("scateCd") + ")" + " > "
                        + innerCategory.get("dcateNm") + "(" + innerCategory.get("dcateCd") + ")");
                columnsList.add(rows);
            });

            System.out.println("scrollId 1: " + scrollId);

            while (!scrollId.isEmpty()) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(TimeValue.timeValueSeconds(30));

                SearchResponse searchScrollResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                if (searchScrollResponse.getHits().getHits().length == 0) {
                    System.out.println("getHits : " + searchScrollResponse.getHits().getHits().length);
                    break;
                }
                scrollId = searchScrollResponse.getScrollId();

                SearchHit[] scresults = searchScrollResponse.getHits().getHits();
                Arrays.stream(scresults).forEach(hit -> {
                    Map<String, Object> result = hit.getSourceAsMap();

                    Map<String, Object> innerCategory =
                            (Map<String, Object>) result.get("category");

                    List<String> rows = new ArrayList<>();
                    rows.add(store.getStoreId());
                    rows.add(store.getStoreName());
                    rows.add(result.get("itemNo").toString());
                    rows.add(result.get("itemNm").toString());
                    rows.add("판매중");
                    rows.add(innerCategory.get("lcateNm") + "(" + innerCategory.get("lcateCd") + ")" + " > "
                            + innerCategory.get("mcateNm") + "(" + innerCategory.get("mcateCd") + ")" + " > "
                            + innerCategory.get("scateNm") + "(" + innerCategory.get("scateCd") + ")" + " > "
                            + innerCategory.get("dcateNm") + "(" + innerCategory.get("dcateCd") + ")");
                    columnsList.add(rows);
                });
            }

        } catch (IOException e) {
            e.getStackTrace();
        }

        return columnsList;
    }
}
