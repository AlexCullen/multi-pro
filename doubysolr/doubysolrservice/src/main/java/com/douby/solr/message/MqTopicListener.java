package com.douby.solr.message;

import com.douby.common.SaveHandle;
import com.douby.common.SolrUtil;
import com.douby.solr.mapper.ItemSolrMapper;
import com.douby.solr.pojo.ItemSolr;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
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
 * @Date: 2018/6/19 17:05
 * TODO:
 */
public class MqTopicListener implements MessageListener {
    @Autowired
    private ItemSolrMapper itemSolrMapper;

    @Value("${solr.url}")
    private String solrUrl;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String msgText = textMessage.getText();
            if ("".equals(msgText)) {
                return;
            }
            String[] msgs = msgText.split(",");
            if (msgs.length == 0) {
                return;
            }
            SaveHandle<ItemSolr> saveHandle = new SaveHandle<ItemSolr>() {
                @Override
                public List<SolrInputDocument> handle(ItemSolr itemSolr) {
                    List<SolrInputDocument> solrInputDocuments = new ArrayList<>();
                    SolrInputDocument solrInputFields = new SolrInputDocument();
                    solrInputFields.addField("id", itemSolr.getId());
                    solrInputFields.addField("itemTitle", itemSolr.getTitle());
                    solrInputFields.addField("itemSellPoint", itemSolr.getSellPoint());
                    solrInputFields.addField("itemPrice", itemSolr.getPrice());
                    solrInputFields.addField("itemCategoryName", itemSolr.getCategoryName());
                    solrInputFields.addField("itemImage", itemSolr.getImage());
                    solrInputDocuments.add(solrInputFields);
                    return solrInputDocuments;
                }
            };
            for (String msg : msgs) {
                long itemId = new Long(msg);
                ItemSolr itemSolr = itemSolrMapper.getItemById(itemId);
                SolrUtil.initSolrClient(solrUrl, "product");
                if (itemSolr == null) {
                    SolrUtil.deleteDocumentById(itemId + "");
                } else {
                    SolrUtil.addDocument(itemSolr, saveHandle);
                }
                SolrUtil.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
