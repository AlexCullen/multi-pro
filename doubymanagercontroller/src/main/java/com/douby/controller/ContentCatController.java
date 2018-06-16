package com.douby.controller;

import com.douby.common.E3Result;
import com.douby.common.EasyUiTreeNode;
import com.douby.content.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * @Date: 2018/6/8 10:53
 * TODO:
 */
@Controller
public class ContentCatController {
    @Autowired
    private ContentCategoryService contentCategoryService;


    @RequestMapping("/content/category/list")
    @ResponseBody
    public List<EasyUiTreeNode> getTreeNodes(@RequestParam(name = "id", defaultValue = "0") long parentId) {
        E3Result e3Result = contentCategoryService.getContentCateList(parentId);
        List<EasyUiTreeNode> easyUiTreeNodeList = (List<EasyUiTreeNode>) e3Result.getData();
        return easyUiTreeNodeList;
    }

    @RequestMapping("content/category/create")
    @ResponseBody
    public E3Result addTreeNode(long parentId, String name) {
        E3Result e3Result = contentCategoryService.addTreeNode(parentId, name);
        return e3Result;
    }

    @RequestMapping("content/category/delete")
    @ResponseBody
    public void deleteTreeNode(long id) {
        contentCategoryService.deleteTreeNode(id);
    }

    @RequestMapping("content/category/update")
    @ResponseBody
    public E3Result updateTreeNode(long id, String name) {
        E3Result e3Result = contentCategoryService.updateTreeNode(id, name);
        return e3Result;
    }
}
