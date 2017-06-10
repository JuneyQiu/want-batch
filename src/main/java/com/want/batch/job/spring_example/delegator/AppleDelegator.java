package com.want.batch.job.spring_example.delegator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.want.batch.job.spring_example.dao.AppleDao;

/**
 * apple的delegator实现类
 * 
 * @author suyulin
 */
@Component
public class AppleDelegator {
	
	@Autowired
	private AppleDao appleDao;
	
	/**
	 * 批量操作，增加多条apple信息。
	 * @throws SQLException 
	 */
    public int addByBatch(String name, int num) {
    	
    	// modify mandy 2013-07-03
    	List<Object[]> list = new ArrayList<Object[]>();
    	
    	for(int i = 1; i <= num; i++) {
    		
    		Object[] bo = new Object[1];
    		bo[0] = (name + "_" + i);
    		
    		list.add(bo);
    	}
    	
    	return appleDao.addByBatch(list);
    }
}
