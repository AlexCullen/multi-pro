package com.douby.cartproviderinterface.service;

import com.douby.common.E3Result;
import com.douby.manager.dto.TbItemDto;

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
 * @Date: 2018/6/28 10:53
 * TODO:
 */
public interface CartService {

    public E3Result addProduct2Cart(Long userId, long itemId, int num);

    public E3Result addProduct2Cart(long itemId, int num, List<TbItemDto> list);

    public E3Result addProductList2Cart(Long userId, List<TbItemDto> tbItemDtoList);

    public E3Result updateProduct2Cart(Long userId, long itemId, int num);

    public E3Result updateProduct2Cart(List<TbItemDto> tbItemDtoList, long itemId, int num);

    public E3Result removeProductFCart(Long userId, long itemId);

    public E3Result removeProductFCart(List<TbItemDto> tbItemDtoList, long itemId);

    public List<TbItemDto>  getProductListFCart(Long userId);
}
