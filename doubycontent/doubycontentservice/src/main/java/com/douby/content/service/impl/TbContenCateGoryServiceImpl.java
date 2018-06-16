package com.douby.content.service.impl;

import com.douby.common.E3Result;
import com.douby.common.EasyUiTreeNode;
import com.douby.common.JedisUtil;
import com.douby.content.service.ContentCategoryService;
import com.douby.manager.pojo.TbContentCategory;
import com.douby.manager.pojo.TbContentCategoryExample;
import com.douby.mapper.TbContentCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
 * @Date: 2018/6/8 10:05
 * TODO:
 */
@Service
public class TbContenCateGoryServiceImpl implements ContentCategoryService {


    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    @Autowired
    private JedisUtil jedisUtil;

    @Value("${index_key1}")
    private String key1;

    @Override
    public E3Result getContentCateList(long parentId) {
        E3Result e3Result = E3Result.ok();
        try {
            TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
            TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
            criteria.andParentIdEqualTo(parentId);
            List<TbContentCategory> tbContentCategories =
                    tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
            List<EasyUiTreeNode> easyUiTreeNodeList = new ArrayList<>();
            for (TbContentCategory tbContentCategory : tbContentCategories) {
                EasyUiTreeNode easyUiTreeNode = new EasyUiTreeNode(tbContentCategory.getId().intValue(),
                        tbContentCategory.getName(),
                        tbContentCategory.getIsParent() ? "closed" : "open");
                easyUiTreeNodeList.add(easyUiTreeNode);
            }
            e3Result = E3Result.ok(easyUiTreeNodeList);
        } catch (Exception e) {
            e3Result = E3Result.build(-1, e.getMessage());
        }
        return e3Result;
    }

    @Override
    public E3Result addTreeNode(long parentId, String name) {
        E3Result e3Result = E3Result.ok();
        try {
            TbContentCategory tbContentCategory = new TbContentCategory(parentId, name);
            tbContentCategoryMapper.insert(tbContentCategory);
            TbContentCategory parent = fingItemByParentId(tbContentCategory.getParentId());
            if (null != parent && !parent.getIsParent()){
                TbContentCategory updateParent = new TbContentCategory();
                updateParent.setId(parent.getId());
                updateParent.setIsParent(true);
                updateParent.setUpdated(new Date());
                tbContentCategoryMapper.updateByPrimaryKeySelective(updateParent);
            }
            e3Result = E3Result.ok(tbContentCategory);
            if (jedisUtil.exists(key1)){
                jedisUtil.del(key1);
            }
        }catch (Exception e){
            e3Result = E3Result.build(-1, e.getMessage());
        }
        return e3Result;
    }

    @Override
    public E3Result updateTreeNode(long id, String name) {
        E3Result e3Result = E3Result.ok();
        try {
            TbContentCategory tbContentCategory = new TbContentCategory();
            tbContentCategory.setId(id);
            tbContentCategory.setName(name);
            tbContentCategory.setUpdated(new Date());
            tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
            e3Result = E3Result.ok(tbContentCategory);
            if (jedisUtil.exists(key1)){
                jedisUtil.del(key1);
            }
        }catch (Exception e){
            e3Result = E3Result.build(-1, e.getMessage());
        }
        return e3Result;
    }

    @Override
    public void deleteTreeNode(long id) {
        try {
            TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
            TbContentCategoryExample.Criteria criteria = tbContentCategoryExample.createCriteria();
            criteria.andParentIdEqualTo(id);
            List<TbContentCategory> tbContentCategories =
                    tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
            if (tbContentCategories.size() > 0){
                return;
            }

            TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(id);

            TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());

            TbContentCategoryExample pExample = new TbContentCategoryExample();
            TbContentCategoryExample.Criteria pcriteria = pExample.createCriteria();
            pcriteria.andParentIdEqualTo(parent.getId());
            List<TbContentCategory> parents =
                    tbContentCategoryMapper.selectByExample(pExample);

            if (parents.size() == 1 && parents.get(0).getId() == id){
                TbContentCategory updateParent = new TbContentCategory();
                updateParent.setId(parent.getId());
                updateParent.setIsParent(false);
                updateParent.setUpdated(new Date());
                tbContentCategoryMapper.updateByPrimaryKeySelective(updateParent);
            }

            tbContentCategoryMapper.deleteByPrimaryKey(id);
            if (jedisUtil.exists(key1)){
                jedisUtil.del(key1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private TbContentCategory  fingItemByParentId(long parentId){
        TbContentCategory tbContentCategory =  tbContentCategoryMapper.selectByPrimaryKey(parentId);
        return tbContentCategory;
    }
}
