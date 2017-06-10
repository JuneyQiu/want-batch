package com.want.batch.job.archive.notify.jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.want.batch.job.archive.notify.pojo.MessageBo;
import com.want.batch.job.archive.notify.pojo.NotifyBo;
import com.want.batch.job.archive.notify.pojo.NotifyCategoryNum;
import com.want.batch.job.archive.notify.util.Constant;

/**收集短信存入portal.sms_tbl
 * @author 00078588
 *
 */
@Component
public class MessageCommand extends AbstractNotifyCommand {

	@Override
	protected List<NotifyBo> queryResults() {
		// TODO Auto-generated method stub
		return null;
	}
	public void execute() {
		deleteNotifyDataByDivId(14);//发送短信前，删除通路的数据
		logger.info(this.getClass().getSimpleName()+":execute()----------Start to gather messages.");
		logger.info("收集所有主任的短信......");
		//主任
		saveMessage(getMessageBoListByUserTypeSid(Constant.USER_TYPE_SID_ZR));
		logger.info("收集所有所长的短信......");
		//所长
		saveMessage(getMessageBoListByUserTypeSid(Constant.USER_TYPE_SID_SZ));
		logger.info("收集所有专员的短信......");
		//专员
		saveMessage(getMessageBoListByUserTypeSid(Constant.USER_TYPE_SID_ZY));
		logger.info("收集所有总监的短信......");
		//总监
		saveMessage(getMessageBoListByUserTypeSid(Constant.USER_TYPE_SID_ZJ));
		logger.info("收集所有事业部总经理或副总的短信......");
		//总经理
		saveMessage(getMessageBoListOfZjlGroupByDivision());
		logger.info(this.getClass().getSimpleName()+":execute()----------End to gather messages.");
	}
	private void saveMessage(List<MessageBo> messageList){
		if(messageList==null) return;
		logger.info(" messageList.size()="+messageList.size());
		for(MessageBo bo:messageList){
			saveMessage(bo);
		}
	}
	private void saveMessage(MessageBo messageBo){
		StringBuffer buf=new StringBuffer("");
		buf.append(" insert into sms_tbl (sid,func_sid,func_case_id,create_time,update_time,send_time ")
			.append("  ,phone_number,content,status,log) ")
			.append("  values (sms_sid_seq.nextval,5,?,sysdate,null,sysdate+2/24,?,?,1,null) ");

		this.getPortalJdbcOperations().update(buf.toString(),new Object[]{messageBo.getEmpId()
			,messageBo.getMobile(),messageBo.getMessageInfo()});
	}
	/**考虑到不同事业部的总经理可能是同一个人，但现在短信却要发给事业部副总，
	 * 因此原本发给总经理一个人的短信现在可能要分发给多个不同的事业部副总。
	 * division_vice_leader_tbl表中维护了需要接收短信的各事业部副总工号，如果某个事业部没有维护接收短信的人，
	 * 则按原来的规则，默认发送给总经理
	 * @return
	 */
	private List<MessageBo> getMessageBoListOfZjlGroupByDivision(){
		StringBuffer buf=new StringBuffer("");
		buf.append(" select a.emp_id")
			.append(",a.division_id")
			.append(",a.absum")
			.append(",substr(he.emp_mobile,1,11) as emp_mobile ")
			
			.append(" from ")
			.append(" ( ")
			.append("  		select decode(v.vice_leader_id,null,aait.zongjingli_id,v.vice_leader_id) as emp_id")
			.append("       ,aait.division_id ")
			.append("		,count(distinct customer_id) as absum  ")
			.append("  		from all_abnormality_info_tbl aait  ")
			.append("  		inner join division_usertype_abtype_cfg cfg on aait.abnormal_type=cfg.abnormal_type_id  ")
			.append("               							   		and aait.division_id=cfg.division_id and cfg.user_type_id=14  ")
			.append("       left join division_vice_leader_tbl v on aait.division_id=v.division_id ")
			.append("  		where aait.create_date<trunc(sysdate)+1/2 and aait.create_date>trunc(sysdate-1)+1/2 ")
			.append("  		group by decode(v.vice_leader_id,null,aait.zongjingli_id,v.vice_leader_id),aait.division_id ")
			.append(" ) a  ")
			.append(" inner join hrorg.emp he on he.emp_id=a.emp_id ");
		
		List<MessageBo> msgBoList=this.getiCustomerJdbcOperations().query(buf.toString(), new MessageBoRowMapper(), new Object[]{});
		for(MessageBo msgBo:msgBoList){
			msgBo.setNotifyCategoryNumList(getNotifyNumsByEmpDivision(msgBo.getEmpId(),msgBo.getDivisionId()));
		}
		return msgBoList;
	}
	private List<NotifyCategoryNum> getNotifyNumsByEmpDivision(String empId,int divisionId){
		StringBuffer buf=new StringBuffer("");
		buf.append("select e.sid as category_sid,e.name as category_name,count(distinct a.customer_id) as category_sid_num  ")
			.append(" from all_abnormality_info_tbl a ")
			.append(" inner join division_usertype_abtype_cfg b on a.abnormal_type=b.abnormal_type_id ")
			.append("           and a.division_id=b.division_id and b.user_type_id=14 ")
			.append(" left join division_vice_leader_tbl c on a.division_id=c.division_id ")
			.append(" inner join notify_info_tbl d on a.abnormal_type=d.id ")
			.append(" inner join notify_category_info_tbl e on d.category_sid=e.sid ")
			.append(" where a.create_date<trunc(sysdate)+1/2 and a.create_date>trunc(sysdate-1)+1/2 ")
			.append(" and decode(c.vice_leader_id,null,a.zongjingli_id,c.vice_leader_id)=? and a.division_id=?  ")
			.append(" group by e.sid,e.name ");
		
		return this.getiCustomerJdbcOperations().query(buf.toString(), new NotifyCategoryNumRowMapper()
			, new Object[]{empId,divisionId});
	}
	/**查询指定类型(user_type_sid)的用户的工号、电话号码和异常个数
	 * @param userTypeSid
	 * @return
	 */
	private List<MessageBo> getMessageBoListByUserTypeSid(int userTypeSid){
		String columnName=getColumnName(userTypeSid);
		StringBuffer buf=new StringBuffer("");
		buf.append(" select a.emp_id ")
			.append(",0 as division_id")
			.append(",a.absum")
			.append(",substr(he.emp_mobile,1,11) as emp_mobile ")
			
			.append(" from ")
			.append(" ( ")
			.append("  		select aait.").append(columnName).append(" as emp_id")
			.append("		,count(distinct customer_id) as absum  ")
			.append("  		from all_abnormality_info_tbl aait  ")
			.append("  		inner join division_usertype_abtype_cfg cfg on aait.abnormal_type=cfg.abnormal_type_id  ")
			.append("               							   		and aait.division_id=cfg.division_id and cfg.user_type_id=?  ")
			.append("  		where aait.create_date<trunc(sysdate)+1/2 ")
			.append("             and aait.create_date>trunc(sysdate-1)+1/2 ")
			.append("  			  and aait.").append(columnName).append(" is not null ")
			.append("  			  and aait.").append(columnName).append(" !='null' ")
			.append("  		group by aait.").append(columnName)
			.append(" ) a  ")
			.append(" inner join hrorg.emp he on he.emp_id=a.emp_id ");
		
		List<MessageBo> msgBoList=this.getiCustomerJdbcOperations().query(buf.toString(), new MessageBoRowMapper()
						, new Object[]{userTypeSid});
		for(MessageBo msgBo:msgBoList){
			msgBo.setNotifyCategoryNumList(getNotifyNumsByEmp(msgBo.getEmpId(),userTypeSid,columnName));
		}
		return msgBoList;
	}
	/**
	 * @param empId 接收短信的用户工号 
	 * @param userTypeSid
	 * @param columnName  与表all_abnormality_info_tbl表的字段进行匹配。。。。。好郁闷
	 * @return
	 */
	private List<NotifyCategoryNum> getNotifyNumsByEmp(String empId,int userTypeSid,String columnName){
		StringBuffer buf=new StringBuffer("");
		buf.append("select d.sid as category_sid,d.name as category_name,count(distinct a.customer_id) as category_sid_num   ")
			.append(" from all_abnormality_info_tbl a ")
			.append(" inner join division_usertype_abtype_cfg b on a.abnormal_type=b.abnormal_type_id ")
			.append("                 and a.division_id=b.division_id and b.user_type_id=?  ") 
			.append(" inner join notify_info_tbl c on a.abnormal_type=c.id ")
			.append(" inner join notify_category_info_tbl d on c.category_sid=d.sid ")
			.append(" where a.create_date<trunc(sysdate)+1/2 and a.create_date>trunc(sysdate-1)+1/2 ")
			.append(" and a.").append(columnName).append("=?  ")
			.append(" group by d.sid,d.name ");
		
		return this.getiCustomerJdbcOperations().query(buf.toString(), new NotifyCategoryNumRowMapper()
			, new Object[]{userTypeSid,empId});
	}
	public class MessageBoRowMapper implements RowMapper<MessageBo>{
		@Override
		public MessageBo mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			MessageBo messageBo=new MessageBo();
			
