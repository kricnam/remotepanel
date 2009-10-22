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
		laDegree = Data[start];
		laMinute = Data[start+1];
		laSecond10 = (short)ToChar(Data, start+2);
		lgDegree = Data[start+4];
		lgMinute = Data[start+5];
		lgSecond10 = (short)ToChar(Data,start+6);
		nAltitude = (short)ToChar(Data,start+8);
		nSatlite = Data[start+10];
		nGeoSys = Data[start+11];
	}
	GPSData(String N,String E,String H,String SAT, String GEOD)
	{
		laDegree = (byte) Integer.parseInt(N.substring(0, 1));
		laMinute = (byte) Integer.parseInt(N.substring(2, 3));
		laSecond10 = (short)(Integer.parseInt(N.substring(4, 6)));
		lgDegree = (byte)(Integer.parseInt(E.substring(0, 2)));
		lgMinute = (byte) Integer.parseInt(E.substring(3, 4));
		lgSecond10 = (short)(Integer.parseInt(E.substring(5, 7)));
		nAltitude = (short)(Integer.parseInt(H));
		nSatlite = (byte)(Integer.parseInt(SAT));
		nGeoSys = (byte)(Integer.parseInt(GEOD));

	}
	
	public GPSData() {
		lgDegree = 0;
		lgMinute = 0;
		lgSecond10 = (short)0;
		laDegree = 0;
		laMinute = 0;
		laSecond10 = (short)0;
		nAltitude = (short)0;
		nSatlite = 0;
		nGeoSys = 0;
	}
	String CSVString()
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%02d%02d%03d,%03d%02d%03d,%d,%d,%d", laDegree,laMinute,laSecond10,
				lgDegree,lgMinute,lgSecond10,nAltitude,nSatlite,nGeoSys);
		return sb.toString();
	}
}
