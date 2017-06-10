/*--

 Copyright (C) 2012.
 All rights reserved.

 */
package com.want.batch.job.lds.bo;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LDAP {

	private static final Log logger = LogFactory.getLog(LDAP.class);

	private final int LDAP_SIZELIMIT_EXCEEDED = 1000;
	private final int ONE_RESULT = 1;
	private final int MANY_RESULT = 2;

	private static double MINUTE = 1000*60;
	private static double UPDATE_THRESHOLD = 0;

	private String ldapEnvironment;
	private String ldapHostnameF;
	private int ldapPortF;
	private String ldapUsernameF;
	private String ldapPasswordF;

	private String adHostname;
	private int adPort;
	private String adUsername;
	private String adPassword;
	private String adBaseDn;

	public static String ldapBaseDN;
	public static String userOU;
	public static String employeeOU;
	public static String customerOU;
	public static String orgOU;
	public static String posOU;
	public static String apOU;
	public static String hrOU;
	

	private LdapContext ldapContext = null, adContext = null;

	private static LDAP ldap = null;

	public static LDAP getInstance(String... properties) {
		if (ldap == null)
			try {
				ldap = new LDAP(properties[0]);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		return ldap;
	}
	
	private LDAP(String properties) throws IOException {
		Properties props = new Properties();
		InputStream in = LDAP.class.getClassLoader().getResourceAsStream(properties);
		props.load(in);
		
		ldapEnvironment = props.getProperty("ldap_env");
		
		ldapHostnameF = props.getProperty("ldap_hostname");
		ldapPortF = Integer.parseInt(props.getProperty("ldap_port"));
		ldapUsernameF = props.getProperty("ldap_username");
		ldapPasswordF = props.getProperty("ldap_password");
		
		adHostname = props.getProperty("ad_hostname");
		adPort = Integer.parseInt(props.getProperty("ad_port"));
		adUsername = props.getProperty("ad_username");
		adPassword = props.getProperty("ad_password");
		adBaseDn = props.getProperty("ad_users");


		ldapBaseDN = props.getProperty("ldap_base_dn");

		userOU = props.getProperty("ldap_users") + "," + ldapBaseDN;
		employeeOU = props.getProperty("ldap_employees") + "," + ldapBaseDN;
		customerOU = props.getProperty("ldap_customers") + "," + ldapBaseDN;
		orgOU = props.getProperty("ldap_organizations") + "," + ldapBaseDN;
		posOU = props.getProperty("ldap_positions") + "," + ldapBaseDN;
		apOU = props.getProperty("ldap_ap") + "," + ldapBaseDN;
		hrOU = props.getProperty("ldap_hr") + "," + ldapBaseDN;
		
		UPDATE_THRESHOLD = Double.valueOf(props.getProperty("ldap_skip_in_minutes")) * MINUTE;
		
		logger.info("Initialized variables:");
		logger.info("Host:Port: " + ldapHostnameF + ":" + ldapPortF);
		logger.info("User OU: " + userOU);
		logger.info("Employee OU: " + employeeOU);
		logger.info("Customer OU: " + customerOU);
		logger.info("Org OU: " + orgOU);
		logger.info("Pos OU: " + posOU);
		logger.info("AP OU: " + apOU);
		logger.info("HR OU: " + hrOU);
	}

	private InitialLdapContext getInitialLdapContext(String hostname, int port, String username, String password)
			throws NamingException {

		String providerURL = new StringBuffer("ldap://").append(hostname).append(":").append(port).toString();

		// Properties props = new Properties();
		Hashtable<String, Object> props = new Hashtable<String, Object>(11);
		props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		props.put(Context.PROVIDER_URL, providerURL);

		if ((username != null) && (!username.equals(""))) {
			props.put("java.naming.ldap.attributes.binary", "objectSid");
			props.put(Context.SECURITY_AUTHENTICATION, "simple");
			props.put(Context.SECURITY_PRINCIPAL, username);
			props.put(Context.SECURITY_CREDENTIALS, ((password == null) ? "" : password));
		}

		return new InitialLdapContext(props, null);
	}

	private LdapContext getLdapContext() throws NumberFormatException, NamingException {
		
		if (ldapContext == null) 
			ldapContext = getInitialLdapContext(ldapHostnameF, ldapPortF, ldapUsernameF, ldapPasswordF);

		try {
			ldapContext.getEnvironment();
		} catch (Exception e) {
			ldapContext = getInitialLdapContext(ldapHostnameF, ldapPortF, ldapUsernameF, ldapPasswordF);
		}

		return ldapContext;
	}
	
	private LdapContext getAdContext() throws NumberFormatException, NamingException {
		
		if (adContext == null) 
			adContext = getInitialLdapContext(adHostname, adPort, adUsername, adPassword);

		try {
			adContext.getEnvironment();
		} catch (Exception e) {
			adContext = getInitialLdapContext(ldapHostnameF, ldapPortF, ldapUsernameF, ldapPasswordF);
		}

		return adContext;
	}
	
	public void close() throws NamingException {
		getLdapContext().close();
	}
	
	public void init() {
		
	}
	
	public boolean save(Updatable updatable) throws Exception {
		
		boolean isUpdatedRecently = false;
		boolean updated = false;
		double diff = 0;
		
		try {
			String[] values = get(updatable.getDn(), "whenChanged");
			Date whenChanged =  parseLdapDate(values[0]);
			if (whenChanged != null) {
				 diff = Math.abs(whenChanged.getTime() - System.currentTimeMillis());
				 if (diff < UPDATE_THRESHOLD)
					 isUpdatedRecently = true;
			}
		} catch (Exception e) {
		}
		
		if (!isUpdatedRecently) {
			checkParentContainer(updatable.getDn());
			Attributes container = updatable.getContainer();
			removeEmplyAttribute(container);
			String[] backupMemberOf = new String[]{};
			
			try {
				backupMemberOf = get(updatable.getDn(), "memberOf");
			} catch (Exception e) {
			}
			
			getLdapContext().destroySubcontext(updatable.getDn());
			
			getLdapContext().createSubcontext(updatable.getDn(), container);
			
			restoreMemberOf(backupMemberOf, updatable.getDn());
			
			updated = true;
		} else
			logger.debug(updatable.getDn() + " has been updated within " + ((int)(diff / (5*MINUTE)) + " minutes"));
		
		return updated;
	}
	
	public boolean delete(String dn) {
		boolean result = false;
		try {
			if (exists(dn)) {
				getLdapContext().destroySubcontext(dn);
				result = true;
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	public boolean update(Updatable updatable) throws Exception {
		
		boolean isUpdatedRecently = false;
		boolean updated = false;
		double diff = 0;
		
		try {
			String[] values = get(updatable.getDn(), "whenChanged");
			Date whenChanged =  parseLdapDate(values[0]);
			if (whenChanged != null) {
				 diff = Math.abs(whenChanged.getTime() - System.currentTimeMillis());
				 if (diff < UPDATE_THRESHOLD)
					 isUpdatedRecently = true;
			}
		} catch (Exception e) {
		}
		
		if (!isUpdatedRecently) {
			checkParentContainer(updatable.getDn());
			if (!exists(updatable.getDn())) {
				Attributes container = updatable.getContainer();
				removeEmplyAttribute(container);
				getLdapContext().createSubcontext(updatable.getDn(), container);
				updated = true;
			}
		} else
			logger.debug(updatable.getDn() + " has been updated within " + ((int)(diff / (5*MINUTE)) + " minutes"));
		
		return updated;
	}

	
	private void removeEmplyAttribute(Attributes container) throws NamingException {
		for (NamingEnumeration<?> attrs = container.getAll(); attrs.hasMoreElements();) {
			Attribute attr = (Attribute) attrs.nextElement();
			Object obj = container.get(attr.getID()).get();
			if (obj != null && obj instanceof String) {
				String value = (String) obj; 
				if (value == null || value.trim().length() <= 0) {
					container.remove(attr.getID());
					logger.debug(attr.getID() + " is empty, removed");
				}
			}
		}
	}
		
	public boolean isProduction() {
		if (ldapEnvironment != null && ldapEnvironment.equals(Constants.PRODUCTION))
			return true;
		else
			return false;
	}
	
	private void restoreMemberOf(String[] parents, String child) {
		for (String dn: parents) {
			ModificationItem[] mods = new ModificationItem[1];
			Attribute mod = new BasicAttribute("member", child);
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod);
			
				try {
					getLdapContext().modifyAttributes(dn, mods);
					//logger.debug(dn + " add member " + child);
				} catch (Exception e) {
					logger.error("[" + child + "] add member [" + dn + "] fail for " + e.getMessage());
				}
		}
	}


	public void checkParentContainer(String dn) throws NamingException {
		String parent = dn.substring(dn.indexOf(",") + 1);
		if (!exists(parent)) {
			
			if (parent == null || parent.indexOf(",") < 0) {
				logger.error("invalid value " + parent);
				return;
			}
			
			if (exists(parent)) {
				return;
			} else
				checkParentContainer(parent);

			String id = parent.substring(3, parent.indexOf(","));

			Attribute objClasses = new BasicAttribute("objectClass");
			objClasses.add("top");
			objClasses.add("container");

			Attribute cn = new BasicAttribute("cn", id);

			Attributes container = new BasicAttributes();
			container.put(objClasses);
			container.put(cn);

			getLdapContext().createSubcontext(parent, container);
			logger.debug("已添加 :" + dn);

		}
	}
	
	private String[] get(String dn, String... keys) throws NumberFormatException, NamingException {
		ArrayList<String> results = new ArrayList<String>();
		Attributes attributes = getLdapContext().getAttributes(dn, keys);
		
		for (int i=0; i<keys.length; i++) {
			Attribute attr = attributes.get(keys[i]);
			for (int j=0; j<attr.size(); j++)
				results.add(attr.get(j).toString());
			
		}
		String[] array = new String[]{};
		return results.toArray(array);
	}

	private boolean exists(String dn) throws NumberFormatException, NamingException {
		boolean ret = false;
		try {
			String[] results = get(dn, "cn");
			if (results != null && results.length == 1 && results[0] != null)
				ret = true;
		} catch (NamingException e) {
			//logger.debug(dn + " not existing for " + e.getMessage());
		}
		return ret;
	}
	
	protected String getADSid(String empId) throws NamingException {
		SearchControls cons = new SearchControls();
		cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(objectClass=organizationalPerson)(objectCategory=CN=Person,CN=Schema,CN=Configuration,DC=want-want,DC=com)(CN=" + empId + "))";
		String ret = null;
		NamingEnumeration<?> results = getAdContext().search(adBaseDn, filter, cons);

		if(results.hasMore()) {
			SearchResult entry = (SearchResult) results.next();
			Attributes attributes = entry.getAttributes();
			Attribute attr = attributes.get("objectSid");
			if (attr != null) {
				for (NamingEnumeration<?> namingEnum_2 = attr.getAll(); namingEnum_2.hasMoreElements();) {
					Object attValue = namingEnum_2.nextElement();
					ret = getSIDAsString((byte[]) attValue);
					break;
				}
			}
		}

		return ret;
	}

	private static String getSIDAsString(byte[] SID) {
		StringBuilder strSID = new StringBuilder("S-");
		strSID.append(SID[0]).append('-');
		StringBuilder tmpBuff = new StringBuilder();
		for (int t = 2; t <= 7; t++) {
			String hexString = Integer.toHexString(SID[t] & 0xFF);
			tmpBuff.append(hexString);
		}
		strSID.append(Long.parseLong(tmpBuff.toString(), 16));
		int count = SID[1];
		for (int i = 0; i < count; i++) {
			int currSubAuthOffset = i * 4;
			tmpBuff.setLength(0);
			tmpBuff.append(String.format("%02X%02X%02X%02X", (SID[11 + currSubAuthOffset] & 0xFF),
					(SID[10 + currSubAuthOffset] & 0xFF), (SID[9 + currSubAuthOffset] & 0xFF),
					(SID[8 + currSubAuthOffset] & 0xFF)));
	
			strSID.append('-').append(Long.parseLong(tmpBuff.toString(), 16));
		}
		return strSID.toString();
	}
	
	private static SimpleDateFormat LDAP_DATE_FORMAT = null;
	static {
		LDAP_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
		LDAP_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
		
	private Date parseLdapDate(String ldapDate){	
	    try {
	        return LDAP_DATE_FORMAT.parse(ldapDate);
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return null;
	}
	
	
	protected List<String> getDns(List<String> cns, String type) throws NamingException, IOException {
		return getDns(userOU, cns, type);
	}

	protected List<String> getDns(String dn, List<String> cns, String type) throws NamingException, IOException {
		List<String> results = new ArrayList<String>();
		String initFilter = "(&(objectClass=" + type + ")(|";
		if (cns != null && cns.size() > 0) {
			for (int i = 0; i < cns.size(); i++) {
				String filter = initFilter + "(cn=" + cns.get(i) + ")))";
				try {
					Set<String> list = queryWithFilter(dn, filter.toString());
					results.addAll(list);
				} catch (Exception e) {
					logger.error(filter + " not found for " + e.getMessage() );
				}
			}
		}

		return results;
	}

	private Set<String> queryWithFilter(String baseDN, String filter) throws NamingException, IOException {
		return query(baseDN, filter, SearchControls.SUBTREE_SCOPE, MANY_RESULT,
				this.LDAP_SIZELIMIT_EXCEEDED).keySet();
	}

	private Map<String, SearchResult> query(String searchDN, String filter, int scope,
			int occurrance, int size) throws NamingException, IOException {
		Map<String, SearchResult> values = new HashMap<String, SearchResult>();
		byte[] cookie = null;
		LdapContext ctx = getLdapContext();
		ctx.setRequestControls(new Control[] { new PagedResultsControl(size, Control.CRITICAL) });
		int count = 0;

		do {
			SearchControls cons = new SearchControls();

			cons.setSearchScope(scope);
			NamingEnumeration<?> results = ctx.search(searchDN, filter, cons);

			while (results.hasMore()) {
				SearchResult sr = (SearchResult) results.next();
				values.put(sr.getAttributes().get("distinguishedName").get().toString(), sr);
				count++;
				if (count % 1000 == 0)
					logger.debug("已读取" + count + "个用户数");

				if (occurrance == ONE_RESULT)
					break;
			}

			if (occurrance == ONE_RESULT)
				break;

			Control[] controls = ctx.getResponseControls();
			if (controls != null)
				for (int i = 0; i < controls.length; i++)
					if (controls[i] instanceof PagedResultsResponseControl) {
						PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
						cookie = prrc.getCookie();
					}

			ctx.setRequestControls(
					new Control[] { new PagedResultsControl(this.LDAP_SIZELIMIT_EXCEEDED, cookie, Control.CRITICAL) });
		} while (cookie != null);

		return values;
	}

}
