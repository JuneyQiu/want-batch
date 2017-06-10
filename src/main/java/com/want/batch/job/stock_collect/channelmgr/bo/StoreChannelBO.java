package com.want.batch.job.stock_collect.channelmgr.bo;

import java.util.List;

public class StoreChannelBO {
	private int STORESID;
	private String STOREID;
	private String STORENAME;
	private String STORETYPE;
	private String PROJECTSID;
	private List PROJARR;
	private boolean HW01; // 配送共线
	private boolean HW22; // 配送休闲
	private boolean HW23; // 配送乳饮
	private String AREA001;// 配送共线区域
	private String AREA022;// 配送休闲区域
	private String AREA023;// 配送乳饮区域
	private boolean HW04; // 县城共线
	private boolean HW17; // 县城休闲
	private boolean HW18; // 县城乳饮
	private String AREA004;// 送旺下乡区域
	private String AREA017;// 县城休闲区域
	private String AREA018;// 县城乳饮区域
	private boolean HW10; // 城区饮品
	private boolean HW12; //乳品共线
	private boolean HW27; //乳品利乐
	private boolean HW28;// 乳品铁罐
	private String AREA010;// 城区饮品区域
	private String AREA012;// 城区-乳品区域
	private String AREA027;// 乳品利乐区域
	private String AREA028;// 乳品铁罐区域
	private boolean HW011; // 通路发展
	private boolean HW14; // 强网-休闲
	private boolean HW15;// 糕饼膨化
	private boolean HW25;//米果炒货
	private boolean HW26;//糖果果冻
	private boolean HW29; // 休闲点心
	private String AREA014;// 强网-休闲区域
	private String AREA015;// 糕饼膨化区域
	private String AREA025;// 米国炒货区域
	private String AREA026;//糖果果冻区域
	private String AREA029; // 休闲点心
	private boolean HW016; // 冰品
	private String AREA016;// 冰品区域
	private boolean HW020; // 现渠
	private String AREA020;// 现渠区域
	private boolean HW021; // 方便
	private String AREA021;// 方便区域
	private boolean HW024; // 直营
	private String AREA024;// 直营区域
	private boolean HW30; // 哎呦点心
	private String AREA030;// 哎呦点心区域
	private boolean HW31; // 特通
	private String AREA031;// 特通区域
	private boolean HW32; // 配送一线
	private String AREA032;// 配送一线区域
	private boolean HW33; // 配送二线
	private String AREA033;// 配送二线区域
	private boolean HW34; // 配送三线
	private String AREA034;// 配送三线区域
	public StoreChannelBO() {
		HW01 = false;
		HW22 = false;
		HW23 = false;
		HW04 = false;
		HW17 = false;
		HW18 = false;
		HW10 = false;
		HW12 = false;
		HW27 = false;
		HW28 = false;
		HW011 = false;
		HW14 = false;
		HW15=false;
		HW25=false;
		HW26=false;
		HW016 = false;
		HW021 = false;
		HW27 = false;
		HW28 = false;
		HW29 = false;
		HW30 = false;
		HW31 = false;
		HW32 = false;
		HW33 = false;
		HW34 = false;
	}

	public int getSTORESID() {
		return STORESID;
	}

	public void setSTORESID(int storesid) {
		STORESID = storesid;
	}

	public String getSTOREID() {
		return STOREID;
	}

	public void setSTOREID(String storeid) {
		STOREID = storeid;
	}

	public String getSTORENAME() {
		return STORENAME;
	}

	public void setSTORENAME(String storename) {
		STORENAME = storename;
	}

	public String getSTORETYPE() {
		return STORETYPE;
	}

	public void setSTORETYPE(String storetype) {
		STORETYPE = storetype;
	}

	public String getPROJECTSID() {
		return PROJECTSID;
	}

	public void setPROJECTSID(String projectsid) {
		PROJECTSID = projectsid;
	}

	public List getPROJARR() {
		return PROJARR;
	}

	public void setPROJARR(List projarr) {
		PROJARR = projarr;
	}

	public String getAREA001() {
		return AREA001;
	}

	public void setAREA001(String area001) {
		AREA001 = area001;
	}

	public String getAREA004() {
		return AREA004;
	}

	public void setAREA004(String area004) {
		AREA004 = area004;
	}

	public String getAREA017() {
		return AREA017;
	}

	public void setAREA017(String area017) {
		AREA017 = area017;
	}

	public String getAREA018() {
		return AREA018;
	}

	public void setAREA018(String area018) {
		AREA018 = area018;
	}
    
	

	public boolean isHW10() {
		return HW10;
	}

	public void setHW10(boolean hW10) {
		HW10 = hW10;
	}

	public boolean isHW12() {
		return HW12;
	}

	public void setHW12(boolean hW12) {
		HW12 = hW12;
	}

	public boolean isHW27() {
		return HW27;
	}

	public void setHW27(boolean hW27) {
		HW27 = hW27;
	}

	public boolean isHW28() {
		return HW28;
	}

	public void setHW28(boolean hW28) {
		HW28 = hW28;
	}

	public String getAREA027() {
		return AREA027;
	}

	public void setAREA027(String aREA027) {
		AREA027 = aREA027;
	}

	public String getAREA028() {
		return AREA028;
	}

