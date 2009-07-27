package com.bitcomm;

public class DateTime extends MeasureData{ 
	short year;
	byte month;
	byte day;
	byte hour;
	byte minute;
	DateTime(byte[] Data, int start)
	{
		year = (short)ToChar(Data,start);
		month = Data[start+2];
		day = Data[start+3];
		hour = Data[start+4];
		minute = Data[start+5];
	}
}
