package com.bj58.qa.rs.inteface.modeltest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSONObject;
import com.bj58.qa.hy.inteface.BaseTest;
import com.bj58.qa.hy.inteface.database.DBUtil;
import com.bj58.qa.hy.inteface.http.HttpRequest;
import com.bj58.qa.hy.inteface.utils.Logger;
import com.bj58.qa.rs.inteface.dataprovider.PayRefundProvider;
import com.bj58.qa.rs.inteface.util.CommonUtil;

import junit.framework.Assert;

/**
 * 发起退款/退单接口
 * @author hkk 
 * 2017.07.07
 * 1.判断数据库中相应记录的数量
 * 2.判断数据库中某些字段的的数据是否正确
 */

public class PayRefundTest extends BaseTest {
	private String url = "http://rsm.58.com/pay/refund";
	private Map<String, String> header;
	private DBUtil db;

	@Override

	@BeforeClass
	public void beforeClass() {
		db = new DBUtil("M");// 初始化数据库
		db.connection();// 连接数据库
	}

	@Test(dataProviderClass = PayRefundProvider.class, dataProvider = "paySendAgainProvider")
	public void payRefundTest(String demandId, String amount, String remark, String typeid, String evidence, String msg)
			throws Exception {
		Logger.log("发起退款/退单接口 " + "测试开始=======发送Http请求： " + url, true);
		System.out.println("发起退款/退单");

		// 要用到的sql语句
		String sqlStrDiaryDebt = "select * from t_rs_demand_diary_debt where demand_id = ";
		String sqlStrAmountBook = "select * from t_rs_demand_amount_book where demand_id = ";
		String sqlStrStaff = "select * from t_rs_staff_optionlog where demand_id = ";
		String sqlStrOrder = "select * from t_rs_demand_order where demand_id = ";
		String sqlStrBusiness = "select * from t_rs_business where userid = ";
		String sqlStrSettlement = "select * from t_rs_settlement where demand_id = ";
		String sqlStrSettlementLog = "select * from t_rs_settlement_log where settlement_id = ";

		// 统计该demandId下，流水表t_rs_demand_diary_debt的数据大小
		List ddDemandListOrigin = returnResultSetList(sqlStrDiaryDebt, demandId);

		// 获取该demandId下，获取汇总表t_rs_demand_amount_book中的退款字段refund_amount的已退款金额
		List amountBookListOrigin = returnResultSetList(sqlStrAmountBook, demandId);
		Map<String, Object> amountBookMapOrigin = (Map) amountBookListOrigin.get(0);
		int discount = Integer.parseInt(amountBookMapOrigin.get("discount").toString());// 获取该demandId下的折扣
		int refund_amount_origin = Integer.parseInt(amountBookMapOrigin.get("refund_amount").toString());// 获取该demandId下的已退款金额
		int commisionOrigin = Integer.parseInt(amountBookMapOrigin.get("commision").toString());

		// 获取该demandId下，客服操作表t_rs_staff_optionlog下的记录信息
		List staffListOrigin = returnResultSetList(sqlStrStaff, demandId);

		Map<String, String> param = new HashMap<String, String>();
		param.put("demandId", demandId);
		param.put("amount", amount);
		param.put("remark", remark);
		param.put("typeid", typeid);
		param.put("evidence", evidence);
		param.put("NVrgLpVluBFisSw", "201702141355236f87c771");

		JSONObject responseObject = HttpRequest.doPostReturnResponseJson(url, param);
		System.out.println(responseObject);

		// 发起退款
		if (Integer.parseInt(typeid) == 1) {
			Object status = responseObject.get("status");
			Object message = responseObject.get("message");
			int statusInt = Integer.parseInt(status.toString());
			Assert.assertEquals("发起退款失败 " + message, 1, statusInt);

			// 获取发起退款之后的该demandId下的流水表t_rs_demand_diary_debt中的数据大小
			List ddDemandListAfter = returnResultSetList(sqlStrDiaryDebt, demandId);
			Map<String, Object> map = new HashMap<String, Object>();
			map = (Map) ddDemandListAfter.get(ddDemandListAfter.size() - 1);// 获取新创建的流水信息
			long diaryDebtId = Long.parseLong(map.get("id").toString());// 获取该数据下的流水id
			// 判断是否插入支付流水表成功
			Assert.assertEquals("插入支付流水失败", ddDemandListOrigin.size() + 1, ddDemandListAfter.size());

			// 发起退款成功
			if (statusInt == 1) {
				if (diaryDebtId > 0) {
					// 判断是否更新了退款数据

					// 如果退款订单未被商家接过单，无需更新commision佣金和商家金额，且其为0
					List amountBookListAfter = returnResultSetList(sqlStrAmountBook, demandId);
					Map<String, Object> amountBookMapAfter = (Map) amountBookListAfter.get(0);
					int refund_amount_After = Integer.parseInt(amountBookMapAfter.get("refund_amount").toString());// 获取发起退款之后该demandId下的已退款金额
					int commision = Integer.parseInt(amountBookMapAfter.get("commision").toString());
					int bisSettlement = Integer.parseInt(amountBookMapAfter.get("commision").toString());

					if ((commision == 0) || "NULL".equals(commision)) {
						Assert.assertEquals(msg + " 下汇总表更新退款数据失败 ",
								refund_amount_origin + (Integer.parseInt(amount)) * discount, refund_amount_After);

					} else {
						// 订单已被商家抢单，获取该demandId下的商家信息

						List demandOrderList = returnResultSetList(sqlStrOrder, demandId);
						Map<String, Object> mapOrder = new HashMap<String, Object>();
						mapOrder = (Map) demandOrderList.get(0);// 获取该demandId下的被抢单信息
						String businessUserId = mapOrder.get("business_user_id").toString();// 获取抢取该订单的商家id

						// 获取t_business表中该商家的抽成比
						List businessList = returnResultSetList(sqlStrBusiness, businessUserId);
						Map<String, Object> mapBusiness = new HashMap<String, Object>();
						mapBusiness = (Map) businessList.get(0);// 获取该demandId下的商家信息
						int ratio = Integer.parseInt(mapBusiness.get("ratio").toString());
						double commisionAfter = (Integer.parseInt(amount)) * ratio * 0.01;

						int commisionActual = 0;
						if ((commisionAfter % 1) == 0) {
							commisionActual = (int) commisionAfter;
						} else {
							commisionActual = (int) commisionAfter + 1;
						}
						// 判断58佣金是否更新为amount-商家金额比
						Assert.assertEquals(msg + "下汇总表更新退款数据失败", commision, (commisionOrigin - commisionActual));
					}
					// 获取发起退款之后，该demandId下客服操作表t_rs_staff_optionlog的记录信息
					List staffListAfter = returnResultSetList(sqlStrStaff, demandId);
					Assert.assertEquals("退款操作插入客服操作表失败", staffListOrigin.size() + 1, staffListAfter.size());

				}

			}

		}

		// 发起退单
		if (Integer.parseInt(typeid) == 2) {

			Object status = responseObject.get("status");
			Object message = responseObject.get("message");
			int statusInt = Integer.parseInt(status.toString());
			Assert.assertEquals("发起退单失败 " + message, 1, statusInt);

			// 获取发起退单之后的该demandId下的流水表t_rs_demand_diary_debt中的数据大小
			List ddDemandList = returnResultSetList(sqlStrDiaryDebt, demandId);
			Assert.assertEquals("插入退款流水表失败", ddDemandListOrigin.size() + 1, ddDemandList.size());

			// 如果退款流水插入成功
			if ((ddDemandListOrigin.size() + 1) == ddDemandList.size()) {
				// 查询t_rs_demand_order表，该订单是否已被抢单
				List orderList = returnResultSetList(sqlStrOrder, demandId);
				Map<String, Object> mapOrder = new HashMap<String, Object>();
				mapOrder = (Map) orderList.get(0);// 获取该demandId下的被抢单信息
				Long orderId = Long.parseLong(mapOrder.get("id").toString());

				// 获取发起退单后，汇总表的信息
				List bookListAfter = returnResultSetList(sqlStrAmountBook, demandId);
				Map<String, Object> mapAmountBook = new HashMap<String, Object>();
				mapAmountBook = (Map) bookListAfter.get(0);// 获取该demandId下的汇总表信息
				int bisSettlement = Integer.parseInt(mapAmountBook.get("bis_settlement").toString());
				int commision = Integer.parseInt(mapAmountBook.get("commision").toString());
				int transactionAmount = Integer.parseInt(mapAmountBook.get("transaction_amount").toString());
				int refuns_amount = Integer.parseInt(mapAmountBook.get("refund_amount").toString());

				if ("已被接单，退单金额等于实际支付金额".equals(msg)) {
					/*// 获取发起退单后，汇总表的信息
					List bookListAfter = returnResultSetList(sqlStrBook, demandId);
					Map<String, Object> mapAmountBook = new HashMap<String, Object>();
					mapAmountBook = (Map) bookListAfter.get(0);// 获取该demandId下的被抢单信息
					int bisSettlement = Integer.parseInt(mapAmountBook.get("bis_settlement").toString());
					int commision = Integer.parseInt(mapAmountBook.get("commision").toString());*/

					// 判断汇总表中商家剩余金额是否为0
					Assert.assertEquals("汇总表更新退单信息失败", 0, bisSettlement);
				}

				if ("已被接单，退单金额小于实际支付金额".equals(msg)) {
					// 判断退单剩余金额是否结算到商家余额中
					Assert.assertEquals("汇总表更新退单信息失败", transactionAmount, bisSettlement);

					// 如果订单已被接单，则发起商家结算 ，如果是服务完成后，发起退单，则应该重新结算，更新结算 表，否则直接创建结算订单
					// 获取该demandId下结算表的数据
					List settlementList = returnResultSetList(sqlStrSettlement, demandId);
					Map<String, Object> mapSettlement = new HashMap<String, Object>();
					mapSettlement = (Map) settlementList.get(0);// 获取该demandId下的结算表信息
					int amountSettlement = Integer.parseInt(mapSettlement.get("amount").toString());
					Assert.assertEquals("创建结算表失败 ", bisSettlement, amountSettlement);

					// 判断结算日志是否插入结算日志表中
					String settlementId = mapSettlement.get("id").toString();
					List settlementLogList = returnResultSetList(sqlStrSettlementLog, settlementId);
					Map<String, Object> mapSettlementLog = new HashMap<String, Object>();
					mapSettlementLog = (Map) settlementList.get(settlementLogList.size() - 1);
					String createTime = mapSettlementLog.get("create_time").toString().substring(0, 19);
					// 获取当前时间
					Date currentTime = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String nowTime = formatter.format(currentTime);
					Assert.assertEquals("创建结算订单日志录入失败！", nowTime, createTime);

				}

				// 获取退单后，客服操作表的信息
				List staffListAfter = returnResultSetList(sqlStrStaff, demandId);
				Assert.assertEquals("客服操作表更新退单信息失败", staffListOrigin.size() + 1, staffListAfter.size());
			}

		}

	}

	private List<Map> returnResultSetList(String str, String id) throws Exception {
		System.out.println("((((((" + "\"" + str + "\"" + id);
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;

		try {
			ps = db.prepareStatement(str + id);
			rs = ps.executeQuery();
			list = CommonUtil.resultSetToList(rs);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			ps.close();
			rs.close();
		}

	}

	@AfterClass
	public void afterClass() {
		if (db != null) {// 断开数据库连接
			db.closeConnection();
		}

	}
}
