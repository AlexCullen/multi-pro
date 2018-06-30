package com.douby.cartproviderservice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douby.cartproviderinterface.service.CartService;
import com.douby.common.E3Result;
import com.douby.common.JedisUtil;
import com.douby.manager.dto.TbItemDto;
import com.douby.manager.pojo.TbItem;
import com.douby.mapper.TbItemMapper;
import org.apache.commons.lang3.StringUtils;
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
 * @Date: 2018/6/28 11:00
 * TODO:
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Value("${REDIS_CART_PRE}")
    private String redisKeyPre;

    /**
     * 将商品加入购物车，存入redis
     * redis中的存入类型是 hash， key是前缀+userId, value是TbItemDto
     *
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public E3Result addProduct2Cart(Long userId, long itemId, int num) {
        String redisKey = redisKeyPre + userId;

        String fieldKey = String.valueOf(itemId);
        try {
            boolean flag = increaseProductInCart(redisKey, fieldKey, num, true);

            if (!flag) {
                addProduct2Cart(redisKey, fieldKey, itemId, num);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return E3Result.build(-1, "未知错误");
        }
        return E3Result.ok();
    }

    @Override
    public E3Result addProduct2Cart(long itemId, int num, List<TbItemDto> list) {
        boolean flag = true;
        for (TbItemDto tbItemDto : list) {
            if (itemId == tbItemDto.getId()) {
                tbItemDto.setNum(tbItemDto.getNum() + num);
                flag = false;
                break;
            }
        }
        if (flag) {
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
            TbItemDto tbItemDto = new TbItemDto(tbItem);
            tbItemDto.setNum(num);
            list.add(tbItemDto);
        }
        return E3Result.ok(list);
    }

    /**
     * 商品的数据来源是Cookie
     * 将cookie中的数据加入redis中
     *
     * @param userId
     * @param tbItemDtoList
     * @return
     */
    @Override
    public E3Result addProductList2Cart(Long userId, List<TbItemDto> tbItemDtoList) {
        for (TbItemDto tbItem : tbItemDtoList) {
            addProduct2Cart(userId, tbItem.getId(), tbItem.getNum());
        }
        return E3Result.ok();
    }

    @Override
    public E3Result updateProduct2Cart(Long userId, long itemId, int num) {
        String redisKey = redisKeyPre + userId;

        String fieldKey = String.valueOf(itemId);

        increaseProductInCart(redisKey, fieldKey, num, false);
        return E3Result.ok(getProductListFCart(userId));
    }

    @Override
    public E3Result updateProduct2Cart(List<TbItemDto> tbItemDtoList, long itemId, int num) {
        for (TbItemDto tbItemDto : tbItemDtoList) {
            if (tbItemDto.getId() == itemId) {
                tbItemDto.setNum(num);
                break;
            }
        }
        return E3Result.ok(tbItemDtoList);
    }

    @Override
    public E3Result removeProductFCart(Long userId, long itemId) {
        String redisKey = redisKeyPre + userId;

        String fieldKey = String.valueOf(itemId);

        jedisUtil.hdel(redisKey, fieldKey);

        return E3Result.ok(getProductListFCart(userId));
    }

    @Override
    public E3Result removeProductFCart(List<TbItemDto> tbItemDtoList, long itemId) {
        for (TbItemDto tbItemDto : tbItemDtoList) {
            if (tbItemDto.getId() == itemId) {
                tbItemDtoList.remove(tbItemDto);
                break;
            }
        }
        return E3Result.ok(tbItemDtoList);
    }

    /**
     * 从redis总获取购物车的数据
     *
     * @param userId
     * @return
     */
    @Override
    public List<TbItemDto> getProductListFCart(Long userId) {
        String redisKey = redisKeyPre + userId;
        List<String> cartJson = new ArrayList<>();
        if (jedisUtil.exists(redisKey)) {
            cartJson = jedisUtil.hvals(redisKey);
        }
        List<TbItemDto> tbItemDtoList = new ArrayList<>();
        for (String productJson : cartJson) {
            TbItemDto tbItemDto = JSONObject.parseObject(productJson, TbItemDto.class);
            tbItemDtoList.add(tbItemDto);
        }
        return tbItemDtoList;
    }

    /**
     * 对于已经存在的商品，进行数量的增加操作
     *
     * @param redisKey
     * @param fieldKey
     * @param num
     * @return
     */
    private boolean increaseProductInCart(String redisKey, String fieldKey, int num, boolean add) {
        //在redis中存在该条记录
        if (jedisUtil.hexists(redisKey, fieldKey)) {
            String itemJson = jedisUtil.hget(redisKey, fieldKey);
            TbItemDto tbItemDto = JSONObject.parseObject(itemJson, TbItemDto.class);
            //true，add 增加数量，在原有基础上， false，update，重新设置数量
            if (add) {
                tbItemDto.setNum(tbItemDto.getNum() + num);
            } else {
                tbItemDto.setNum(num);
            }
            jedisUtil.hset(redisKey, fieldKey, JSONObject.toJSONString(tbItemDto));
            return true;
        }
        return false;
    }

    private void addProduct2Cart(String redisKey, String fieldKey, long primaryId, int num) {
        //在redis中不存在， 那么就从数据库（其实最好从solr库，毕竟速度快）中去查询， 并将该条记录放入redis中
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(primaryId);
        TbItemDto tbItemDto = new TbItemDto(tbItem);
        tbItemDto.setNum(num);

        //取第一张图片
        String image = tbItemDto.getImage();
        if (StringUtils.isNotBlank(image)) {
            tbItemDto.setImage(image.split(",")[0]);
        }
        jedisUtil.hset(redisKey, fieldKey, JSONObject.toJSONString(tbItemDto));
    }
}
