package com.bitcomm;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Formatter;

public class DateTime extends MeasureData{ 
	short year;
	byte month;
	byte day;
	byte hour;
	byte minute;
	boolean bValid;
	DateTime()
	{
		bValid = false;
	}
	DateTime(byte[] Data, int start)
	{
		year = (short)ToChar(Data,start);
		month = Data[start+2];
		day = Data[start+3];
		hour = Data[start+4];
		minute = Data[start+5];
		bValid = true;
	}
	DateTime(String strT) throws ParseException
	{
		//System.out.println("DATATIME IN:"+strT);
		strT = strT.replace("\"", "");
		String[] str = strT.split("[/| |:]");
		//System.out.println(strT);
		//System.out.println(str.length);
		year = (short)Integer.parseInt(str[0]);
		month = (byte)(Integer.parseInt(str[1]));
		day =(byte)Integer.parseInt(str[2]);
		hour = (byte)Integer.parseInt(str[3]);
		minute = (byte)Integer.parseInt(str[4]);
		bValid = true;
		//System.out.println("DATATIEM OUT:"+String.valueOf(year)+" "+String.valueOf(month));
	}
	
	void setTime(Calendar cal)
	{
		year = (short) cal.get(Calendar.YEAR);
		month = (byte) (cal.get(Calendar.MONTH) + 1);
		day = (byte) cal.get(Calendar.DAY_OF_MONTH);
		hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
		minute = (byte) cal.get(Calendar.MINUTE);
		bValid = true;
	}
	
	Calendar getTime()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day, hour, minute);
		return cal;
	}
	
	void addMinute(int Minute)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day, hour, minute);
		cal.add(Calendar.MINUTE, Minute);
		setTime(cal);
	}
	
	void addHour(int Hour)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day, hour, minute);
		cal.add(Calendar.HOUR, Hour);
		setTime(cal);
	}	
	
	byte[] ByteStream()
	{
		byte[] out = new byte[6];
		out[0]= (byte) ((year>>8)&0x00FF);
		out[1]= (byte) (year & 0x00FF);
		out[2]= month;
		out[3]=day;
		out[4]=hour;
		out[5]=minute;
		return out;
	}
	public String toStringDate()
	{
		if (bValid)
		{
			NumberFormat formater = new DecimalFormat("00");
			return formater.format(year%100)+"-"+formater.format(month)+"-"+formater.format(day);  
 		}
		else
			return new String("--------");
	}

	public String toStringTime()
	{
		if (bValid)
		{
			NumberFormat formater = new DecimalFormat("00");
			return formater.format(hour)+":"+formater.format(minute);  
 		}
		else
			return new String("--:--");
	}

	String toStringShortDate()
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%02d/%02d", month,day);
		return sb.toString();
	}
	String CSVString()
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("\"%04d/%02d/%02d %02d:%02d\"", year,month,day,hour,minute);
		return sb.toString();
 
	}
}
