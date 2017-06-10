package com.want.batch.job.basedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.WantBatchException;
import com.want.batch.job.AbstractWantJob;
import com.want.utils.SapDAO;
/**
 * @author 00100979 从sap同步客户编码的数据
 */
@Component
public class SyncCustMark extends AbstractWantJob {
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	public SapDAO sapDAO;

	private Connection con = null;

	@Override
	public void execute() {
		logger.info("---start SyncCustMark----");
		try {
			con = this.getICustomerConnection();
			ArrayList prodlist = getStoreMarkFromSap();
			//if(prodlist.size()>0){
			this.createCustomerMark(prodlist);
			//}
		} catch (Exception e) {
			logger.error("数据库异常！", e);
			throw new WantBatchException(e);
		} finally {
			this.close(con);
		}
		logger.info("--end SyncCustMark--");
	}
	public ArrayList getStoreMarkFromSap() {
		HashMap querydata = new HashMap();
		ArrayList prodlist = new ArrayList();
		ArrayList list = sapDAO.getSAPData4("ZRFC22", "T_ZRFC22_OUT", querydata);
		
		logger.info("T_ZRFC22_OUT list:" + list.size());
		
		for (int i = 0; i < list.size(); i++) {
			HashMap temphm = (HashMap) list.get(i);
			String data[] = { (String) temphm.get("KUNNR"),
							  (String) temphm.get("VKORG"), 
							  (String) temphm.get("VTWEG"),
							  (String) temphm.get("SPART"), 
							  (String) temphm.get("KKBER"),
							  (String) temphm.get("MATNR"),
							  (String) temphm.get("ZSD_MARK_MARK") };

			 prodlist.add(data);
		}
		return prodlist;
	}
	private void createCustomerMark(ArrayList prodlist) {
		PreparedStatement pstmt = null;
		String sqlcmd1 = " delete from CUSTOMER_STORE_MARK ";
		String sqlcmd2 = "insert into CUSTOMER_STORE_MARK (CUSTOMER_ID,COMPANY_ID,CHANNEL_ID,PROD_GROUP_ID,CREDIT_ID,PRODUCT_ID,ISMARK,RECORD_DATE)"
				+ " values(?,?,?,?,?,?,?,to_char(sysdate,'yyyy-mm-dd'))";
		try {
			pstmt = con.prepareStatement(sqlcmd1);
			pstmt.execute();	
			pstmt = con.prepareStatement(sqlcmd2);	
			for (int i = 0; i < prodlist.size(); i++) {
				String data[] = (String[]) prodlist.get(i);
				pstmt.setString(1, data[0]);
				pstmt.setString(2, data[1]);
				pstmt.setString(3, data[2]);
				pstmt.setString(4, data[3]);
				pstmt.setString(5, data[4]);
				pstmt.setString(6, data[5]);
				pstmt.setString(7, data[6]);
				pstmt.execute();
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw new WantBatchException(sqle);
		} finally {
			this.close(con);
		}
	}
}
