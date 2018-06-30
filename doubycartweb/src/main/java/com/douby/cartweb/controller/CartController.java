package com.douby.cartweb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douby.cartproviderinterface.service.CartService;
import com.douby.common.CookieUtils;
import com.douby.common.E3Result;
import com.douby.manager.dto.TbItemDto;
import com.douby.sso.dto.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * @Date: 2018/6/28 10:01
 * TODO:
 */

@Controller
public class CartController {

    @Value("${CART_COOKIE_KEY_NAME}")
    private String cartCookieKeyName;

    @Value("${COOKIE_CART_EXPIRE}")
    private int cartExpire;

    @Autowired
    private CartService cartService;

    /**
     * 查看购物车的流程
     * 1：获取cookie中的临时购物车数据
     * 2：判断用户是否登录
     * 3：已经登录，将cookie中的购物车数据存入redis中， 再获取用户的购物车数据, 删除当前的cookie， 将重新获取的数据存入cookie
     * 6：未登录，执行下一步
     * 7：将数据放入request域中
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/cart/cart")
    public String toCartPage(HttpServletRequest req, HttpServletResponse resp) {

        //从Cookie中获取购物车的数据
        List<TbItemDto> itemDtoList = productFromCookie(req);

        //获取当前用户的购物车数据， 用户未登录返回Cookie中的list
        List<TbItemDto> itemDtoListFUser = productFromUser(req, resp, itemDtoList);

        //放入request域中
        req.setAttribute("cartList", itemDtoListFUser);

        return "cart";
    }

    /**
     * 加入购物车
     * 当前用户未登录，购物车存放在cookie中
     * 已登录， 购物车数据存放在redis中，
     *
     * @param itemId
     * @param num
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("/cart/add/{itemId}")
    public String addProduct2Cart(@PathVariable("itemId") long itemId, @RequestParam(defaultValue = "1") int num,
                                  HttpServletRequest req, HttpServletResponse resp) {
        List<TbItemDto> tbItemDtoList = productFromCookie(req);

        //若用户已经登录，那么就将数据加入redis中
        UserDto userDto = (UserDto) req.getAttribute("user");
        if (userDto != null) {
            cartService.addProduct2Cart(userDto.getId(), itemId, num);
            return "cartSuccess";
        }

        //若用户未登录，从cookie获取数据， 无相同的就添加一条记录， 有相同的就在当前的基础上+上num
        E3Result e3Result = cartService.addProduct2Cart(itemId, num, tbItemDtoList);
        if (e3Result.getStatus() == 200) {
            tbItemDtoList = (List<TbItemDto>) e3Result.getData();
        }
        CookieUtils.setCookie(req, resp, cartCookieKeyName, JSONObject.toJSONString(tbItemDtoList), cartExpire, true);

        return "cartSuccess";
    }

    @RequestMapping("/cart/update/num/{itemId}/{num}")
    @ResponseBody
    public E3Result updateProductFCart(@PathVariable("itemId") long itemId, @PathVariable("num") int num,
                                       HttpServletRequest req, HttpServletResponse resp) {
        //用户登录
        //若用户已经登录，那么就将数据加入redis中
        UserDto userDto = (UserDto) req.getAttribute("user");
        if (userDto != null) {
            List<TbItemDto> list = new ArrayList<>();
            cartService.updateProduct2Cart(userDto.getId(), itemId, num);
            return E3Result.ok();
        }
        //用户未登录
        List<TbItemDto> tbItemDtoList = productFromCookie(req);
        E3Result e3Result = cartService.updateProduct2Cart(tbItemDtoList, itemId, num);
        tbItemDtoList = (List<TbItemDto>) e3Result.getData();
        CookieUtils.setCookie(req, resp, cartCookieKeyName, JSONObject.toJSONString(tbItemDtoList), cartExpire, true);
        return E3Result.ok();
    }

    @RequestMapping("/cart/delete/{itemId}")
    public String removeProductFCart(@PathVariable("itemId") long itemId,
                                       HttpServletRequest req, HttpServletResponse resp) {
        //用户登录
        //若用户已经登录，那么就将数据加入redis中
        UserDto userDto = (UserDto) req.getAttribute("user");
        if (userDto != null) {
            cartService.removeProductFCart(userDto.getId(), itemId);
            return "redirect:/cart/cart.html";
        }
        //用户未登录
        List<TbItemDto> tbItemDtoList = productFromCookie(req);
        E3Result e3Result =  cartService.removeProductFCart(tbItemDtoList, itemId);
        CookieUtils.setCookie(req, resp, cartCookieKeyName, JSONObject.toJSONString(e3Result.getData()), cartExpire, true);
        return "redirect:/cart/cart.html";
    }

    /**
     * 从Cookie中获取购物车列表
     *
     * @param req
     * @return
     */
    private List<TbItemDto> productFromCookie(HttpServletRequest req) {
        //从Cookie中获取商品列表
        String productJson = CookieUtils.getCookieValue(req, cartCookieKeyName, true);

        List<TbItemDto> resultList = new ArrayList<>();
        boolean flag = StringUtils.isNotBlank(productJson);
        if (flag) {
            try {
                resultList = JSONArray.parseArray(productJson, TbItemDto.class);
            } catch (Exception pe) {
                pe.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * 判断当前用户是否登录
     * 登录：将cookie中的购物车数据存入redis中， 再获取用户的购物车数据, 删除当前的cookie
     * 未登录： 返回原始的List
     *
     * @param req
     * @return
     */
    private List<TbItemDto> productFromUser(HttpServletRequest req, HttpServletResponse resp, List<TbItemDto> cookieItemList) {
        List<TbItemDto> itemDtoList = new ArrayList<>();
        //判断用户是否登录
        UserDto userDto = (UserDto) req.getAttribute("user");
        if (userDto != null) {
            //将数据放入Redis中
            cartService.addProductList2Cart(userDto.getId(), cookieItemList);

            //重新从redis中获取
            itemDtoList = cartService.getProductListFCart(userDto.getId());

            //删除当前的cookie
            CookieUtils.deleteCookie(req, resp, cartCookieKeyName);

            return itemDtoList;
        }
        return cookieItemList;
    }
}