			messageBo.setEmpId(rs.getString("emp_id"));
			messageBo.setDivisionId(rs.getInt("division_id"));
			messageBo.setMobile(rs.getString("emp_mobile"));
			messageBo.setExcNum(rs.getInt("absum"));
			
			return messageBo;
		}
	}
	public class NotifyCategoryNumRowMapper implements RowMapper<NotifyCategoryNum>{
		@Override
		public NotifyCategoryNum mapRow(ResultSet rs, int rowNum) throws SQLException {
			// TODO Auto-generated method stub
			NotifyCategoryNum notifyCategoryNum=new NotifyCategoryNum();
			
			notifyCategoryNum.setCategoryId(rs.getInt("category_sid"));
			notifyCategoryNum.setCategoryName(rs.getString("category_name"));
			notifyCategoryNum.setExcNum(rs.getInt("category_sid_num"));
			
			return notifyCategoryNum;
		}
	}
	/** 根据用户类型获得all_abnormality_info_tbl表中对应的字段名
	 * @param userType
	 * @return
	 */
	private String getColumnName(int userType){
		String postName = "";
		switch(userType){
			case 2: postName="YEDAI_ID";break;
			case 5: postName="ZHUREN_ID";break;
			case 6: postName="SUOZHANG_ID";break;
			case 7: postName="ZHUANYUAN_ID";break;
			case 12: postName="ZONGJIAN_ID";break;
			case 14: postName="ZONGJINGLI_ID";break;
			case 99: postName="ZONGCAI_ID";break;
		}
		return postName;
	}
	/**删除指定事业部的异常通报数据
	 * @param divId
	 */
	private void deleteNotifyDataByDivId(int divId){
		StringBuffer buf=new StringBuffer("");
		buf.append(" delete from all_abnormality_info_tbl where division_id=? ");

		this.getiCustomerJdbcOperations().update(buf.toString(),new Object[]{divId});
	}
}
