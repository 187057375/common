package org.wsh.common.orm.mysql.mybatis.build;

import org.wsh.common.orm.mysql.mybatis.base.AbstractBuildFactory;
import org.wsh.common.orm.mysql.mybatis.bean.Table;
import org.wsh.common.orm.mysql.mybatis.bean.TableWapper;
import org.wsh.common.orm.mysql.mybatis.enums.OutPathKey;
import org.wsh.common.orm.mysql.mybatis.util.Util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.wsh.common.orm.mysql.mybatis.util.Util.parseDate;

/**
 * author: wsh
 * JDK-version:  JDK1.8
 * comments:  构建持久层
 * since Date： 2016/11/16 15:23
 */
public class BuildDao extends AbstractBuildFactory {

	@Override
	public OutPathKey getOutPath() {
		return OutPathKey.DAO;
	}

	@Override
	public void buildTable(TableWapper tableWapper) {

		String daoPackage = tableWapper.getDaoPackage();
		String pojoPackage = tableWapper.getPojoPackage();
		String output = tableWapper.getOutPathMap().get(getOutPath());
		Table table = tableWapper.getTable();
		String tableName = table.getName();
		String headName = Util.getUpperHumpName(tableName);
		String minDoName = Util.getHumpName(tableName);
		String bigDoName = Util.getUpperHumpName(tableName);
		String bigDaoName = Util.getUpperHumpName(tableName)+"Dao";
		bigDoName = bigDoName.replace("Tab", "");
		bigDaoName = bigDaoName.replace("Tab", "");

		char letters[] = new char[tableName.length()];
		for(int i=0;i<tableName.length();i++){
			char letter = tableName.charAt(i);
			if(letter>='a' && letter<='z')
				letter = (char) (letter-32);
			letters[i] = letter;
		}

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("daoPackage", daoPackage);
		map.put("pojoPackage", pojoPackage);
		map.put("bigDoName", bigDoName + "DO");
		map.put("minDoName", minDoName + "DO");
		map.put("bigDaoName", bigDaoName);
		map.put("headName", headName);
		map.put("date", parseDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
		map.put("tableName", new String(letters));
		Util.writeCode("dao", map,output+bigDaoName+".java/");
	}
}