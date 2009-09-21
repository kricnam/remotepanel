package com.bitcomm;

import java.util.Formatter;

public class GPSData extends MeasureData {
	byte lgDegree;
	byte lgMinute;
	short lgSecond10;
	byte laDegree;
	byte laMinute;
	short laSecond10;
	short nAltitude;
	byte nSatlite;
	byte nGeoSys;
	GPSData(byte[]Data, int start)
	{
		lgDegree = Data[start];
		lgMinute = Data[start+1];
		lgSecond10 = (short)ToChar(Data, start+2);
		laDegree = Data[start+4];
		laMinute = Data[start+5];
		laSecond10 = (short)ToChar(Data,start+6);
		nAltitude = (short)ToChar(Data,start+8);
		nSatlite = Data[start+10];
		nGeoSys = Data[start+11];
	}
	String ToString()
	{
		return String.valueOf((int)laDegree)+" "+String.valueOf((int)laMinute);
	}
	String CSVString()
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%03d%02d%02d,%03d%02d%02d,%d,%d,%d", laDegree,laMinute,lgSecond10/10,
				lgDegree,lgMinute,lgSecond10/10,nAltitude,nSatlite,nGeoSys);
		return sb.toString();
	}
}
