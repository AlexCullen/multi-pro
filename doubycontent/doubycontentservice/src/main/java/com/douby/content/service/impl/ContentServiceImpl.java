package com.douby.content.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.douby.common.JedisUtil;
import com.douby.content.dto.TbContentDto;
import com.douby.content.service.ContentService;
import com.douby.manager.pojo.TbContent;
import com.douby.manager.pojo.TbContentExample;
import com.douby.mapper.TbContentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
 * @Date: 2018/6/8 17:09
 * TODO:
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper tbContentMapper;

    @Autowired
    private JedisUtil jedisUtil;

    @Value("${index_key1}")
    private String key1;

    @Override
    public List<TbContentDto> getContentList(long cid) {
        List<TbContentDto> tbContentDtoList = new ArrayList<>();
        try {
            System.out.println(jedisUtil.get(key1));
            if (jedisUtil.exists(key1)) {
                return JSONArray.parseArray(jedisUtil.get(key1), TbContentDto.class);
            }
            TbContentExample tbContentExample = new TbContentExample();
            TbContentExample.Criteria criteria = tbContentExample.createCriteria();
            criteria.andCategoryIdEqualTo(cid);
            List<TbContent> tbContents = tbContentMapper.selectByExampleWithBLOBs(tbContentExample);
            for (TbContent tbContent : tbContents) {
                TbContentDto tbContentDto = new TbContentDto(tbContent);
                tbContentDtoList.add(tbContentDto);
            }
            jedisUtil.set(key1, JSONArray.toJSONString(tbContentDtoList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tbContentDtoList;
    }
}
