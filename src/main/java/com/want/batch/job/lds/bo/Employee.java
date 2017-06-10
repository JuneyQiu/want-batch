package com.want.batch.job.lds.bo;

import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Employee extends User {

	private static final Log logger = LogFactory.getLog(Employee.class);

	public static final String[] jobGradeDesc = {"特一等","特二等","特三等","特四等","委一等","委二等","委三等","聘四等","聘五等","聘六等","雇七等"};
	private String mobile; //手机号
	private String email; //电子邮件
	private String locale="zh_CN"; //语言
	private String telephoneNumber; //分机号
	private String jobGrade; //职等
	private String jobName; //职务
	private String birthday; //生日
	private String supervisor; //主管工号
	private String supervisorOfSales; //业务类主管工号
	private String onboardDate; //入职日期
	private String position; //岗位资讯
	private String organization; //单位资讯
	private List<Position> positionList; //岗位列表
	private List<String> memberOfList; //成员列表
	private String empClassName; //在职状态
	private String empSubClassName; //在职状态子分类
	private String empAreaName; //人事范围
	private String empSubAreaName; //人事子范围
	private String empClassCode; //在职状态
	private String empSubClassCode; //在职状态子分类
	//private String empAreaCode; //人事范围
	private String empSubAreaCode; //人事子范围	
	private boolean groupInlcuded; //是否需要将memberOf的属性加在Employee物件中
	
	private byte[] photo = null;
	
	private List<String> orgIdList;
	
	
	//add by TaoJie begin
	private boolean status;

	/*
	 *是否为空岗 
	 */
	public boolean isEmptyPos;
	
	
	/*
	 * 主岗副岗
	 */
	private String masterId;
		
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getTelephoneNumber() {
		return telephoneNumber;
	}
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	public String getJobGradeDesc() {
		String jobGradeDesc = goJobGradeDesc(jobGrade);
		return jobGradeDesc;
	}
	public void setJobGrade(String jobGrade) {
		this.jobGrade = jobGrade;
	}	
	public String getJobGrade() {
		return jobGrade;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}	
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public List<Position> getPositionList() {
		return positionList;
	}
	public void setPositionList(List<Position> positionList) {
		this.positionList = positionList;
	}
	private static String goJobGradeDesc(String jobGradeId)
	{
		String result = jobGradeId;
		try{
		   int iJobGradeId = Integer.parseInt(jobGradeId);
		   result = jobGradeDesc[iJobGradeId-1];
		}catch(Exception e){}
		return result;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getSupervisorOfSales() {
		return supervisorOfSales;
	}
	public void setSupervisorOfSales(String supervisorOfSales) {
		this.supervisorOfSales = supervisorOfSales;
	}	
	public String getEmpClassName() {
		return empClassName;
	}
	public void setEmpClassName(String empClassName) {
		this.empClassName = empClassName;
	}
	public String getEmpSubClassName() {
		return empSubClassName;
	}
	public void setEmpSubClassName(String empSubClassName) {
		this.empSubClassName = empSubClassName;
	}	
	public String getEmpAreaName() {
		return empAreaName;
	}
	public void setEmpAreaName(String empAreaName) {
		this.empAreaName = empAreaName;
	}
	public String getEmpSubAreaName() {
		return empSubAreaName;
	}
	public void setEmpSubAreaName(String empSubAreaName) {
		this.empSubAreaName = empSubAreaName;
	}
	public String getOnboardDate() {
		return onboardDate;
	}
	public void setOnboardDate(String onboardDate) {
		this.onboardDate = onboardDate;
	}		
	public String getEmpClassCode() {
		return empClassCode;
	}
	public void setEmpClassCode(String empClassCode) {
		this.empClassCode = empClassCode;
	}
	public String getEmpSubClassCode() {
		return empSubClassCode;
	}
	public void setEmpSubClassCode(String empSubClassCode) {
		this.empSubClassCode = empSubClassCode;
	}
	//public String getEmpAreaCode() {
	//	return empAreaCode;
	//}
	//public void setEmpAreaCode(String empAreaCode) {
	//	this.empAreaCode = empAreaCode;
	//}
	public String getEmpSubAreaCode() {
		return empSubAreaCode;
	}
	public void setEmpSubAreaCode(String empSubAreaCode) {
		this.empSubAreaCode = empSubAreaCode;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}	

	public String toString() {
		StringBuffer positions = new StringBuffer();
		StringBuffer memberOfs = new StringBuffer();
		if(positionList!=null && positionList.size()>0)
		{
			for(int i=0;i<positionList.size();i++)
			{
				Position position = (Position)positionList.get(i);
				Organization organization = position.getOrganization();
				if(organization!=null)
					positions.append(organization.toString()+"\n");					
				if(position!=null)
				   positions.append(position.toString());
			}
		}
		
		if(memberOfList!=null && memberOfList.size()>0)
		{
			for(int i=0;i<memberOfList.size();i++)
			{
				String group = (String)memberOfList.get(i);			
				memberOfs.append(group+"\n");
			}
		}		
		
		String result = "工号:" + getId() + "    姓名:" + getName() + "    分机号:" + getTelephoneNumber() + "    手机号:" + getMobile() + "\n"
		+ "职等:" + getJobGrade()+ "    职等描述:" + getJobGradeDesc() + "    职务:" + getJobName() + "\n"
		+ "入职日期:" + getOnboardDate()	 +  "\n"
		+ "在职状态码:" + getEmpClassCode() + "    在职状态子类别码:" + getEmpSubClassCode() +  "\n"			

		+ "在职状态:" + getEmpClassName() + "    在职状态子类别:" + getEmpSubClassName() +  "\n";			
		
		if(getOrganization()!=null)
		  result += "单位:" + getOrganization() + "\n";

		if(positionList!=null && positionList.size()>0)
		  result += "岗位与单位资讯:\n" + positions.toString();
		
		if(memberOfList!=null && memberOfList.size()>0)
		  result += "所属成员(Member Of):\n" + memberOfs.toString() + "\n\n";	
		return result;
	}
	
	public void setMemberOfList(List<String> memberOfList) {
		this.memberOfList = memberOfList;
	}
	public List<String> getMemberOfList() {
		return memberOfList;
	}
	public void setGroupInlcuded(boolean groupInlcuded) {
		this.groupInlcuded = groupInlcuded;
	}
	public boolean isGroupInlcuded() {
		return groupInlcuded;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMasterId() {
		return masterId;
	}
	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}
	public boolean isEmptyPos() {
		return isEmptyPos;
	}
	public void setEmptyPos(boolean isEmptyPos) {
		this.isEmptyPos = isEmptyPos;
	}
	public List<String> getOrgIdList() {
		return orgIdList;
	}
	public void setOrgIdList(List<String> orgIdList) {
		this.orgIdList = orgIdList;
	}

	
	
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	@Override
	public Attributes getContainer() {
		// Create a container set of attributes
		Attributes container = new BasicAttributes();

		// Create the objectclass to add
		Attribute objClasses = new BasicAttribute("objectClass");
		container.put(objClasses);
		objClasses.add("top");
		objClasses.add("person");
		objClasses.add("organizationalPerson");
		objClasses.add("wantWantOrgPerson");
		
		if (LDAP.getInstance().isProduction()) {
			// Has AD account
			// userProxyFull
			// wantWantUserProxyFull
			try {
				container.put(new BasicAttribute("objectSid", LDAP.getInstance().getADSid(getId())));
				objClasses.add("userProxyFull");
				objClasses.add("wantWantUserProxyFull");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
						
			container.put(new BasicAttribute("mail", this.getEmail()));
		} else {
			// Has no AD account
			// user
			// wantWantOrgPerson
			objClasses.add("user");
			container.put(new BasicAttribute("description", "ADUser"));
			container.put(new BasicAttribute("userPassword", "P@ssw0rd"));
			container.put(new BasicAttribute("mail", "ecad_tester@want-want.com"));
		}

		//TODO: add "-" for development purpose only
		container.put(new BasicAttribute("userPrincipalName", getId()));
		container.put(new BasicAttribute("l", getLocale()));
		container.put(new BasicAttribute("cn", getId()));
		container.put(new BasicAttribute("sn", getName()));
		container.put(new BasicAttribute("uid", getId()));

		if (getJobGrade() != null)
			container.put(new BasicAttribute("empJobGrade", getJobGrade()));
		
		if (getGender() != null)
			container.put(new BasicAttribute("empGender", getGender()));

		if (getJobName() != null)
			container.put(new BasicAttribute("title", getJobName()));

		if (getOnboardDate() != null)
			container.put(new BasicAttribute("onboardDate", getOnboardDate()));
		
		if (getEmpClassName() != null)
			container.put(new BasicAttribute("empClassName", getEmpClassName()));

		if (getEmpSubClassName() != null)
			container.put(new BasicAttribute("empSubClassName", getEmpSubClassName()));

		if (getEmpAreaName() != null)
			container.put(new BasicAttribute("empAreaName", getEmpAreaName()));

		if (getEmpSubAreaName() != null)
			container.put(new BasicAttribute("empSubAreaName", getEmpSubAreaName()));

		if (getEmpClassCode() != null)
			container.put(new BasicAttribute("empClassCode", getEmpClassCode()));

		if (getEmpSubClassCode() != null)
			container.put(new BasicAttribute("empSubClassCode", getEmpSubClassCode()));

		if (getEmpSubAreaCode() != null)
			container.put(new BasicAttribute("empSubAreaCode", getEmpSubAreaCode()));

		if (getMobile() != null)
			container.put(new BasicAttribute("mobile", getMobile().trim()));

		if (getTelephoneNumber() != null)
			container.put(new BasicAttribute("telephoneNumber", getTelephoneNumber().trim()));
		
		if (getPhoto() != null)
			container.put(new BasicAttribute("thumbnailPhoto", getPhoto()));

		return container;
	}
}
