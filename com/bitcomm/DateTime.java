package com.bitcomm;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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

	String CSVString()
	{
		return "\"" + toStringDate() + " " + toStringTime() + "\""; 
	}
}
