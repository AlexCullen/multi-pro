package com.douby.sso.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.douby.common.E3Result;
import com.douby.common.JedisClusterUtil;
import com.douby.common.JedisUtil;
import com.douby.manager.pojo.TbUser;
import com.douby.manager.pojo.TbUserExample;
import com.douby.mapper.TbUserMapper;
import com.douby.sso.contant.Constant;
import com.douby.sso.dto.UserDto;
import com.douby.sso.service.AccountService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
 * @Date: 2018/6/26 9:41
 * TODO:
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private TbUserMapper tbUserMapper;

    /**
     * 注册用户
     *
     * @return
     */
    @Override
    public E3Result regist(UserDto user) {
        try {
            TbUser tbUser = new TbUser();
            tbUser.setUsername(user.getUsername());
            tbUser.setPassword(DigestUtils.md5Hex(user.getPassword()));
            tbUser.setCreated(new Date());
            tbUser.setUpdated(new Date());
            tbUser.setEmail(user.getEmail());
            tbUser.setPhone(user.getPhone());
            int effCount = tbUserMapper.insert(tbUser);
            return E3Result.ok(effCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return E3Result.build(404, "注册失败");
    }

    @Override
    public E3Result check(String param, int type) {
        if (type == 1) {
            return E3Result.ok(checkUserName(param));
        } else if (type == 2) {
            return E3Result.ok(checkPhone(param));
        } else if (type == 3) {
            return E3Result.ok(checkEmail(param));
        } else {
            return E3Result.build(400, "数据类型错误");
        }
    }

    @Override
    public E3Result loginIn(String userName, String password) {
        TbUser tbUser = loginCheckUserName(userName);
        if (tbUser == null) {
            return E3Result.build(400, "用户不存在");
        }

        if (!loginCheckPassword(password, tbUser)) {
            return E3Result.build(400, "密码错误");
        }

        UserDto userDto = new UserDto();
        userDto.setUsername(tbUser.getUsername());
        userDto.setPhone(tbUser.getPhone());
        userDto.setEmail(tbUser.getEmail());

        //授权令牌 存入redis中 时间限制30min
        String token = UUID.randomUUID().toString();
        jedisUtil.set(Constant.TOKEN_PRE + token, JSONObject.toJSONString(userDto));
        jedisUtil.expire(Constant.TOKEN_PRE + token, Constant.TOKEN_EXPIRE);
        return E3Result.ok(Constant.TOKEN_PRE + token);
    }

    @Override
    public E3Result loginOut(String token) {
        jedisUtil.del(Constant.TOKEN_PRE);
        return E3Result.ok(true);
    }

    @Override
    public E3Result checkToken(String token) {
        if (!jedisUtil.exists(token)) {
            return E3Result.build(404, "授权已过期");
        }
        String tokenObj = jedisUtil.get(token);

        if (StringUtils.isBlank(tokenObj)) {
            return E3Result.build(404, "授权已过期");
        }
        UserDto userDto = JSONObject.parseObject(tokenObj, UserDto.class);
        jedisUtil.expire(token, Constant.TOKEN_EXPIRE);
        return E3Result.ok(userDto);
    }

    private boolean loginCheckPassword(String password, TbUser tbUser) {
        String passwordMd5 = DigestUtils.md5Hex(password);
        return passwordMd5.equals(tbUser.getPassword());
    }

    private TbUser loginCheckUserName(String userName) {
        TbUserExample tbUserExample = new TbUserExample();
        tbUserExample.createCriteria().andUsernameEqualTo(userName);
        List<TbUser> tbUserList = tbUserMapper.selectByExample(tbUserExample);
        if (tbUserList == null || tbUserList.size() == 0) {
            return null;
        }
        return tbUserList.get(0);
    }

    /**
     * 用户信息校验
     *
     * @param user
     * @return
     */
    @Override
    public E3Result check(UserDto user) {
        if (user == null ||
                StringUtils.isBlank(user.getUsername()) ||
                StringUtils.isBlank(user.getPassword()) ||
                StringUtils.isBlank(user.getPhone())) {
            return E3Result.build(404, "注册信息不能为空");
        }
        if (!checkUserName(user.getUsername())) {
            return E3Result.build(404, "用户已存在");
        }
        if (!checkPhone(user.getPhone())) {
            return E3Result.build(404, "手机号已存在");
        }
        return E3Result.ok();
    }

    /**
     * 两个检测可以试试使用策略模式
     * 检测username是否可用
     *
     * @param userName
     * @return
     */
    private boolean checkUserName(String userName) {
        TbUserExample tbUserExample = new TbUserExample();
        tbUserExample.createCriteria().andUsernameEqualTo(userName);
        int count = tbUserMapper.countByExample(tbUserExample);
        return count == 0;
    }

    /**
     * 检测phone是否可用
     *
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        TbUserExample tbUserExample = new TbUserExample();
        tbUserExample.createCriteria().andPhoneEqualTo(phone);
        int count = tbUserMapper.countByExample(tbUserExample);
        return count == 0;
    }

    /**
     * 检测phone是否可用
     *
     * @param email
     * @return
     */
    private boolean checkEmail(String email) {
        TbUserExample tbUserExample = new TbUserExample();
        tbUserExample.createCriteria().andEmailEqualTo(email);
        int count = tbUserMapper.countByExample(tbUserExample);
        return count == 0;
    }
}
