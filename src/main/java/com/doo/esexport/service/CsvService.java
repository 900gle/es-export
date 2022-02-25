package com.doo.esexport.service;//package com.curi.log.service;

import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CsvService {

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

        storeList.stream().forEach( x->getTestData(x) );
        return "ok";
    }

    public void getTestData(Store store) {

        int size = 10;

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

            CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream("./"+store.getStoreName()+".csv"), "EUC-KR"));

            SearchHit[] results = searchResponse.getHits().getHits();
            Arrays.stream(results).forEach(hit -> {
                Map<String, Object> result = hit.getSourceAsMap();

                Map<String, Object> innerCategory =
                        (Map<String, Object>) result.get("category");

                String listRows = store.getStoreId()
                        + store.getStoreName()
                        + result.get("itemNo")
                        + result.get("itemNm").toString()
                        + "판매중"
                        + innerCategory.get("lcateNm") + "(" + innerCategory.get("lcateCd") + ")"
                        + innerCategory.get("mcateNm") + "(" + innerCategory.get("mcateCd") + ")"
                        + innerCategory.get("scateNm") + "(" + innerCategory.get("scateCd") + ")"
                        + innerCategory.get("dcateNm") + "(" + innerCategory.get("dcateCd") + ")";

                cw.writeNext(new String[]{
                        store.getStoreId()
                        , store.getStoreName()
                        , result.get("itemNo").toString()
                        , result.get("itemNm").toString()
                        , "판매중"
                        , innerCategory.get("lcateNm") + "(" + innerCategory.get("lcateCd") + ")" + " > "
                        + innerCategory.get("mcateNm") + "(" + innerCategory.get("mcateCd") + ")" + " > "
                        + innerCategory.get("scateNm") + "(" + innerCategory.get("scateCd") + ")" + " > "
                        + innerCategory.get("dcateNm") + "(" + innerCategory.get("dcateCd") + ")"
                });

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

                    String listRows = store.getStoreId()
                            + store.getStoreName()
                            + result.get("itemNo")
                            + result.get("itemNm").toString()
                            + "판매중"
                            + innerCategory.get("lcateNm") + "(" + innerCategory.get("lcateCd") + ")"
                            + innerCategory.get("mcateNm") + "(" + innerCategory.get("mcateCd") + ")"
                            + innerCategory.get("scateNm") + "(" + innerCategory.get("scateCd") + ")"
                            + innerCategory.get("dcateNm") + "(" + innerCategory.get("dcateCd") + ")";

                    cw.writeNext(new String[]{
                            store.getStoreId()
                            , store.getStoreName()
                            , result.get("itemNo").toString()
                            , result.get("itemNm").toString()
                            , "판매중"
                            , innerCategory.get("lcateNm") + "(" + innerCategory.get("lcateCd") + ")" + " > "
                            + innerCategory.get("mcateNm") + "(" + innerCategory.get("mcateCd") + ")" + " > "
                            + innerCategory.get("scateNm") + "(" + innerCategory.get("scateCd") + ")" + " > "
                            + innerCategory.get("dcateNm") + "(" + innerCategory.get("dcateCd") + ")"
                    });
                });
            }

            cw.close();

        } catch (IOException e) {
            e.getStackTrace();
        }


    }


}
