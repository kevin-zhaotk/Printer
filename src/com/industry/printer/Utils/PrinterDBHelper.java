package com.industry.printer.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PrinterDBHelper extends SQLiteOpenHelper {

	public static PrinterDBHelper dbHelper;
	private static final String DATABASE_NAME = "printer_database";
	private static final String TABLE_COUNT_NAME = "sys_config_count";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DB_CREATE_SQL = "create table if not exists sys_config_count(name varchar primary key,"
			+ "value integer)";
	
	public static PrinterDBHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new PrinterDBHelper(context);
		}
		return dbHelper;
	}
	
	public PrinterDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	public PrinterDBHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public PrinterDBHelper(Context context) {
		this(context, DATABASE_NAME, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_CREATE_SQL);
		// 添加默认记录值
		// db.execSQL("insert into printer_database(name, value) values('count', 0)");
		ContentValues values = new ContentValues();
		values.put("name", "count");
		values.put("value", 0);
		db.insert(TABLE_COUNT_NAME, null, values);
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	
	public void updateCount(Context context, int count) {
		PrinterDBHelper dbHelper = getInstance(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", "count");
		values.put("value", count);
		String where = "name = count";
		db.update(DATABASE_NAME, values, where, null);
		db.close();
	}
	
	public int getCount(Context context) {
		PrinterDBHelper dbHelper = getInstance(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] columns = {"value"}; 
		Cursor cursor = db.query(TABLE_COUNT_NAME, columns, null, null, null, null, null);
		int count = cursor.getInt(0);
		return count;
	}
}
