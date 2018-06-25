package com.douby.controller;

import com.douby.common.EasyUiDataGridResult;
import com.douby.common.E3Result;
import com.douby.manager.dto.TbItemDto;
import com.douby.manager.dto.TbItemSaveDto;
import com.douby.manager.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
 * @Date: 2018/6/4 16:50
 * TODO:
 */
@RestController
public class ItemController {
    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    public TbItemDto getItemById(@PathVariable Long itemId) {
        TbItemDto tbItem = itemService.getItemById(itemId);
        return tbItem;
    }

    @RequestMapping("/item/list")
    public EasyUiDataGridResult getItemList(Integer page, Integer rows) {
        //调用服务查询商品列表
        EasyUiDataGridResult result = itemService.getItemList(page, rows);
        return result;
    }

    @RequestMapping(value = "/item/save", method = RequestMethod.POST)
    @ResponseBody
    public E3Result saveItem(TbItemSaveDto tbItemSaveDto) {
        E3Result e3Result = null;
        try {
            e3Result = itemService.saveItem(tbItemSaveDto);
            itemService.sendMessage(e3Result.getData().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return e3Result;
    }


    @RequestMapping(value = "/item/update", method = RequestMethod.POST)
    @ResponseBody
    public E3Result updateItem(TbItemSaveDto tbItemSaveDto) {
        E3Result e3Result = null;
        try {
            e3Result = itemService.updateItem(tbItemSaveDto);
            itemService.sendMessage(e3Result.getData().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return e3Result;
    }

    @RequestMapping(value = "/item/delete", method = RequestMethod.POST)
    @ResponseBody
    public E3Result deleteItems(String ids) {
        E3Result e3Result = null;
        try {
            e3Result = itemService.deleteItems(ids);
            itemService.sendMessage(e3Result.getData().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return e3Result;
    }
}
