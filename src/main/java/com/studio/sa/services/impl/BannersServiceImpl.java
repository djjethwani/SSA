package com.studio.sa.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.studio.sa.repo.BannersRepo;
import com.studio.sa.services.BannersService;

@Service
public class BannersServiceImpl implements BannersService {
	
	@Autowired
	BannersRepo bannersRepo;
}
