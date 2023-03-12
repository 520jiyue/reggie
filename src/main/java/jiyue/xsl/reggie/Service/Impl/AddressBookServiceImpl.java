package jiyue.xsl.reggie.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jiyue.xsl.reggie.Entity.AddressBook;
import jiyue.xsl.reggie.Mapper.AddressBookMapper;
import jiyue.xsl.reggie.Service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
