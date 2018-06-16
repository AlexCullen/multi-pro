package com.douby.manager.service.impl;

import com.douby.common.EasyUiTreeNode;
import com.douby.manager.pojo.TbItemCat;
import com.douby.manager.pojo.TbItemCatExample;
import com.douby.manager.service.ItemCastService;
import com.douby.mapper.TbItemCatMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @Date: 2018/6/5 9:47
 * TODO:
 */
@Service
public class ItemCastServiceImpl implements ItemCastService {

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Override
    public List<EasyUiTreeNode> getItemCatlist(int parentId) {
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andParentIdEqualTo((long) parentId);
        List<TbItemCat> tbItemCatList = tbItemCatMapper.selectByExample(tbItemCatExample);
        List<EasyUiTreeNode> easyUiTreeNodeList = new ArrayList<>();
        for (TbItemCat tbItemCat : tbItemCatList) {
            EasyUiTreeNode easyUiTreeNode = new EasyUiTreeNode(tbItemCat.getId().intValue(),
                    tbItemCat.getName(), tbItemCat.getIsParent() ? "closed" : "open");
            easyUiTreeNodeList.add(easyUiTreeNode);
        }
        return easyUiTreeNodeList;
    }
}
