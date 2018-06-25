package com.douby.item.message;

import com.douby.common.EasyUiDataGridResult;
import com.douby.manager.dto.TbItemDescDto;
import com.douby.manager.dto.TbItemDto;
import com.douby.manager.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.*;
import java.util.HashMap;
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
 * @Date: 2018/6/24 15:46
 * TODO:
 */
public class AddTempMsgListener implements MessageListener {
    @Autowired
    private ItemService itemService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${temp_url}")
    private String tempUrl;

    @Value("${ftl_url}")
    private String ftlUrl;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println(textMessage.getText());
            String msgText = textMessage.getText();
            if ("".equals(msgText)) {
                return;
            }
            String[] msgs = msgText.split(",");
            if (msgs.length == 0) {
                return;
            }
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            configuration.setDefaultEncoding("UTF-8");
            configuration.setDirectoryForTemplateLoading(new File(ftlUrl));
            Template template = configuration.getTemplate("item.ftl");
            EasyUiDataGridResult easyUiDataGridResult = itemService.getItemList(1,934);
            List<TbItemDto> tbItemDtoList = easyUiDataGridResult.getRows();
            for(TbItemDto tbItemDto : tbItemDtoList){
                long itemId = tbItemDto.getId();
                TbItemDescDto itemDescDto = itemService.getItemDescById(itemId);
                Map map = new HashMap();
                map.put("item", tbItemDto);
                map.put("itemDesc", itemDescDto);
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tempUrl + "/" + itemId + ".html"), "UTF-8");
                template.process(map, osw);
                osw.flush();
                osw.close();
            }
//            for (String msg : msgs) {
//                long itemId = new Long(msg);
//                TbItemDto tbItemDto = itemService.getItemById(itemId);
//                TbItemDescDto itemDescDto = itemService.getItemDescById(itemId);
//                if (tbItemDto == null){
//                    continue;
//                }
//                Map map = new HashMap();
//                map.put("item", tbItemDto);
//                map.put("itemDesc", itemDescDto);
//
//                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(tempUrl+"/"+itemId+".html"), "UTF-8");
//                template.process(map, osw);
//                osw.flush();
//                osw.close();
//            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}
