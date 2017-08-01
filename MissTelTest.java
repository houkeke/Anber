package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bj58.lbg.rs.entity.RsMissTelEntity;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.MissTelProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

public class MissTelTest extends BaseTest {

	private String url = "http://rsm.58.com/misstel/list";

	private Map<String, String> header;

	private DBUtil db;

	@Override
	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = MissTelProvider.class, dataProvider = "missTelProvider")
	public void misTelListTest(String msg) throws Exception {
		Logger.log(msg + "测试开始=========发送Http请求： " + url, true);
		JSONObject responseObj = HttpRequest.doGetReturnResponseJson(url, "NVrgLpVluBFisSw=201704181352000688ee3a",
				header);
		System.out.println(responseObj);
		Object message = responseObj.get("message");
		Assert.assertEquals(message.toString(), "SUCCESS", "获取未接来电列表返回状态码错误");

		JSONArray arrayData = responseObj.getJSONArray("data");
		int missTelSize = arrayData.size();

		for (int index = 0; index < missTelSize; index++) {
			RsMissTelEntity missTelEntity = arrayData.getJSONObject(index).toJavaObject(RsMissTelEntity.class);
			System.out.println(missTelEntity.getMisstelOpenid());
		}

		PreparedStatement misTelPs = db.prepareStatement("select * from t_rs_miss_tel where state = 0");
		ResultSet misTelRS = misTelPs.executeQuery();
		List misTelList = CommonUtil.resultSetToList(misTelRS);

		Assert.assertEquals(missTelSize, misTelList.size(), "获取未接来电列表数量与数据库不一致");
	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}
	}
}
