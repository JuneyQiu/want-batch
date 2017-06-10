package com.want.batch.job.archive.notify;

import java.util.List;

import org.joda.time.DateTime;

import com.want.batch.job.AbstractWantJob;
import com.want.batch.job.archive.notify.jobs.NotifyCommand;

public class NotifyJob extends AbstractWantJob {

	private List<NotifyCommand> commands;

	@Override
	public void execute() {
		DateTime dateTime = new DateTime();
		initAbnormalityCustInterimTbl();
		for (NotifyCommand command : this.getCommands()) {
			batchStatus.setFuncId(this.getBatchStatusFuncId()+dateTime.toString("HH"));
			command.execute();
		}
	}
	
	public List<NotifyCommand> getCommands() {
		return commands;
	}

	public void setCommands(List<NotifyCommand> commands) {
		this.commands = commands;
	}
	
	/**
	 * 根据客户属性生成各事业部需要记录异常的客户
	 */
	private void initAbnormalityCustInterimTbl(){
		StringBuffer buf1=new StringBuffer("");
		StringBuffer buf2=new StringBuffer("");
		buf1.append(" delete from abnormality_cust_interim ");
		buf2.append(" insert into abnormality_cust_interim(division_id,customer_id,abnormal_type_id,create_user,create_date) ")
			.append(" select a23.division_id,d23.customer_id,a23.abnormal_type_id,'mis' as create_user,sysdate as create_date ")
			.append(" from division_custype_abtype_cfg a23 ")
			.append(" inner join sales_area_rel b23 on a23.division_id=b23.divsion_sid ")
			.append(" inner join cus_type_grp23_rel c23 on a23.cus_type_id=c23.customer_type_id ")
			.append("               and trim(b23.channel_id)=c23.sale_channel ")
			.append("               and trim(b23.prod_group_id)=c23.prod_group ")
			.append(" inner join knvv d23 on c23.customer_group2=d23.customer_group2 ")
			.append("                      and c23.customer_group3=d23.customer_group3 ")
			.append("                      and c23.sale_channel=d23.sales_channel ")
			.append("                      and c23.prod_group=d23.product ")
			.append(" union ")
			.append(" select a4.division_id,d4.customer_id,a4.abnormal_type_id,'mis' as create_user,sysdate as create_date ")
			.append(" from division_custype_abtype_cfg a4 ")
			.append(" inner join sales_area_rel b4 on a4.division_id=b4.divsion_sid ")
			.append(" inner join cus_type_grp4_rel c4 on a4.cus_type_id=c4.customer_type_id ")
			.append("              					and trim(b4.channel_id)=c4.sale_channel ")
			.append("              					and trim(b4.prod_group_id)=c4.prod_group ")
			.append(" inner join knvv d4 on c4.customer_group4=d4.customer_group4 ")
			.append("                    and c4.sale_channel=d4.sales_channel ")
			.append("                    and c4.prod_group=d4.product ");

		this.getiCustomerJdbcOperations().update(buf1.toString());
		this.getiCustomerJdbcOperations().update(buf2.toString());
	}

}
