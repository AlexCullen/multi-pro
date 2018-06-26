package com.douby.sso;

import static org.junit.Assert.assertTrue;

import com.douby.common.E3Result;
import com.douby.sso.dto.UserDto;
import com.douby.sso.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring/spring-base.xml"})
public class AppTest
{
    @Autowired
    private AccountService accountService;

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void registTest(){
        System.out.println(accountService == null);
        UserDto userDto = new UserDto();
        userDto.setUsername("douby");
        userDto.setPassword("12121");
        userDto.setPhone("13629583007");
        userDto.setEmail("18211@qq.com");
        E3Result e3Result = accountService.regist(userDto);
        System.out.println(e3Result.getStatus());
    }

    @Test
    public void loginTest(){
        UserDto userDto = new UserDto();
        userDto.setUsername("douby");
        userDto.setPassword("12121");
       E3Result e3Result = accountService.loginIn(userDto);
        System.out.println(e3Result.getStatus());
    }
}
