package com.douby.solr.service.impl;

import com.douby.common.ResultHandle;
import com.douby.common.SolrUtil;
import com.douby.solr.dto.SearchItem;
import com.douby.solr.dto.SearchResult;
import com.douby.solr.service.SolrService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *                _ooOoo_
 *                o8888888o
 *                88" . "88
 *                (| -_- |)
 *                O\ = /O
 *              ____/`---'\____
 *            .   ' \\| |// `.
 *              / \\||| : |||// \
 *             / _||||| -:- |||||- \
 *              | | \\\ - /// | |
 *              | \_| ''\---/'' | |
 *            \ .-\__ `-` ___/-. /
 *            ___`. .' /--.--\ `. . __
 *          ."" '< `.___\_<|>_/___.' >'"".
 *         | | : `- \`.;`\ _ /`;.`/ - ` : | |
 *          \ \ `-. \_ __\ /__ _/ .-` / /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                `=---='
 * .............................................
 *      佛祖镇楼                  BUG辟易
 * 佛曰:
 *       写字楼里写字间，写字间里程序员；
 *       程序人员写程序，又拿程序换酒钱。
 *       酒醒只在网上坐，酒醉还来网下眠；
 *       酒醉酒醒日复日，网上网下年复年。
 *       但愿老死电脑间，不愿鞠躬老板前；
 *       奔驰宝马贵者趣，公交自行程序员。
 *       别人笑我忒疯癫，我笑自己命太贱；
 *       不见满街漂亮妹，哪个归得程序员？
 *
 * @Author: cpzh
 * @Date: 2018/6/14 14:39
 * TODO:
 */
@Service
public class SolrServiceImpl implements SolrService {
    @Value("${solr.url}")
    private String solrUrl;

    @Override
    public SearchResult search(String keyword, int page, int rows) {
        SearchResult searchResult = new SearchResult();
        try {
            SolrUtil.initSolrClient(solrUrl);
            ResultHandle<SearchResult> resultHandle = new ResultHandle<SearchResult>() {
                @Override
                public SearchResult handle(SolrDocumentList solrDocuments,
                                           Map<String, Map<String, List<String>>> highlighting) {
                    List<SearchItem> itemSolrList = new ArrayList<>();
                    for (SolrDocument solrDocument : solrDocuments) {

                        String id = solrDocument.get("id") == null ?
                                "" : solrDocument.get("id").toString();
                        String itemTitle = solrDocument.get("itemTitle") == null ?
                                "" : solrDocument.get("itemTitle").toString();
                        String itemSellPoint = solrDocument.get("itemSellPoint") == null ?
                                "" : solrDocument.get("itemSellPoint").toString();
                        long itemPrice = solrDocument.get("itemPrice") == null ?
                                0L : (Long) solrDocument.get("itemPrice");
                        String itemImage = solrDocument.get("itemImage") == null ?
                                "" : solrDocument.get("itemImage").toString();
                        String itemCategoryName = solrDocument.get("itemCategoryName") == null ?
                                "" : solrDocument.get("itemCategoryName").toString();

                        if (highlighting != null){
                            highlighting.get("itemTitle");
                            List<String> list = highlighting.get(solrDocument.get("id")).get("itemTitle");
                            if (list != null && list.size() > 0) {
                                if (list.get(0) != null && !"".equals(list.get(0))) {
                                    itemTitle = list.get(0);
                                }
                            }
                        }

                        SearchItem itemDto = new SearchItem(id, itemTitle, itemSellPoint,
                                itemPrice, itemImage, itemCategoryName);
                        itemSolrList.add(itemDto);
                    }
                    long numFound = solrDocuments.getNumFound();
                    SearchResult searchResult = new SearchResult(numFound, itemSolrList);
                    return searchResult;
                }
            };

            SolrQuery solrQuery = SolrUtil.transSolrQuery("itemKeywords:" + keyword);
            if (page < 1) {
                page = 1;
            }
            solrQuery.setStart((page - 1) * rows);
            solrQuery.setRows(rows);
            SolrQuery solrQueryHl = SolrUtil.addHighLight("<em style=\"color:red\">", "</em>", "itemTitle");
            solrQuery.add(solrQueryHl);
            searchResult = SolrUtil.query(resultHandle, solrQuery, true);
            SolrUtil.close();
            long recordCount = searchResult.getRecordCount();
            int totalPage = (int) (recordCount % rows == 0 ? recordCount/rows : recordCount/rows +1);
            searchResult.setTotalPages(totalPage);
            return searchResult;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return searchResult;
    }
}
