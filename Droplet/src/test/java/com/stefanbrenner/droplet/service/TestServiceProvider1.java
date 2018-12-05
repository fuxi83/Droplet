package com.stefanbrenner.droplet.service;

import org.mangosdk.spi.ProviderFor;

@ProviderFor(ITestService.class)
public class TestServiceProvider1 implements ITestService {
	@Override
	public String getServiceName() {
		return "TestServiceProvider1";
	}
}