package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.bj58.lbg.rs.entity.RsDemandAmountBookEntity;
import com.bj58.lbg.rs.entity.RsDemandDetailEntity;
import com.bj58.lbg.rs.entity.RsDemandEntity;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.biz.DemandBiz;
import com.bj58.qa.rs.inteface.dataprovider.DemandProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

public class DemandTest extends BaseTest {
	private String url = "http://rsm.58.com/demand/submit";

	private Map<String, String> header;

	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = DemandProvider.class, dataProvider = "demandSubmitProvider")
	public void demandSubmitTest(String phone, String msg) throws Exception {
		Logger.log(msg + "测试用例开始=======================");
		Map<String, String> params = new HashMap<String, String>();
		RsDemandEntity demandEntity = DemandBiz.genRsDemand(phone);
		RsDemandAmountBookEntity amount = DemandBiz.genRsAmountBook();
		RsDemandDetailEntity remandDetail = DemandBiz.genDemandDetail();
		params = DemandBiz.genParams(demandEntity, remandDetail, amount);

		JSONObject responseObj = HttpRequest.doPostReturnResponseJson(url, params);
		System.out.println(responseObj);

		Object message = responseObj.get("message");
		System.out.println("******" + message);
		Assert.assertEquals(message.toString(), "SUCCESS", "新建搬家需求接口结果返回错误");

		Object data = responseObj.get("data");
		Assert.assertNotEquals(StringUtils.isBlank(String.valueOf(data)), "", "新建需求接口返回需求ID为空");

		Long demandId = Long.parseLong(String.valueOf(data));

		PreparedStatement demandPs = db.prepareStatement("select * from t_rs_demand where id = " + demandId);
		ResultSet demandRS = demandPs.executeQuery();
		List demandList = CommonUtil.resultSetToList(demandRS);
		Assert.assertEquals(demandList.size(), 1, "插入t_rs_demand一条记录失败， id为：" + demandId);

		PreparedStatement amountBookPs = db
				.prepareStatement("select * from t_rs_demand_amount_book where demand_id = " + demandId);
		ResultSet amountBookRS = amountBookPs.executeQuery();
		List amountBookList = CommonUtil.resultSetToList(amountBookRS);
		Assert.assertEquals(amountBookList.size(), 1, "插入t_rs_demand_amount_book一条记录失败， demand_id为：" + demandId);

		PreparedStatement demandDetailPs = db
				.prepareStatement("select * from t_rs_demand_detail where demand_id = " + demandId);
		ResultSet demandDetailRS = demandDetailPs.executeQuery();
		List demandDetailList = CommonUtil.resultSetToList(demandDetailRS);
		Assert.assertEquals(demandDetailList.size(), 1, "插入t_rs_demand_detail一条记录失败， demand_id为：" + demandId);

		PreparedStatement tellLogPs = db
				.prepareStatement("select * from t_rs_staff_tellog where demand_id = " + demandId);
		ResultSet tellLogRS = tellLogPs.executeQuery();
		List tellLogList = CommonUtil.resultSetToList(tellLogRS);
		Assert.assertEquals(tellLogList.size(), 1, "插入t_rs_demand_detail一条记录失败， demand_id为：" + demandId);
	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}
	}
}
