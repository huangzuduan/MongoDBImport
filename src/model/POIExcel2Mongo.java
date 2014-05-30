package model;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
public class POIExcel2Mongo {

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
	 * 字段信息
	 * 
	 * @author YW0071
	 * 
	 */
	enum FieldType {
		TypeInt("int"), TypeBool("boolean"), TypeLong("long"), TypeString(
				"string"), TypeDouble("double");

		FieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		String fieldName;
		String fieldType;

		static FieldType valueOfStr(String fieldType) {
			for (FieldType ftype : values()) {
				if (ftype.fieldType.equalsIgnoreCase(fieldType)) {
					return ftype;
				}
			}
			return null;
		}
	}

	/**
	 * 程序主入口仅支持 1997-2003
	 * 
	 * @author Huang
	 * @date 2013-5-29 上午11:54:54
	 * @return void
	 * @throws Exception
	 */
	public static String importDB(String filePath) throws Exception {

		int dataNum = 0;
		long starTime = System.currentTimeMillis();
		DbTableObject dbtable = new DbTableObject();
		File f = new File(filePath);

		Workbook book = null;
		if (filePath.endsWith(".xls")) {
			NPOIFSFileSystem fs = new NPOIFSFileSystem(f);
			book = new HSSFWorkbook(fs.getRoot(), true);
		} else if (filePath.endsWith(".xlsx")) {
			OPCPackage pkg = OPCPackage.open(f);
			book = new XSSFWorkbook(pkg);
		} else {
			return "文件格式有错误!";
		}

		Sheet sheetHead = book.cloneSheet(0); // 获得第一个工作表对象

		int lows = sheetHead.getLastRowNum() - sheetHead.getFirstRowNum() + 1;
		if (lows < 3) {
			return "lows is letter 3";
		}
		int findFlag = 0x0000;
		Row row2 = sheetHead.getRow(1);
		int columns = row2.getLastCellNum() - row2.getFirstCellNum() + 1;
		for (int j = 0; j < columns; j++) {// 列
			Cell cell = row2.getCell(j); // 获得单元格
			if (cell == null) {
				continue;
			}
			String content = cell.getStringCellValue();
			if (content.indexOf("*") > -1) {
				dbtable.dbName = content.substring(1).trim(); // 1
				findFlag |= 0x0010;
			}
			if (content.indexOf("#") > -1) {
				dbtable.tableName = content.substring(1).trim(); // 2
				findFlag |= 0x0001;
			}
			if (findFlag == 0x0011) {
				break;
			}
		}

		List<FieldType> fieldObj = new ArrayList<>();
		for (int j = 0; j < columns; j++) {// 列
			Cell cell = sheetHead.getRow(2).getCell(j); // 获得单元格
			if (cell == null) {
				continue;
			}
			String content = cell.getStringCellValue().trim();
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
			FieldType fo = FieldType.valueOfStr(type);
			if (fo == null) {
				return "字段类型定义错误" + type;
			}
			fo.fieldName = fieldName;
			fieldObj.add(fo);
		}

		Config.init(XMLManger.readXml(System.getProperty("user.dir")
				+ "/config.xml"));

		db = MongoDB.initDB(Config.getString("dbHost"),
				Config.getInt("dbPort"), Config.getString("dbName"),
				Config.getString("dbUser"), Config.getString("dbPass"));
		clearTable(db, dbtable.tableName);

		int sheets = book.getNumberOfSheets();
		for (int ii = 0; ii < sheets; ii++) {
			Sheet sheet = book.getSheetAt(ii);
			// 判断该表是否为其他策划工具表
			int iLows = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1;
			if (iLows < 3) {
				continue;
			}
			Cell iCell = sheet.getRow(2).getCell(1); // 获得单元格
			if (iCell == null) {
				continue;
			}
			String iContent = iCell.getStringCellValue();
			if (iContent == null || iContent.equals("")) {
				continue;
			}
			List<DBObject> dList = new ArrayList<>();
			for (int i = 3; i < iLows; i++) {
				DBObject ob = new BasicDBObject();
				boolean isBadSheetRow = false;
				for (int j = 0; j < fieldObj.size(); j++) {
					Cell cell = sheet.getRow(i).getCell(j); // 获得单元格
					String content = cell.toString().trim();
					if (j == 0 && content.equals("")) {
						isBadSheetRow = true;
						break;
					}
					try {
						FieldType fo = fieldObj.get(j);

						switch (fo) {
						case TypeInt:
							ob.put(fo.fieldName,
									(int) Double.parseDouble(content));
							break;
						case TypeBool:
							ob.put(fo.fieldName, Boolean.valueOf(content));
							break;
						case TypeString:
							ob.put(fo.fieldName, Long.parseLong(content));
							break;
						case TypeLong:
							ob.put(fo.fieldName, Long.parseLong(content));
							break;
						case TypeDouble:
							ob.put(fo.fieldName, Double.parseDouble(content));
							break;

						default:
							return "错误数据类型,未能识别(行:" + (i + 1) + ",列" + (j + 1)
									+ ")" + fo.fieldType + "=>" + content;
						}
					} catch (Exception e) {
						return "错误数据类型错误(行:" + (i + 1) + ",列" + (j + 1) + ")"
								+ "=>" + content;
					}

				}
				if (isBadSheetRow) {
					continue;
				}
				for (FieldType field : fieldObj) {
					if (!ob.containsField(field.fieldName)) {
						return "该" + field.fieldName + "缺少字段的赋值";
					}
				}
				dList.add(ob);
			}
			dataNum += dList.size();
			insertTable(db, dbtable.tableName, dList);
		}
		long endTime = System.currentTimeMillis();
		return "导入" + dbtable.tableName + "表，共" + dataNum + "条数据，共花了"
				+ (endTime - starTime) + "毫秒,时间:" + DateTime.date("HH:mm:ss");

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
