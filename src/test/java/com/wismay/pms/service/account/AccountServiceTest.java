package com.wismay.pms.service.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springside.modules.test.security.shiro.ShiroTestUtils;

import com.wismay.pms.data.UserData;
import com.wismay.pms.entity.User;
import com.wismay.pms.repository.UserDao;
import com.wismay.pms.service.ServiceException;
import com.wismay.pms.service.account.ShiroDbRealm.ShiroUser;

/**
 * AccountService的测试用例, 测试Service层的业务逻辑.
 * 
 * @author calvin
 */
public class AccountServiceTest {

	@InjectMocks
	private AccountService accountService;

	@Mock
	private UserDao mockUserDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ShiroTestUtils.mockSubject(new ShiroUser(3L, "foo", "Foo"));
	}

	@Test
	public void registerUser() {
		User user = UserData.randomNewUser();
		Date currentTime = new Date();

		accountService.registerUser(user);

		// 验证user的角色，注册日期和加密后的密码都被自动更新了。
		assertEquals("user", user.getRoles());
		assertEquals(currentTime, user.getRegisterDate());
		assertNotNull(user.getPassword());
		assertNotNull(user.getSalt());
	}

	@Test
	public void updateUser() {
		// 如果明文密码不为空，加密密码会被更新.
		User user = UserData.randomNewUser();
		accountService.updateUser(user);
		assertNotNull(user.getSalt());

		// 如果明文密码为空，加密密码无变化。
		User user2 = UserData.randomNewUser();
		user2.setPlainPassword(null);
		accountService.updateUser(user2);
		assertNull(user2.getSalt());
	}

	@Test
	public void deleteUser() {
		// 正常删除用户.
		accountService.deleteUser(2L);
		Mockito.verify(mockUserDao).delete(2L);

		// 删除超级管理用户抛出异常, userDao没有被执行
		try {
			accountService.deleteUser(1L);
			fail("expected ServicExcepton not be thrown");
		} catch (ServiceException e) {
			// expected exception
		}
		Mockito.verify(mockUserDao, Mockito.never()).delete(1L);
	}

}
