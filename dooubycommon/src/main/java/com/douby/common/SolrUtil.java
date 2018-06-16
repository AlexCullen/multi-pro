package com.douby.common;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
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
 * @Date: 2018/6/13 11:51
 * TODO:
 */
public class SolrUtil {

    private static SolrClient solrClient = null;

    public static void initSolrClient(String url) {
        solrClient = new HttpSolrClient.Builder(url).build();
    }

    public static SolrQuery transSolrQuery(String... param) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setParam("q", param);
        return solrQuery;
    }

    public static SolrQuery transSolrQuery(Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            sb.append(entry.getKey() + ":" + entry.getValue());
            sb.append(" AND ");
        }
        int lastIndex = sb.lastIndexOf("AND");
        String paramStr = sb.substring(0, lastIndex).trim();
        return transSolrQuery(paramStr);
    }

    public static SolrQuery addHighLight(String hlPre, String hlPost, String... fields) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setHighlight(true);
        String fieldStr = "";
        for (String field : fields) {
            fieldStr += (field + ",");
        }
        fieldStr = fieldStr.substring(0, fieldStr.length()-1);
        solrQuery.addHighlightField(fieldStr);
        solrQuery.setHighlightSimplePre(hlPre);
        solrQuery.setHighlightSimplePost(hlPost);
        //每个分片的最大长度，默认为100
//        solrQuery.setHighlightFragsize(1);
        return solrQuery;
    }

    public static <T> T query(ResultHandle<T> resultHandle, SolrQuery solrQuery) throws IOException, SolrServerException {
        return query(resultHandle, solrQuery, false);
    }

    public static <T> T query(ResultHandle<T> resultHandle, SolrQuery solrQuery , boolean flag) throws IOException, SolrServerException {
        if (solrClient == null) {
            throw new NullPointerException("SolrClient is Null");
        }
        QueryResponse queryResponse = solrClient.query(solrQuery);
        SolrDocumentList solrDocuments = queryResponse.getResults();
        if (flag){
            return resultHandle.handle(solrDocuments, queryResponse.getHighlighting());
        }else{
            return resultHandle.handle(solrDocuments, null);
        }
    }

    public static <T> void addDocument(T t, SaveHandle<T> saveHandle) throws IOException, SolrServerException {
        if (solrClient == null) {
            throw new NullPointerException("SolrClient is Null");
        }
        List<SolrInputDocument> solrInputDocument = saveHandle.handle(t);
        solrClient.add(solrInputDocument);
        solrClient.commit();
    }

    public static void deleteDocumentById(String id) throws IOException, SolrServerException {
        if (solrClient == null) {
            throw new NullPointerException("SolrClient is Null");
        }
        solrClient.deleteById(id);
        solrClient.commit();
    }

    public static void deleteDocumentById(List ids) throws IOException, SolrServerException {
        if (solrClient == null) {
            throw new NullPointerException("SolrClient is Null");
        }
        solrClient.deleteById(ids);
        solrClient.commit();
    }

    public static void deleteDocument(String solrQuery) throws IOException, SolrServerException {
        if (solrClient == null) {
            throw new NullPointerException("SolrClient is Null");
        }
        solrClient.deleteByQuery(solrQuery);
        solrClient.commit();
    }

    public static void deleteAll() throws IOException, SolrServerException {
        deleteDocument("*:*");
    }

    public static void close() {
        if (solrClient != null) {
            try {
                solrClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                solrClient = null;
            }
        }
    }

}
