package com.douby.manager.service.impl;

import com.douby.common.EasyUiDataGridResult;
import com.douby.common.IDUtils;
import com.douby.common.E3Result;
import com.douby.manager.dto.TbItemDto;
import com.douby.manager.dto.TbItemSaveDto;
import com.douby.manager.pojo.TbItemDesc;
import com.douby.manager.service.ItemService;
import com.douby.mapper.TbItemDescMapper;
import com.douby.mapper.TbItemMapper;
import com.douby.manager.pojo.TbItem;
import com.douby.manager.pojo.TbItemExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * @Date: 2018/6/4 16:37
 * TODO:
 */
@Component
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;

    @Override
    public TbItemDto getItemById(long itemId) {
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        TbItemDto tbItemDto = new TbItemDto(tbItem);
        return tbItemDto;
    }

    @Override
    public EasyUiDataGridResult getItemList(int page, int rows) {
        PageHelper.startPage(page, rows);
        TbItemExample tbItemExample = new TbItemExample();
        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        PageInfo<TbItem> pageInfo = new PageInfo<>(tbItemList);
        List<TbItemDto> tbItemDtos = new ArrayList<>();
        for (TbItem tbItem : pageInfo.getList()) {
            TbItemDto tbItemDto = new TbItemDto(tbItem);
            tbItemDtos.add(tbItemDto);
        }
        int total = (int) pageInfo.getTotal();
        EasyUiDataGridResult<TbItemDto> easyUiDataGridResult =
                new EasyUiDataGridResult<>(total, tbItemDtos);
        return easyUiDataGridResult;
    }

    @Override
    public E3Result saveItem(TbItemSaveDto tbItemSaveDto) {
        E3Result e3Result = E3Result.ok();
        try {
            long id = IDUtils.genItemId();
            TbItem tbItem = new TbItem();
            tbItem.setId(id);
            tbItem.setTitle(tbItemSaveDto.getTitle());
            tbItem.setSellPoint(tbItemSaveDto.getSellPoint());
            tbItem.setPrice(tbItemSaveDto.getPrice());
            tbItem.setNum(tbItemSaveDto.getNum());
            tbItem.setBarcode(tbItemSaveDto.getBarcode());
            tbItem.setImage(tbItemSaveDto.getImage());
            tbItem.setCid(tbItemSaveDto.getCid());
            tbItem.setStatus((byte) 1);
            tbItem.setCreated(new Date());
            tbItem.setUpdated(new Date());
            String desc = tbItemSaveDto.getDesc();
            TbItemDesc tbItemDesc = new TbItemDesc(desc);
            tbItemDesc.setItemId(id);

            tbItemMapper.insert(tbItem);
            tbItemDescMapper.insert(tbItemDesc);
        } catch (Exception e) {
            e3Result = E3Result.build(0, e.getMessage());
        }
        return e3Result;
    }

    @Override
    public E3Result updateItem(TbItemSaveDto tbItemSaveDto) {
        E3Result e3Result = E3Result.ok();
        try {
            TbItem tbItem = new TbItem();
            tbItem.setId(tbItemSaveDto.getId());
            tbItem.setTitle(tbItemSaveDto.getTitle());
            tbItem.setSellPoint(tbItemSaveDto.getSellPoint());
            tbItem.setPrice(tbItemSaveDto.getPrice());
            tbItem.setNum(tbItemSaveDto.getNum());
            tbItem.setBarcode(tbItemSaveDto.getBarcode());
            tbItem.setImage(tbItemSaveDto.getImage());
            tbItem.setCid(tbItemSaveDto.getCid());
            tbItem.setUpdated(new Date());
            String desc = tbItemSaveDto.getDesc();
            TbItemDesc tbItemDesc = new TbItemDesc(desc);
            tbItemDesc.setItemId(tbItemSaveDto.getId());
            tbItemDesc.setItemDesc(tbItemSaveDto.getDesc());
            tbItemMapper.updateByPrimaryKeySelective(tbItem);
            tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
        } catch (Exception e) {
            e3Result = E3Result.build(0, e.getMessage());
        }
        return e3Result;
    }

    @Override
    public E3Result deleteItems(String ids) {
        E3Result e3Result = E3Result.ok();
        try {
            String[] idArr = ids.split(",");
            for (String id : idArr) {
                tbItemMapper.deleteByPrimaryKey(Long.valueOf(id));
                tbItemDescMapper.deleteByPrimaryKey(Long.valueOf(id));
            }
        } catch (Exception e) {
            e3Result = E3Result.build(0, e.getMessage());

        }
        return e3Result;
    }
}
