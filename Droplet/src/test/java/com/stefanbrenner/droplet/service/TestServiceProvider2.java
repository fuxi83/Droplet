package com.stefanbrenner.droplet.service;

import org.mangosdk.spi.ProviderFor;

@ProviderFor(ITestService.class)
public class TestServiceProvider2 implements ITestService {
	@Override
	public String getServiceName() {
		return "TestServiceProvider2";
	}
}