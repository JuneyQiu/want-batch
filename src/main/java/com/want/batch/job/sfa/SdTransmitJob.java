package com.want.batch.job.sfa;

import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.want.batch.job.sfa.pojo.SdActualDisplay;
import com.want.batch.job.sfa.pojo.SdActualPicture;
import com.want.batch.job.sfa.pojo.SdActualProd;
import com.want.batch.job.sfa.pojo.SpecialDisplayActual;
import com.want.batch.job.sfa.util.Constants;
import com.want.batch.job.utils.Toolkit;

/**从sfa转移数据至icustomer下的special_display_actual,sd_actual_display,sd_actual_prod,sd_actual_picture
 * @author 00078588
 * 
 */
@Component
@Transactional(readOnly=true)
public class SdTransmitJob extends AbstractSfaJob{
	
	@Override
	public void execute(){
		// TODO Auto-generated method stub
		long startTimeMillis=System.currentTimeMillis();
		logger.info("SdTransmitJob:execute() start.");
		//从源表(special_display_actual_42)中查出指定年月的特陈主表数据
		Calendar c=Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);//每天凌晨跑前一天的数据
		List<SpecialDisplayActual> sdaList=querySpecialDisplayActualFromSfa(Toolkit.dateToString(c.getTime(), "yyyyMM")
				,Toolkit.dateToString(c.getTime(), "yyyy-MM-dd"));
		logger.info("The length of SpecialDisplayActual's list is:"+sdaList.size());
		for(SpecialDisplayActual sda:sdaList){
			transfer(sda);
		}
		logger.info("SdTransmitJob:execute() end.");
		long endTimeMillis=System.currentTimeMillis();
		logger.info("SdTransmitJob:execute()---------耗时："+Toolkit.timeTransfer(endTimeMillis-startTimeMillis));
	}
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void transfer(SpecialDisplayActual sda){
		//检查表special_display_actual中是否有相同记录
		SpecialDisplayActual oldSda=this.getSpecialDisplayActualByAppSid(sda.getAppSid());
		if(oldSda==null){
			//special_display_actual中没有相同记录，则新增数据
			this.saveSpecialDisplayActual(sda);
			for(SdActualDisplay sad:sda.getSadList()){
				//更新SdActualDisplay的actualSid属性
				sad.setActualSid(sda.getSid());
				//往表sd_actual_display中新增数据
				this.saveSdActualDisplay(sad);
				saveProdAndPicture(sad);
			}
		}else{	
			
			// 2013-01-29 wonci 添加更新181DB中特陈填写资料的判断,当181DB中，业代提交状态非已提交时，更新181Db资料，否则不更新
			if (!"1".equals(oldSda.getFillInStatusYd())) {
				
				//special_display_actual中有相同记录，则更新数据
				sda.setSid(oldSda.getSid());
				
				// wonci 2013-01-29 修改业代回传的提交状态和dateSource
				oldSda.setFillInStatusYd(sda.getFillInStatusYd());
				oldSda.setDataSource(sda.getDataSource());
				
				//更新目标表special_display_actual的数据
				this.updateSpecialDisplayActualBySid(oldSda);
				for(SdActualDisplay sad:sda.getSadList()){
					//更新SdActualDisplay的actualSid属性
					sad.setActualSid(sda.getSid());
					//检查表sd_actual_display中是否有相同记录
					SdActualDisplay oldSad=this.getSdActualDisplayByActualSidAndStoreDisplaySidAndCreateUserType(sad.getActualSid()
							,sad.getStoreDisplaySid(),sad.getCreateUserType());
					if(oldSad==null){
						//sd_actual_display中没有相同记录，新增
						this.saveSdActualDisplay(sad);
						saveProdAndPicture(sad);
					}else{
						//sd_actual_display中有相同记录，更新
						sad.setSid(oldSad.getSid());
						//更新目标表sd_actual_display中的数据
						this.updateSdActualDisplayBySid(sad);
						//删除目标表sd_actual_prod中对应的所有数据
						this.deleteSdActualProdByActualDisplaySid(sad.getSid());
						//删除目标表sd_actual_prod中对应的所有数据
						this.deleteSdActualPictureByActualDisplaySid(sad.getSid());
						saveProdAndPicture(sad);
					}
				}
			}
		}
	}
	/**保存指定特陈记录的所有品项和照片
	 * @param sad
	 */
	private void saveProdAndPicture(SdActualDisplay sad){
		for(SdActualProd sap:sad.getSapList()){
			//更新SdActualProd的actualDisplaySid属性
			sap.setActualDisplaySid(sad.getSid());
			//往表sd_actual_prod中新增数据
			this.saveSdActualProd(sap);
		}
		for(SdActualPicture sapict:sad.getSapictList()){
			//更新SdActualPicture的actualDisplaySid属性
			sapict.setActualDisplaySid(sad.getSid());
			//往表sd_actual_picture中新增数据
			this.saveSdActualPicture(sapict);
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<SpecialDisplayActual> querySpecialDisplayActualFromSfa(String yearMonth,String day){
		StringBuffer buf=new StringBuffer("");
		buf.append(" select nvl(c.web_sid,-99999999) as sid")
			.append(",nvl(a.application_sid,-99999999) as application_sid")
			.append(",a.submit_check_status_xg")
			.append(",a.submit_check_date_xg")
			.append(",a.submit_check_status_zr")
			.append(",a.submit_check_date_zr")
			.append(",a.submit_check_status_sz")
			.append(",a.submit_check_date_sz")
			.append(",a.submit_check_status_zj")
			.append(",a.submit_check_date_zj")
			.append(",a.check_status")
			.append(",a.fill_in_status_yd")
			.append(",a.fill_in_date_yd")
			.append(",a.fill_in_status_zr")
			.append(",a.fill_in_date_zr")
			.append(",a.fill_in_status_kh")
			.append(",a.fill_in_date_kh")
			.append(",nvl(a.submit_amount,-99999999) as submit_amount")
			.append(",? as data_source   ")
			
			.append(" from special_display_actual_42 a ")
			.append(" inner join special_display_application b on a.application_sid=b.sid ")
			.append(" inner join sd_actual_seq_temp c on a.sid=c.sfa_sid ")
			
			.append(" where b.year_month=? and to_char(a.update_date,'yyyy-mm-dd')=? ");
		
		List<SpecialDisplayActual> sdaList=this.getiCustomerJdbcOperations().query(buf.toString()
					, new SpecialDisplayActualRowMapper(),new Object[]{Constants.DATA_SOURCE,yearMonth,day});
		for(SpecialDisplayActual sda:sdaList){
			sda.setSadList(querySdActualDisplayByActualSidFromSfa(sda.getSid()));
		}
		return sdaList;
	}
	public List<SdActualDisplay> querySdActualDisplayByActualSidFromSfa(long actualSid){
		StringBuffer buf=new StringBuffer("");
		buf.append("select nvl(b.web_sid,-99999999) as sid")
			.append(",nvl(c.web_sid,-99999999) as actual_sid")
			.append(",nvl(a.store_display_sid,-99999999) as store_display_sid")
			.append(",a.store_id")
			.append(",nvl(a.display_type_sid,-99999999) as display_type_sid")
			.append(",nvl(a.actual_input,-99999999) as actual_input")
			.append(",nvl(a.actual_sales,-99999999) as actual_sales")
			.append(",nvl(a.location_type_sid,-99999999) as location_type_sid")
			.append(",a.is_receive_receipt")
			.append(",a.create_date")
			.append(",a.create_user")
			.append(",a.create_user_type")
			.append(",nvl(a.display_acreage,-99999999) as display_acreage ")
			.append(",nvl(a.display_sidecount,-99999999) as display_sidecount")
			.append(",a.assets_id")
			.append(",a.is_cancel")
			.append(",a.sd_check_status_zr")
			.append(",a.sd_check_status_sz")
			.append(",a.sd_check_status_zj")
			.append(",nvl(a.approved_amount,-99999999) as approved_amount")
			.append(",a.is_lock")
			.append(",a.update_date")
			.append(",a.update_user")
			.append(",nvl(a.display_param_id,-99999999) as display_param_id")
			.append(",? as data_source   ")
			
			.append(" from sd_actual_display_42 a ")
			.append(" inner join sd_ad_seq_temp b on a.sid=b.sfa_sid ")
			.append(" inner join sd_actual_seq_temp c on a.actual_sid=c.sfa_sid ")
			
			.append(" where c.web_sid=? ");
		
		List<SdActualDisplay> sadList=this.getiCustomerJdbcOperations().query(buf.toString()
				, new SdActualDisplayRowMapper(),new Object[]{Constants.DATA_SOURCE,actualSid});
		for(SdActualDisplay sad:sadList){
			sad.setSapList(querySdActualProdByActualDisplaySidFromSfa(sad.getSid()));
			sad.setSapictList(querySdActualPictureByActualDisplaySidFromSfa(sad.getSid()));
		}
		return sadList;
	}
	public List<SdActualProd> querySdActualProdByActualDisplaySidFromSfa(long actualDisplaySid){
		StringBuffer buf=new StringBuffer("");
		buf.append("select b.web_sid as sid")
			.append(",c.web_sid as actual_display_sid")
			.append(",a.product_id")
			.append(",? as data_source   ")
			
			.append(" from sd_actual_prod_42 a ")
			.append(" inner join sd_ap_seq_temp b on a.sid=b.sfa_sid ")
			.append(" inner join sd_ad_seq_temp c on a.actual_display_sid=c.sfa_sid    ")
			
			.append(" where c.web_sid=?");
		
		return this.getiCustomerJdbcOperations().query(buf.toString(), new SdActualProdRowMapper()
						,new Object[]{Constants.DATA_SOURCE,actualDisplaySid});
	}
	public List<SdActualPicture> querySdActualPictureByActualDisplaySidFromSfa(long actualDisplaySid){
		StringBuffer buf=new StringBuffer("");
		buf.append("select b.web_sid as actual_display_sid")
			.append(",a.picture_sid ")
			.append(" from sd_actual_picture_42 a ")
			.append(" inner join sd_ad_seq_temp b on a.actual_display_sid=b.sfa_sid ")
			.append(" where b.web_sid=?");
		
		return this.getiCustomerJdbcOperations().query(buf.toString(), new SdActualPictureRowMapper()
						,new Object[]{actualDisplaySid});
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void deleteSpecialDisplayActualBySid(long sid){
		String sql=" delete from special_display_actual where sid=? ";
		this.getiCustomerJdbcOperations().update(sql, new Object[]{sid});
	}
	public void deleteSdActualDisplayBySid(long sid){
		String sql=" delete from sd_actual_display where sid=? ";
		this.getiCustomerJdbcOperations().update(sql, new Object[]{sid});
	}
	public void deleteSdActualProdBySid(long sid){
		String sql=" delete from sd_actual_prod where sid=? ";
		this.getiCustomerJdbcOperations().update(sql, new Object[]{sid});
	}
	public void deleteSdActualProdByActualDisplaySid(long actualDisplaySid){
		String sql=" delete from sd_actual_prod where actual_display_sid=? ";
		this.getiCustomerJdbcOperations().update(sql, new Object[]{actualDisplaySid});
	}
	public void deleteSdActualPictureByActualDisplaySid(long actualDisplaySid){
		String sql=" delete from sd_actual_picture where actual_display_sid=? ";
		this.getiCustomerJdbcOperations().update(sql, new Object[]{actualDisplaySid});
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void updateSpecialDisplayActualBySid(SpecialDisplayActual sda){
		StringBuffer buf=new StringBuffer("");
		buf.append("update special_display_actual ")
			.append(" set application_sid=? ")
			.append("    ,submit_check_status_xg=?")
			.append("    ,submit_check_date_xg=?")
			.append("    ,submit_check_status_zr=?")
			.append("    ,submit_check_date_zr=?")
			.append("    ,submit_check_status_sz=?")
			.append("    ,submit_check_date_sz=?")
			.append("    ,submit_check_status_zj=?")
			.append("    ,submit_check_date_zj=?")
			.append("    ,check_status=?")
			.append("    ,fill_in_status_yd=?")
			.append("    ,fill_in_date_yd=?")
			.append("    ,fill_in_status_zr=?")
			.append("    ,fill_in_date_zr=?")
			.append("    ,fill_in_status_kh=?")
			.append("    ,fill_in_date_kh=?")
			.append("    ,submit_amount=?")
			.append("    ,data_source=?  ")
			.append(" where sid=? ");
		
		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{sda.getAppSid()
			,sda.getSubmitCheckStatusXg(),sda.getSubmitCheckDateXg(),sda.getSubmitCheckStatusZr(),sda.getSubmitCheckDateZr()
			,sda.getSubmitCheckStatusSz(),sda.getSubmitCheckDateSz(),sda.getSubmitCheckStatusZj(),sda.getSubmitCheckDateZj()
			,sda.getCheckStatus(),sda.getFillInStatusYd(),sda.getFillInDateYd(),sda.getFillInStatusZr()
			,sda.getFillInDateZr(),sda.getFillInStatusKh(),sda.getFillInDateKh(),sda.getSubmitAmount()
			,sda.getDataSource(),sda.getSid()});
	}
	public void updateSdActualDisplayBySid(SdActualDisplay sad){
		StringBuffer buf=new StringBuffer("");
		buf.append("update sd_actual_display  ")
			.append(" set actual_sid=? ")
			.append("    ,store_display_sid=? ")
			.append("    ,store_id=? ")
			.append("    ,display_type_sid=? ")
			.append("    ,actual_input=? ")
			.append("    ,actual_sales=? ")
			.append("    ,location_type_sid=? ")
			.append("    ,is_receive_receipt=? ")
			.append("    ,create_date=? ")
			.append("    ,create_user=? ")
			.append("    ,create_user_type=? ")
			.append("    ,display_acreage=? ")
			.append("    ,display_sidecount=? ")
			.append("    ,assets_id=? ")
			.append("    ,is_cancel=? ")
			.append("    ,sd_check_status_zr=? ")
			.append("    ,sd_check_status_sz=? ")
			.append("    ,sd_check_status_zj=? ")
			.append("    ,approved_amount=? ")
			.append("    ,is_lock=? ")
			.append("    ,update_date=? ")
			.append("    ,update_user=? ")
			.append("    ,display_param_id=?")
			.append("    ,data_source=?  ")
			.append(" where sid=? ");
		
		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{sad.getActualSid()
			,sad.getStoreDisplaySid(),sad.getStoreId(),sad.getDisplayTypeSid(),sad.getActualInput(),sad.getActualSales()
			,sad.getLocationTypeSid(),sad.getIsReceiveReceipt(),sad.getCreateDate(),sad.getCreateUser()
			,sad.getCreateUserType(),sad.getDisplayAcreage(),sad.getDisplaySideCount(),sad.getAssetsId()
			,sad.getIsCancel(),sad.getSdCheckStatusZr(),sad.getSdCheckStatusSz(),sad.getSdCheckStatusZj()
			,sad.getApprovedAmount(),sad.getIsLock(),sad.getUpdateDate(),sad.getUpdateUser(),sad.getDisplayParamId()
			,sad.getDataSource(),sad.getSid()});
	}
	public void updateSdActualProd(SdActualProd sap){
		StringBuffer buf=new StringBuffer("");
		buf.append("update sd_actual_prod  ")
			.append(" set actual_display_sid=? ")
			.append("    ,product_id=?")
			.append("    ,data_source=?  ")
			.append(" where sid=? ");

		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{sap.getActualDisplaySid()
						,sap.getProductId(),sap.getDataSource(),sap.getSid()});
	}
	/**批量更新实际特陈照片表中的实际特陈编号
	 * @param oldActualDisplaySid
	 * @param newActualDisplaySid
	 */
	public void updateSdActualPictureDisplaySid(long oldActualDisplaySid,long newActualDisplaySid){
		String sql=" update sd_actual_picture set actual_display_sid=? where actual_display_sid=? ";
		this.getiCustomerJdbcOperations().update(sql, new Object[]{newActualDisplaySid,oldActualDisplaySid});
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**保存数据至表special_display_actual，返回新产生的sid
	 * @param sda
	 * @return
	 */
	public long saveSpecialDisplayActual(SpecialDisplayActual sda){
		String seqSql="select special_display_actual_seq.nextval as new_sid from dual";
		long sid=this.getiCustomerJdbcOperations().queryForLong(seqSql);
		
		sda.setSid(sid);
		StringBuffer buf=new StringBuffer("");
		buf.append("insert into special_display_actual(sid,application_sid,submit_check_status_xg,submit_check_date_xg")
			.append(",submit_check_status_zr,submit_check_date_zr,submit_check_status_sz,submit_check_date_sz")
			.append(",submit_check_status_zj,submit_check_date_zj,check_status,fill_in_status_yd,fill_in_date_yd")
			.append(",fill_in_status_zr,fill_in_date_zr,fill_in_status_kh,fill_in_date_kh,submit_amount,data_source)   ")
			.append("  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{sda.getSid(),sda.getAppSid()
			,sda.getSubmitCheckStatusXg(),sda.getSubmitCheckDateXg(),sda.getSubmitCheckStatusZr(),sda.getSubmitCheckDateZr()
			,sda.getSubmitCheckStatusSz(),sda.getSubmitCheckDateSz(),sda.getSubmitCheckStatusZj(),sda.getSubmitCheckDateZj()
			,sda.getCheckStatus(),sda.getFillInStatusYd(),sda.getFillInDateYd(),sda.getFillInStatusZr()
			,sda.getFillInDateZr(),sda.getFillInStatusKh(),sda.getFillInDateKh(),sda.getSubmitAmount(),sda.getDataSource()});
		
		return sda.getSid();
	}
	/**保存数据至表sd_actual_display，返回新产生的sid
	 * @param sad
	 * @return
	 */
	public long saveSdActualDisplay(SdActualDisplay sad){
		String seqSql="select sd_actual_display_seq.nextval as new_sid from dual";
		long sid=this.getiCustomerJdbcOperations().queryForLong(seqSql);
		
		sad.setSid(sid);
		StringBuffer buf=new StringBuffer("");
		buf.append("insert into sd_actual_display(sid,actual_sid,store_display_sid,store_id,display_type_sid")
			.append(",actual_input,actual_sales,location_type_sid,is_receive_receipt,create_date,create_user")
			.append(",create_user_type,display_acreage,display_sidecount,assets_id,is_cancel,sd_check_status_zr")
			.append(",sd_check_status_sz,sd_check_status_zj,approved_amount,is_lock,update_date,update_user")
			.append(",display_param_id,data_source)   ")
			.append("  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{sad.getSid(),sad.getActualSid()
			,sad.getStoreDisplaySid(),sad.getStoreId(),sad.getDisplayTypeSid(),sad.getActualInput(),sad.getActualSales()
			,sad.getLocationTypeSid(),sad.getIsReceiveReceipt(),sad.getCreateDate(),sad.getCreateUser()
			,sad.getCreateUserType(),sad.getDisplayAcreage(),sad.getDisplaySideCount(),sad.getAssetsId()
			,sad.getIsCancel(),sad.getSdCheckStatusZr(),sad.getSdCheckStatusSz(),sad.getSdCheckStatusZj()
			,sad.getApprovedAmount(),sad.getIsLock(),sad.getUpdateDate(),sad.getUpdateUser()
			,sad.getDisplayParamId(),sad.getDataSource()});
		
		return sad.getSid();
	}
	/**保存数据至表sd_actual_prod，返回新产生的sid
	 * @param sap
	 * @return
	 */
	public long saveSdActualProd(SdActualProd sap){
		String seqSql="select sd_actual_prod_seq.nextval as new_sid from dual";
		long sid=this.getiCustomerJdbcOperations().queryForLong(seqSql);
		
		sap.setSid(sid);
		StringBuffer buf=new StringBuffer("");
		buf.append("insert into sd_actual_prod(sid,actual_display_sid,product_id,data_source)   ")
			.append(" values(?,?,?,?) ");
		
		this.getiCustomerJdbcOperations().update(buf.toString(), new Object[]{sap.getSid()
				,sap.getActualDisplaySid(),sap.getProductId(),sap.getDataSource()});
		
		return sap.getSid();
	}
	public void saveSdActualPicture(SdActualPicture sapict){
		String sql=" insert into sd_actual_picture(actual_display_sid,picture_sid) values(?,?) ";
		this.getiCustomerJdbcOperations().update(sql, new Object[]{sapict.getActualDisplaySid(),sapict.getPictureId()});
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**在目标表(special_display_actual)中根据申请单号(application_sid)查找实际
	 *  在表special_display_actual中，特陈申请单号(application_sid)是唯一的
	 * @param appSid
	 * @return
	 */
	public SpecialDisplayActual getSpecialDisplayActualByAppSid(long appSid){
		String sql=" select * from special_display_actual a where a.application_sid=? ";
		
		List<SpecialDisplayActual> list=this.getiCustomerJdbcOperations().query(sql
				, new SpecialDisplayActualRowMapper(), new Object[]{appSid});
		
		return (list==null||list.size()<=0)?null:list.get(0);
	}
	/**在目标表(sd_actual_display)中根据       特陈实际主表SID(actual_sid)+计划申请终端特陈SID(store_display_sid)+填写人员类型SID(create_user_type)   查找实际特陈
	 * 在表sd_actual_display中，actual_sid+store_display_sid+create_user_type为组合主键
	 * @param actualSid
	 * @param storeDisplaySid
	 * @param createUserType
	 * @return
	 */
	public SdActualDisplay getSdActualDisplayByActualSidAndStoreDisplaySidAndCreateUserType(
			long actualSid,long storeDisplaySid,String createUserType){
		String sql="select * from sd_actual_display a where a.actual_sid=? and a.store_display_sid=? and a.create_user_type=? ";
		
		List<SdActualDisplay> list=this.getiCustomerJdbcOperations().query(sql
				, new SdActualDisplayRowMapper(), new Object[]{actualSid,storeDisplaySid,createUserType});
		
		return (list==null||list.size()<=0)?null:list.get(0);
	}
	/**在目标表(sd_actual_prod)中根据  实际特陈SID(actual_display_sid) 查找特陈实际品项
	 * @param actualDisplaySid
	 * @return
	 */
	public List<SdActualProd> getSdActualProdListByActualDisplaySid(long actualDisplaySid){
		String sql=" select * from sd_actual_prod a where a.actual_display_sid=? ";
		
		return this.getiCustomerJdbcOperations().query(sql, new SdActualProdRowMapper(), new Object[]{actualDisplaySid});
	}
	/**在目标表(sd_actual_picture)中根据 实际特陈SID(actual_display_sid) 查询实际特陈的照片
	 * @param actualDisplaySid
	 * @return
	 */
	public List<SdActualPicture> getSdActualPictureListByActualDisplaySid(long actualDisplaySid){
		String sql=" select * from sd_actual_picture a where a.actual_display_sid=? ";
		return this.getiCustomerJdbcOperations().query(sql, new SdActualPictureRowMapper(), new Object[]{actualDisplaySid});
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
