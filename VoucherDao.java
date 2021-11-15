package com.moglix.rfqapi.dao;

import com.moglix.rfqapi.mapper.VoucherMapper;
import com.moglix.rfqapi.util.S3Util;
import com.opencsv.CSVParser;
import com.opencsv.CSVWriter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

@Component
public class VoucherDao {

    @Autowired
    DataSource dataSource;

    File file;

    @Autowired
    ServletContext servletContext;

    @Autowired
    VoucherMapper voucherMapper;

    @Autowired
    S3Util s3Util;

    VoucherDao(){

      this.file=new File("");

      String fileName="voucher.csv";
        Path p = Paths.get(this.file.toString(),"src","main","resources",fileName);
        this.file=new File(p.toString());
    }





    public void  getVoucher() throws SQLException, IOException {
        Connection con = DataSourceUtils.getConnection(dataSource);
        String query="select vcq.id ,vcq.name ,  vei.voucher_enquiry_id from voucher_customer_enquiry as vcq inner join voucher_enquiry_items as vei on vcq.id = vei.voucher_enquiry_id";
        PreparedStatement pst=con.prepareStatement(query);
        ResultSet rst=pst.executeQuery();
        ResultSetMetaData resultSetMetaData=rst.getMetaData();
        int count=resultSetMetaData.getColumnCount();
        String[] colName=new String[count];
        for(int i=1;i<=count;i++){
            //System.out.println(resultSetMetaData.getColumnName(i));
            colName[i-1]=resultSetMetaData.getColumnName(i);
        }


        List<String[]> csvList=new ArrayList<String[]>();
        csvList.add(colName);
        while(rst.next()){
            //System.out.println(rst.getString(1));
            String[] data=new String[count];
            for(int i=1;i<=count;i++){
                data[i-1]=rst.getString(i);
            }
            csvList.add(data);
        }
        System.out.println("end="+csvList.size());


        //to write into csv file we used CVSWriter class
        String path=servletContext.getRealPath("/")+"voucher.csv";
        File file=new File(path);
        FileWriter fileWriter=new FileWriter(file);

        CSVWriter csvWriter=new CSVWriter(fileWriter);
        csvWriter.writeAll(csvList);


        csvWriter.flush();
        csvWriter.close();



    }

    public String  putInAmazonS3(){

        String filePath=servletContext.getRealPath("/")+"voucher.csv";
        String contentType="text/csv";
        String bucketName="rfqReport";

        String uri=s3Util.getS3Path(filePath,"voucher.csv",bucketName,contentType);

        System.out.println(uri);

       return uri;






    }
}
