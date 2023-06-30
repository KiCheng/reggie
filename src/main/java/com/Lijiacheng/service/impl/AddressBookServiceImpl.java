package com.Lijiacheng.service.impl;

import com.Lijiacheng.domain.AddressBook;
import com.Lijiacheng.mapper.AddressBookMapper;
import com.Lijiacheng.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService{
}
