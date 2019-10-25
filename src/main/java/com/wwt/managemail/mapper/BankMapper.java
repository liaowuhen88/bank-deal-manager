package com.wwt.managemail.mapper;

import com.wwt.managemail.dto.MailDTO;
import com.wwt.managemail.entity.Bank;
import com.wwt.managemail.entity.Mail;
import com.wwt.managemail.utils.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BankMapper extends CommonMapper<Bank> {
    int transaction(Bank bank );
}
