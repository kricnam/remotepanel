package com.bitcomm;

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


}
