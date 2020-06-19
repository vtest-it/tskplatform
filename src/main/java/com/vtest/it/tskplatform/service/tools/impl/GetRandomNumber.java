package com.vtest.it.tskplatform.service.tools.impl;

import org.springframework.stereotype.Service;

/**
 * @author shawn.sun
 * @date 2020/06/10 12:53:23
 */
@Service
public class GetRandomNumber {
	public String getRandomNumber(int length) {
		StringBuffer SB=new StringBuffer();
		for (int i = 0; i < length; i++) {
			int RandomNumber=(int) ((Math.random()*6)/1);
			SB.append(RandomNumber);
		}
		return SB.toString();
	}
}
