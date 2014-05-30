package model;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import util.Config;
import util.DateTime;
import util.MongoDB;
import util.XMLManger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * 将excel表导入mongo
 * 
 * @author Huang
 * @date 2013-5-25 下午3:43:53
 */
public class JXLExcel2Mongo {

	private static DB db;

	/**
	 * 数据库表信息
	 * 
	 * @author Huang
	 * @date 2013-5-25 下午2:10:02
	 */
	static class DbTableObject {
		String dbName;
		String tableName;
	}

	/**
	 * 字段数据
	 * 
	 * @author Huang
	 * @date 2013-5-25 下午2:09:52
	 */
	static class FieldObject {
		int intType;// 1:int,2:boolean,3:long,4:string
		String name;
	}

	/**
	 * 程序主入口仅支持 1997-2003
	 * 
	 * @author Huang
	 * @date 2013-5-29 上午11:54:54
	 * @return void
	 * @throws Exception
	 */
	public static String JXLImportDB(String filePath) throws Exception {

		int dataNum = 0;
		long starTime = System.currentTimeMillis();
		DbTableObject dbtable = new DbTableObject();
		File f = new File(filePath);
		Workbook book;
		try {
			book = Workbook.getWorkbook(f);//
		} catch (Exception e) {
			if (e instanceof BiffException) {
				return "暂不支持xlsx格式，请将保存为了xls格式";
			}
			throw e;
		}
		
		
		Sheet sheetHead = book.getSheet(0); // 获得第一个工作表对象
		boolean findDbName = false;
		boolean findTableName = false;
		for (int j = 0; j < sheetHead.getColumns(); j++) {// 列
			Cell cell = sheetHead.getCell(j, 1); // 获得单元格
			String content = cell.getContents();
			if (!findDbName && content.indexOf("*") > -1) {
				dbtable.dbName = content.substring(1).trim(); // 1
				findDbName = true;
			}
			if (!findTableName && content.indexOf("#") > -1) {
				dbtable.tableName = content.substring(1).trim(); // 2
				findTableName = true;
			}
		}
		List<FieldObject> fieldObject = new ArrayList<>();
		for (int j = 0; j < sheetHead.getColumns(); j++) {// 列
			Cell cell = sheetHead.getCell(j, 2); // 获得单元格
			String content = cell.getContents().trim();
			if (content.equals("")) {
				continue;
			}
			int start = content.indexOf("(");
			int end = content.indexOf(")");

			if (start < 0 || end < 0) {
				return "字段名" + content + "没有指字好类型";
			}

			String type = content.substring(start + 1, end);
			String fieldName = content.substring(0, start);
			FieldObject fo = new FieldObject();
			fo.name = fieldName;
			fo.intType = strType2intType(type);
			fieldObject.add(fo);
		}

		Config.init(XMLManger.readXml(System.getProperty("user.dir")
				+ "/config.xml"));

		db = MongoDB.initDB(Config.getString("dbHost"),
				Config.getInt("dbPort"), Config.getString("dbName"),
				Config.getString("dbUser"), Config.getString("dbPass"));
		clearTable(db, dbtable.tableName);
		for (int ii = 0; ii < book.getSheets().length; ii++) {
			Sheet sheet = (book.getSheets())[ii];
			// 判断该表是否为其他策划工具表
			if (sheet.getRows() < 3 || sheet.getColumns() < 1) {
				continue;
			}
			Cell cellT = sheet.getCell(1, 2); // 获得单元格
			String contentT = cellT.getContents();
			if (contentT == null || contentT.equals("")) {
				continue;
			}
			List<DBObject> dataList = new ArrayList<>();
			for (int i = 3; i < sheet.getRows(); i++) {
				DBObject ob = new BasicDBObject();
				boolean isBadSheetRow = false;
				for (int j = 0; j < fieldObject.size(); j++) {
					Cell cell = sheet.getCell(j, i); // 获得单元格
					String content = cell.getContents();
					if (j == 0 && content.equals("")) {
						isBadSheetRow = true;
						break;
					}
					try {
						FieldObject fo = fieldObject.get(j);
						if (fo.intType == 1) {
							ob.put(fo.name, Integer.parseInt(content));
						} else if (fo.intType == 2) {
							ob.put(fo.name, Boolean.valueOf(content));
						} else if (fo.intType == 3) {
							ob.put(fo.name, Long.parseLong(content));
						} else {
							ob.put(fo.name, content);
						}
					} catch (Exception e) {
						return "错误数据类型" + e.getMessage();
					}

				}
				if (isBadSheetRow) {
					continue;
				}
				for (FieldObject field : fieldObject) {
					if (!ob.containsField(field.name)) {
						continue;
					}
				}
				dataList.add(ob);
			}
			dataNum += dataList.size();
			insertTable(db, dbtable.tableName, dataList);
		}
		long endTime = System.currentTimeMillis();
		return "导入" + dbtable.tableName + "表，共" + dataNum + "条数据，共花了"
				+ (endTime - starTime) + "毫秒,时间:" + DateTime.date("HH:mm:ss");

	}

	/**
	 * 
	 * @author Huang
	 * @date 2013-5-25 下午2:21:14
	 * @return int
	 */
	private static int strType2intType(String type) {
		int intType = 1;
		if (type.equalsIgnoreCase("int")) {
			intType = 1;
		} else if (type.equalsIgnoreCase("boolean")) {
			intType = 2;
		} else if (type.equalsIgnoreCase("long")) {
			intType = 3;
		} else if (type.equalsIgnoreCase("string")) {
			intType = 4;
		}
		return intType;
	}

	/**
	 * 往数据库插入数据
	 * 
	 * @author Huang
	 * @date 2013-5-25 下午3:05:04
	 * @return void
	 */
	public static void insertTable(DB db, String table, List<DBObject> dataList)
			throws UnknownHostException, MongoException {
		DBCollection selTable = db.getCollection(table);
		selTable.insert(dataList);
	}

	/**
	 * 清空该表
	 * 
	 * @author Huang
	 * @date 2013-5-25 下午3:42:25
	 * @return void
	 */
	public static void clearTable(DB db, String table) {
		DBCollection selTable = db.getCollection(table);
		selTable.drop();
	}

}
