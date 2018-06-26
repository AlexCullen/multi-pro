package com.douby.ssoweb.controller;

import com.douby.common.CookieUtils;
import com.douby.common.E3Result;
import com.douby.sso.dto.UserDto;
import com.douby.sso.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 * @Date: 2018/6/26 17:17
 * TODO:
 */

@Controller
public class SsoController {

    @Autowired
    private AccountService accountService;

    @Value("${token_key}")
    private String tokenKey;

    /**
     * 跳转到注册页面
     *
     * @return
     */
    @RequestMapping("/page/register")
    public String toRegistPage() {
        return "regist";
    }

    /**
     * 注册前的条件验证
     *
     * @param param
     * @param type
     * @return
     */
    @RequestMapping("/user/check/{param}/{type}")
    @ResponseBody
    public E3Result beforeRegistCheck(@PathVariable("param") String param,
                                      @PathVariable("type") int type) {
        return accountService.check(param, type);
    }

    /**
     * 用户注册
     *
     * @param userDto
     * @return
     */
    @RequestMapping(value = "/user/register", method = RequestMethod.POST)
    @ResponseBody
    public E3Result register(UserDto userDto) {
        return accountService.regist(userDto);
    }


    @RequestMapping("/page/login")
    public String toLoginPage() {
        return "login";
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public E3Result login(String username, String password,
                          HttpServletRequest request, HttpServletResponse response) {
        E3Result e3Result = accountService.loginIn(username, password);
        if (e3Result.getStatus() == 200) {
            CookieUtils.setCookie(request, response, tokenKey, e3Result.getData().toString());
        }
        return e3Result;
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    @ResponseBody
    public E3Result logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (tokenKey.equals(cookie.getName())) {
//                cookie.setMaxAge(0);
//                //设置cookie时,设置path为根路径 ,如果不设置path ,则无法让cookie 失效
//                cookie.setPath("/");
//                response.addCookie(cookie);
                CookieUtils.deleteCookie(request, response, tokenKey);
            }
        }
        E3Result e3Result = accountService.loginOut(tokenKey);
        return e3Result;
    }

    @RequestMapping(value="/user/token/{token}")
    @ResponseBody
    public E3Result checkToken(@PathVariable("token") String tokenId){
        return accountService.checkToken(tokenId);
    }
}
