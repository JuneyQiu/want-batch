package com.want.batch.job.archive.notify.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.pojo.SdInfo;

public abstract class AbstractNotifyCommand implements NotifyCommand {
	protected final Log logger = LogFactory.getLog(this.getClass());

	@Autowired(required = false)
	@Qualifier("iCustomerJdbcOperations")
	private SimpleJdbcOperations iCustomerJdbcOperations;
	@Autowired(required = false)
	@Qualifier("portalJdbcOperations")
	private SimpleJdbcOperations portalJdbcOperations;
	
	protected SimpleJdbcOperations getiCustomerJdbcOperations() {
		return iCustomerJdbcOperations;
	}
	public SimpleJdbcOperations getPortalJdbcOperations() {
		return portalJdbcOperations;
	}

	@Override
	public void execute() {
		logger.info(this.getClass().getSimpleName()+":execute()----------start");
		List<NotifyBo> list=queryResults();
		logger.info(this.getClass().getSimpleName()+":execute()->results size="+list.size());
		for(int i=0;i<list.size();save(list.get(i++)));
		logger.info(this.getClass().getSimpleName()+":execute()----------end");
	}

	protected abstract List<NotifyBo> queryResults();
	
	protected long save(NotifyBo notifyBo){
		String seqSql=" select ALL_ABNORMALITY_INFO_TBL_SEQ.NEXTVAL from dual ";
		StringBuffer buf=new StringBuffer("");
		buf.append("INSERT INTO ALL_ABNORMALITY_INFO_TBL ")
			.append("(SID,ABNORMAL_TYPE,ABNORMAL_TYPE_DES,ABNORMAL_INFO,ZONGJINGLI_ID,ZONGJINGLI_NAME,COMPANY_ID,COMPANY_NAME")
			.append(",ZONGJIAN_ID,ZONGJIAN_NAME,ZHUANYUAN_ID,ZHUANYUAN_NAME,BRANCH_ID,BRANCH_NAME,SUOZHANG_ID,SUOZHANG_NAME")
			.append(",DIVISION_ID,DIVISION_NAME,ZHUREN_ID,ZHUREN_NAME,YEDAI_ID,YEDAI_NAME,CUSTOMER_ID,CREATE_DATE")
			.append(",IS_FIRST_TIME,START_YMD)    ")
			
			.append(" VALUES (?,?,?,?,?,?,?,?")
			.append(",?,?,?,?,?,?,?,?")
			.append(",?,?,?,?,?,?,?,sysdate")
			.append(",?,?)");

		long nextSeq=this.getiCustomerJdbcOperations().queryForLong(seqSql, new Object[]{});
		
		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{nextSeq,notifyBo.getExcType(),notifyBo.getExcDesc(),notifyBo.getExcInfo()
			,notifyBo.getZjlId(),notifyBo.getZjlName(),notifyBo.getCompanyId(),notifyBo.getCompanyName(),notifyBo.getZongjianId(),notifyBo.getZongjianName()
			,notifyBo.getZhuanyuanId(),notifyBo.getZhuanyuanName(),notifyBo.getBranchId(),notifyBo.getBranchName(),notifyBo.getSuozhangId()
			,notifyBo.getSuozhangName(),notifyBo.getDivisionId(),notifyBo.getDivisionName(),notifyBo.getZhurenId(),notifyBo.getZhurenName()
			,notifyBo.getYedaiId(),notifyBo.getYedaiName(),notifyBo.getCustId(),notifyBo.getIsFirst(),notifyBo.getStartYmd()});
		
		return nextSeq;
	}

	public class NotifyBoRowMapper implements RowMapper<NotifyBo>{
		public NotifyBo mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			NotifyBo notifyBo=new NotifyBo();
			
//			notifyBo.setSid(rs.getLong("sid"));
			notifyBo.setExcType(rs.getString("abnormality_type"));
			notifyBo.setExcDesc(rs.getString("abnormality_des"));
			notifyBo.setExcInfo(rs.getString("abnormality_info"));
			notifyBo.setZjlId(rs.getString("zjl_id"));
			notifyBo.setZjlName(rs.getString("zjl_name"));
			notifyBo.setCompanyId(rs.getString("company_id"));
			notifyBo.setCompanyName(rs.getString("company_name"));
			notifyBo.setZongjianId(rs.getString("zongjian_id"));
			notifyBo.setZongjianName(rs.getString("zongjian_name"));
			notifyBo.setZhuanyuanId(rs.getString("zhuanyuan_id"));
			notifyBo.setZhuanyuanName(rs.getString("zhuanyuan_name"));
			notifyBo.setBranchId(rs.getString("branch_id"));
			notifyBo.setBranchName(rs.getString("branch_name"));
			notifyBo.setSuozhangId(rs.getString("suozhang_id"));
			notifyBo.setSuozhangName(rs.getString("suozhang_name"));
			notifyBo.setDivisionId(rs.getString("division_id"));
			notifyBo.setDivisionName(rs.getString("division_name"));
			notifyBo.setZhurenId(rs.getString("zhuren_id"));
			notifyBo.setZhurenName(rs.getString("zhuren_name"));
			notifyBo.setYedaiId(rs.getString("yedai_id"));
			notifyBo.setYedaiName(rs.getString("yedai_name"));
			notifyBo.setCustId(rs.getString("customer_id"));
//			notifyBo.setCreateDate(rs.getTimestamp("create_date"));
			notifyBo.setIsFirst(rs.getString("is_first_time"));
			notifyBo.setStartYmd(rs.getString("start_ymd"));
			
			return notifyBo;
		}
	}
	public class SdInfoRowMapper implements RowMapper<SdInfo>{
		public SdInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			SdInfo sdInfo=new SdInfo();
			
			sdInfo.setSid(rs.getLong("sid"));
			sdInfo.setNotifyId(rs.getLong("notify_id"));
			sdInfo.setStoreDisplaySid(rs.getLong("store_display_sid"));
			sdInfo.setEmpId(rs.getString("emp_id"));
			sdInfo.setCreateUser(rs.getString("create_user"));
			sdInfo.setCreateDate(rs.getTimestamp("create_date"));
			
			return sdInfo;
		}
	}
}
