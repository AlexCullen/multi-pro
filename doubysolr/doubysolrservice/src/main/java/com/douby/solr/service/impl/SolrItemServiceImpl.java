package com.douby.solr.service.impl;

import com.douby.common.E3Result;
import com.douby.common.SaveHandle;
import com.douby.common.SolrUtil;
import com.douby.solr.mapper.ItemSolrMapper;
import com.douby.solr.pojo.ItemSolr;
import com.douby.solr.service.SolrItemService;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * @Date: 2018/6/14 14:56
 * TODO:
 */
@Service
public class SolrItemServiceImpl implements SolrItemService {

    @Value("${solr.url}")
    private String solrUrl;

    @Autowired
    private ItemSolrMapper itemSolrMapper;

    @Override
    public E3Result importAllItems() {
        List<ItemSolr> itemSolrList = itemSolrMapper.getItemList();
       E3Result e3Result = new E3Result();
        try {
            SaveHandle<List<ItemSolr>> saveHandle = new SaveHandle<List<ItemSolr>>() {
                @Override
                public List<SolrInputDocument> handle(List<ItemSolr> itemSolrs) {
                    List<SolrInputDocument> result = new ArrayList<>();
                    for(ItemSolr itemSolr : itemSolrs){
                        SolrInputDocument solrInputFields = new SolrInputDocument();
                        solrInputFields.addField("id", itemSolr.getId());
                        solrInputFields.addField("itemTitle", itemSolr.getTitle());
                        solrInputFields.addField("itemSellPoint", itemSolr.getSellPoint());
                        solrInputFields.addField("itemPrice", itemSolr.getPrice());
                        solrInputFields.addField("itemCategoryName", itemSolr.getCategoryName());
                        solrInputFields.addField("itemImage", itemSolr.getImage());
                        result.add(solrInputFields);
                    }
                    return result;
                }
            };
            SolrUtil.initSolrClient(solrUrl, "product");
            SolrUtil.addDocument(itemSolrList, saveHandle);
            e3Result = E3Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            e3Result = E3Result.build(500, "数据导入异常");
            SolrUtil.rollBack();
        } catch (SolrServerException e) {
            e.printStackTrace();
            e3Result = E3Result.build(500, "solr服务异常");
            SolrUtil.rollBack();
        }finally {
            SolrUtil.close();
        }
        return e3Result;
    }
}
