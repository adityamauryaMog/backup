package com.moglix.rfqapi.controller;


import com.moglix.rfqapi.constants.MailProperies;
import com.moglix.rfqapi.dao.VoucherDao;
import com.moglix.rfqapi.model.MailData;
import com.moglix.rfqapi.model.MailRequest;
import com.moglix.rfqapi.util.MailUtil;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("voucher")
public class VoucherController {

    @Autowired
    VoucherDao voucherDao;

    @Autowired
    MailUtil mailUtil;


    @RequestMapping(value="getvoucher")
    @ResponseBody
    public String getVoucher() throws SQLException, IOException {

      voucherDao.getVoucher();
       //voucherDao.putInAmazonS3();
        return "hello aditya";
    }

    @RequestMapping("upload")
    @ResponseBody
    public String upoadToS3AndGetUri(){
        return voucherDao.putInAmazonS3();

    }


    @RequestMapping("send")
    @ResponseBody
    public String sendMail(){
        MailData data=new MailData();


        MailRequest mailRequest=new MailRequest();
        mailRequest.setMailFrom(MailProperies.SEND_QUOTE_FROM);
        mailRequest.setMailTo(MailProperies.SEND_QUOTE_TO);

        int res=mailUtil.sendMail(mailRequest);
        if(res==1){
            return "success";
        }

        return "failed";
    }
}
