package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.bj58.lbg.rs.entity.RsSettlementEntity;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.SettlementExceptionDataProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

public class SettlementExceptionTest extends BaseTest{

    private String url = "http://rsm.58.com/settlement/exception";
//	private String url="http://rsm.58.com/data/citys";
    		//"?settlementid=877428028450324480&remark=sdfsd&amount=12";

    private Map<String, String> header;

    private DBUtil db;

    @BeforeClass
    public void beforeClass() {
        db = new DBUtil("M");// 初始化数据库
        db.connection();// 连接数据库
    }

    @Test(dataProviderClass=SettlementExceptionDataProvider.class,dataProvider = "settlementExceptionProvider")
    public void settlementExceptionTest(String msg,String settlementid) throws SQLException{
        Map<String, String> params = new HashMap<String, String>();

        params.put("NVrgLpVluBFisSw","2016051714120056878e24");
        params.put("settlementid",settlementid);
        params.put("remark","修改备注价格");
        params.put("amount","12");
        Logger.log(msg + "测试开始=========发送Http请求： " + url, true);

        //记录插入之前log表中数据条数
        PreparedStatement db_num = db.prepareStatement("select count(*) from t_rs_settlement_log ");
        ResultSet db_num_befor = db_num.executeQuery();
        List db_befor = CommonUtil.resultSetToList(db_num_befor);
        Map<String,Object> dbmap_befor = (Map)db_befor.get(0);
        
        JSONObject  responseObj=HttpRequest.doPostReturnResponseJson(url, params);
        responseObj.remove("header");
        responseObj.remove("cookies");
        //取出插入之后log表中数据条数
        ResultSet db_num_after =db_num.executeQuery();
        List db_after = CommonUtil.resultSetToList(db_num_after);
        Map<String,Object> dbmap_after = (Map)db_after.get(0);
        //比对数据库记录条数，判断是否插入成功        
        Assert.assertEquals(dbmap_befor.get("count"),dbmap_after.get("count"), "异常处理结算订单日志信息录入失败!");
        
        Object state = responseObj.get("state");
        Assert.assertEquals(state.toString(),"1", "异常处理结算订单日志信息录入失败!");
        

    }

    @AfterClass
    public void afterClass(){
        if(db!=null){//断开数据库连接
            db.closeConnection();
        }
    }
}
