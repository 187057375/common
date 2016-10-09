package org.wsh.common.provider.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/provider-bean.xml");
		context.start();

		System.in.read(); // 按任意键退出
	}
}
