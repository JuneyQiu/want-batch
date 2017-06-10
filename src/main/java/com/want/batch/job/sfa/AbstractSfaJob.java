package com.want.batch.job.sfa;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.sfa.pojo.SdActualDisplay;
import com.want.batch.job.sfa.pojo.SdActualPicture;
import com.want.batch.job.sfa.pojo.SdActualProd;
import com.want.batch.job.sfa.pojo.SpecialDisplayActual;
import com.want.batch.job.sfa.util.Constants;

public abstract class AbstractSfaJob extends AbstractWantJob {
	
	public class SpecialDisplayActualRowMapper implements RowMapper<SpecialDisplayActual>{
		@Override
		public SpecialDisplayActual mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			SpecialDisplayActual sda=new SpecialDisplayActual();
			
			if(rs.getLong("sid")==Constants.DEFAULT_LONG){
				sda.setSid(null);
			}else{
				sda.setSid(rs.getLong("sid"));
			}
			if(rs.getLong("application_sid")==Constants.DEFAULT_LONG){
				sda.setAppSid(null);
			}else{
				sda.setAppSid(rs.getLong("application_sid"));
			}
			sda.setSubmitCheckStatusXg(rs.getString("submit_check_status_xg"));
			sda.setSubmitCheckDateXg(rs.getTimestamp("submit_check_date_xg"));
			sda.setSubmitCheckStatusZr(rs.getString("submit_check_status_zr"));
			sda.setSubmitCheckDateZr(rs.getTimestamp("submit_check_date_zr"));
			sda.setSubmitCheckStatusSz(rs.getString("submit_check_status_sz"));
			sda.setSubmitCheckDateSz(rs.getTimestamp("submit_check_date_sz"));
			sda.setSubmitCheckStatusZj(rs.getString("submit_check_status_zj"));
			sda.setSubmitCheckDateZj(rs.getTimestamp("submit_check_date_zj"));
			sda.setCheckStatus(rs.getString("check_status"));
			sda.setFillInStatusYd(rs.getString("fill_in_status_yd"));
			sda.setFillInDateYd(rs.getTimestamp("fill_in_date_yd"));
			sda.setFillInStatusZr(rs.getString("fill_in_status_zr"));
			sda.setFillInDateZr(rs.getTimestamp("fill_in_date_zr"));
			sda.setFillInStatusKh(rs.getString("fill_in_status_kh"));
			sda.setFillInDateKh(rs.getTimestamp("fill_in_date_kh"));
			if(rs.getLong("submit_amount")==Constants.DEFAULT_LONG){
				sda.setSubmitAmount(null);
			}else{
				sda.setSubmitAmount(rs.getLong("submit_amount"));
			}
			sda.setDataSource(rs.getString("data_source"));
			
			return sda;
		}		
	}
	public class SdActualDisplayRowMapper implements RowMapper<SdActualDisplay>{
		@Override
		public SdActualDisplay mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			SdActualDisplay sad=new SdActualDisplay();
			
			if(rs.getLong("sid")==Constants.DEFAULT_LONG){
				sad.setSid(null);
			}else{
				sad.setSid(rs.getLong("sid"));
			}
			if(rs.getLong("actual_sid")==Constants.DEFAULT_LONG){
				sad.setActualSid(null);
			}else{
				sad.setActualSid(rs.getLong("actual_sid"));
			}
			if(rs.getLong("store_display_sid")==Constants.DEFAULT_LONG){
				sad.setStoreDisplaySid(null);
			}else{
				sad.setStoreDisplaySid(rs.getLong("store_display_sid"));
			}
			sad.setStoreId(rs.getString("store_id"));
			if(rs.getInt("display_type_sid")==Constants.DEFAULT_INT){
				sad.setDisplayTypeSid(null);
			}else{
				sad.setDisplayTypeSid(rs.getInt("display_type_sid"));
			}
			if(rs.getDouble("actual_input")==Constants.DEFAULT_DOUBLE){
				sad.setActualInput(null);
			}else{
				sad.setActualInput(rs.getDouble("actual_input"));
			}
			if(rs.getDouble("actual_sales")==Constants.DEFAULT_DOUBLE){
				sad.setActualSales(null);
			}else{
				sad.setActualSales(rs.getDouble("actual_sales"));
			}
			if(rs.getInt("location_type_sid")==Constants.DEFAULT_INT){
				sad.setLocationTypeSid(null);
			}else{
				sad.setLocationTypeSid(rs.getInt("location_type_sid"));
			}
			sad.setIsReceiveReceipt(rs.getString("is_receive_receipt"));
			sad.setCreateDate(rs.getTimestamp("create_date"));
			sad.setCreateUser(rs.getString("create_user"));
			sad.setCreateUserType(rs.getString("create_user_type"));
			if(rs.getLong("display_acreage")==Constants.DEFAULT_LONG){
				sad.setDisplayAcreage(null);
			}else{
				sad.setDisplayAcreage(rs.getLong("display_acreage"));
			}
			if(rs.getDouble("display_sidecount")==Constants.DEFAULT_DOUBLE){
				sad.setDisplaySideCount(null);
			}else{
				sad.setDisplaySideCount(rs.getDouble("display_sidecount"));
			}
			sad.setAssetsId(rs.getString("assets_id"));
			sad.setIsCancel(rs.getString("is_cancel"));
			sad.setSdCheckStatusZr(rs.getString("sd_check_status_zr"));
			sad.setSdCheckStatusSz(rs.getString("sd_check_status_sz"));
			sad.setSdCheckStatusZj(rs.getString("sd_check_status_zj"));
			if(rs.getDouble("approved_amount")==Constants.DEFAULT_DOUBLE){
				sad.setApprovedAmount(null);
			}else{
				sad.setApprovedAmount(rs.getDouble("approved_amount"));
			}
			sad.setIsLock(rs.getString("is_lock"));
			sad.setUpdateDate(rs.getTimestamp("update_date"));
			sad.setUpdateUser(rs.getString("update_user"));
			if(rs.getInt("display_param_id")==Constants.DEFAULT_INT){
				sad.setDisplayParamId(null);
			}else{
				sad.setDisplayParamId(rs.getInt("display_param_id"));
			}
			sad.setDataSource(rs.getString("data_source"));
			
			return sad;
		}
	}
	public class SdActualProdRowMapper implements RowMapper<SdActualProd>{
		@Override
		public SdActualProd mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			SdActualProd sap=new SdActualProd();
			
			sap.setSid(rs.getLong("sid"));
			sap.setActualDisplaySid(rs.getLong("actual_display_sid"));
			sap.setProductId(rs.getString("product_id"));
			sap.setDataSource(rs.getString("data_source"));
			
			return sap;
		}
	}
	public class SdActualPictureRowMapper implements RowMapper<SdActualPicture>{
		@Override
		public SdActualPicture mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			SdActualPicture pict=new SdActualPicture();
			
			pict.setActualDisplaySid(rs.getLong("actual_display_sid"));
			pict.setPictureId(rs.getString("picture_sid"));
			
			return pict;
		}
	}
}