	public void setAREA028(String aREA028) {
		AREA028 = aREA028;
	}

	public String getAREA010() {
		return AREA010;
	}

	public void setAREA010(String area010) {
		AREA010 = area010;
	}

	public String getAREA012() {
		return AREA012;
	}

	public void setAREA012(String area012) {
		AREA012 = area012;
	}

	public boolean isHW011() {
		return HW011;
	}

	public void setHW011(boolean hw011) {
		HW011 = hw011;
	}

	public boolean isHW14() {
		return HW14;
	}

	public void setHW14(boolean hw14) {
		HW14 = hw14;
	}

	public boolean isHW15() {
		return HW15;
	}

	public void setHW15(boolean hw15) {
		HW15 = hw15;
	}

	public boolean isHW25() {
		return HW25;
	}

	public void setHW25(boolean hw25) {
		HW25 = hw25;
	}

	public boolean isHW26() {
		return HW26;
	}

	public void setHW26(boolean hw26) {
		HW26 = hw26;
	}

	public String getAREA014() {
		return AREA014;
	}

	public void setAREA014(String area014) {
		AREA014 = area014;
	}

	public String getAREA015() {
		return AREA015;
	}

	public void setAREA015(String area015) {
		AREA015 = area015;
	}

	public boolean isHW016() {
		return HW016;
	}

	public void setHW016(boolean hw016) {
		HW016 = hw016;
	}

	public String getAREA016() {
		return AREA016;
	}

	public void setAREA016(String area016) {
		AREA016 = area016;
	}

	public boolean isHW021() {
		return HW021;
	}

	public void setHW021(boolean hw021) {
		HW021 = hw021;
	}

	public String getAREA021() {
		return AREA021;
	}

	public void setAREA021(String area021) {
		AREA021 = area021;
	}

	public boolean isHW020() {
		return HW020;
	}

	public void setHW020(boolean hw020) {
		HW020 = hw020;
	}

	public String getAREA020() {
		return AREA020;
	}

	public void setAREA020(String area020) {
		AREA020 = area020;
	}

	public String getAREA022() {
		return AREA022;
	}

	public void setAREA022(String area022) {
		AREA022 = area022;
	}

	public String getAREA023() {
		return AREA023;
	}

	public void setAREA023(String area023) {
		AREA023 = area023;
	}

	public boolean isHW024() {
		return HW024;
	}

	public void setHW024(boolean hw024) {
		HW024 = hw024;
	}

	public String getAREA024() {
		return AREA024;
	}

	public void setAREA024(String area024) {
		AREA024 = area024;
	}

	public String getAREA025() {
		return AREA025;
	}

	public void setAREA025(String area025) {
		AREA025 = area025;
	}

	public String getAREA026() {
		return AREA026;
	}

	public void setAREA026(String area026) {
		AREA026 = area026;
	}

	public boolean isHW01() {
		return HW01;
	}

	public void setHW01(boolean hW01) {
		HW01 = hW01;
	}

	public boolean isHW22() {
		return HW22;
	}

	public void setHW22(boolean hW22) {
		HW22 = hW22;
	}

	public boolean isHW23() {
		return HW23;
	}

	public void setHW23(boolean hW23) {
		HW23 = hW23;
	}

	public boolean isHW04() {
		return HW04;
	}

	public void setHW04(boolean hW04) {
		HW04 = hW04;
	}

	public boolean isHW17() {
		return HW17;
	}

	public void setHW17(boolean hW17) {
		HW17 = hW17;
	}

	public boolean isHW18() {
		return HW18;
	}

	public void setHW18(boolean hW18) {
		HW18 = hW18;
	}

	public boolean isHW29() {
		return HW29;
	}

	public void setHW29(boolean hW29) {
		HW29 = hW29;
	}

	public String getAREA029() {
		return AREA029;
	}

	public void setAREA029(String aREA029) {
		AREA029 = aREA029;
	}

	public boolean isHW30() {
		return HW30;
	}

	public void setHW30(boolean hW30) {
		HW30 = hW30;
	}

	public String getAREA030() {
		return AREA030;
	}

	public void setAREA030(String aREA030) {
		AREA030 = aREA030;
	}

	public boolean isHW31() {
		return HW31;
	}

	public void setHW31(boolean hW31) {
		HW31 = hW31;
	}

	public String getAREA031() {
		return AREA031;
	}

	public void setAREA031(String aREA031) {
		AREA031 = aREA031;
	}

	public boolean isHW32() {
		return HW32;
	}

	public void setHW32(boolean hW32) {
		HW32 = hW32;
	}

	public String getAREA032() {
		return AREA032;
	}

	public void setAREA032(String aREA032) {
		AREA032 = aREA032;
	}

	public boolean isHW33() {
		return HW33;
	}

	public void setHW33(boolean hW33) {
		HW33 = hW33;
	}

	public String getAREA033() {
		return AREA033;
	}

	public void setAREA033(String aREA033) {
		AREA033 = aREA033;
	}

	public boolean isHW34() {
		return HW34;
	}

	public void setHW34(boolean hW34) {
		HW34 = hW34;
	}

	public String getAREA034() {
		return AREA034;
	}

	public void setAREA034(String aREA034) {
		AREA034 = aREA034;
	}
	
	
}
