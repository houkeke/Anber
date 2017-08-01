package com.bj58.qa.rs.inteface.modeltest;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.OrderBusinesslistProvider;

import junit.framework.Assert;

/**
 * 需求页商机派单商家列表接口
 * @author hkk 
 * 2017.07.18
 * 判断接口返回的数据数量与数据库中的是否一致
 * 
*/
public class OrderBusinesslistTest extends BaseTest {
	private String url = "http://rsm.58.com/order/businesslist";
	private Map<String, String> header;
	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = OrderBusinesslistProvider.class, dataProvider = "orderBusinesslistProvider")
	public void orderBusinesslistTestOrderBusinesslistTest(String demandid, String msg, String num) throws Exception {
		Logger.log("需求页商机派单商家列表接口 " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("需求页商机派单商家列表");

		Map<String, String> param = new HashMap<String, String>();
		param.put("demandid", demandid);
		param.put("NVrgLpVluBFisSw", "201702141355236f87c771");

		JSONObject responseObj = HttpRequest.doPostReturnResponseJson(url + "/" + demandid, param);
		System.out.println(responseObj);
		responseObj.remove("cookies");
		responseObj.remove("header");
		int businessNumber = responseObj.size();
		final int businessLimitNum = 3;
		if (Integer.parseInt(num) == businessLimitNum) {
			Assert.assertEquals(msg + num + "获取符合条件的商家列表失败", 0, businessNumber);
		} else {
			boolean flag = false;
			if (businessNumber != 0) {
				flag = true;
			}
			Assert.assertEquals(msg + num + "获取符合条件的商家列表失败", true, flag);
		}

	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}
	}

}
