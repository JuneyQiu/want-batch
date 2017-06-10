// ~ Package Declaration
// ==================================================

package com.want.batch.job.ghbatch.adapter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.utils.SapDAO;

// ~ Comments
// ==================================================

/**
 * 
 * 用于串接旺旺原系统中某些应用.
 * 
 * <pre>
 * 历史纪录：
 * 2010-4-15 Timothy
 * 	新建文件
 * </pre>
 * 
 * @author <pre>
 * SD
 * 	Timothy
 * PG
 * 
 * UT
 * 
 * MA
 * </pre>
 * @version $Rev$
 * 
 *          <p/>
 *          $Id$
 * 
 */
@Component
public class WantMisServiceAdapter {

	// ~ Static Fields
	// ==================================================

	/**
	 * Timothy, 2010-4-21 <br>
	 * 全部订单
	 */
	public static final String SAP_PO_TYPE_ALL = "1";

	/**
	 * Timothy, 2010-4-21 <br>
	 * 未出货订单
	 */
	public static final String SAP_PO_TYPE_UNDELIVER = "2";

	/**
	 * Timothy, 2010-4-21 <br>
	 * 出货但未确认
	 */
	public static final String SAP_PO_TYPE_UNCONFIRM = "3";

	/**
	 * Timothy, 2010-4-21 <br>
	 * 已确认订单
	 */
	public static final String SAP_PO_TYPE_CONFIRM = "4";

	// ~ Fields
	// ==================================================

	private Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SapDAO sapDAO;

	// ~ Constructors
	// ==================================================

	// ~ Methods
	// ==================================================

	/**
	 * <pre>
	 * 2010-4-15 Timothy
	 * 获取在途 SKU 库存
	 * </pre>
	 * 
	 * @param customerId
	 * @param companyId
	 * @param orderDataFrom
	 * @param orderDataTo
	 * @param poType
	 * 
	 * @return 所有查询到的sku对应其在途数量 sku id : sku count
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, BigDecimal> getAllSkusQty(	String customerId,
																								String companyId,
																								String orderDataFrom,
																								String orderDataTo,
																								String poType) throws Exception {

		Map<String, BigDecimal> results = new HashMap<String, BigDecimal>();

		// 1 标识根据日期范围查询，所以poNo参数为空
		String queryType = "1";
		String poNo = "";

		this.logger
			.debug(String
				.format("PoList(SAP) parameters >>> {customerId:%s, companyId:%s, queryType:%s, poNo:%s, orderDataFrom:%s, orderDataTo:%s, poType:%s}",
					customerId,
					companyId,
					queryType,
					poNo,
					orderDataFrom,
					orderDataTo,
					poType));
		
		HashMap querydata = new HashMap(7);
        querydata.put("ZKUNNR",customerId);//??????
        querydata.put("ZVKORG",companyId);//???ID
        querydata.put("ZTYPE",queryType);//????????
        querydata.put("ZVBELN1",poNo);//?????
        querydata.put("ZERDATF",new SimpleDateFormat("yyyy/MM/dd").parse(orderDataFrom));//???????????????
        querydata.put("ZERDATT",new SimpleDateFormat("yyyy/MM/dd").parse(orderDataTo));//???????????
        querydata.put("ZTYPE1",poType);
        
        List<Map<String, String>> pos = sapDAO.getSAPData("ZRFC05", "ZST05", querydata);

		for (Map<String, String> po : pos) {

			// 获取 [销售和分销凭证号]，用于查询订单明细
			HashMap<String, String> poDetailParameters = new HashMap<String, String>(1);
			poDetailParameters.put("ZVBELN", po.get("VBELN1"));
			
			// 2011-04-07 Deli add 客户编号
			poDetailParameters.put("ZKUNAG", customerId);
			
			List<Map<String, String>> poDetailes = sapDAO.getSAPData("ZRFC06", "ZST06", poDetailParameters);

			for (Map<String, String> poDetail : poDetailes) {

				String skuId = poDetail.get("MATNR");
				BigDecimal skuCount = new BigDecimal(poDetail.get("KLMENG"));

				// 当结果集中已经存在相同的sku时，做累加
				if (results.containsKey(skuId)) {
					skuCount = skuCount.add(results.get(skuId));
				}

				results.put(skuId, skuCount);
			}
		}

		this.logger.debug(String.format("[%s]All Sku's count mapping >>> [%s]", customerId, results));

		return results;
	}
}
