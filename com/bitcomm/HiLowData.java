/**
 * 
 */
package com.bitcomm;
/**
 * @author mxx
 *
 */
public class HiLowData extends MeasureData{
	final static byte	nGyh = 1;
	final static byte	uGyh = 2;
	final static byte	mGyh = 3;
	final static byte	NaI=1;
	final static byte   SSD=2;

	short DataLength;
	char DataNum;
	DateTime date;
	Byte cNaIUnit;
	int  nNaIValue;
	int  nNaI_dr_count;
	int  nNaI_cr_count;
	byte cSSDUnit;
	int  nSSDrate;
	int  nSSD_dr_count;
	byte cPT;
	short nMTime;
	int  nStatus;
	short nHVVolt;
	short nThermoral;
	short nBattVoltage;
	DateTime dateGPS;
	GPSData gps;
	byte cFOMA;
	byte cValidType;
	
	HiLowData(byte[] Data)
	{
		DataLength =(short) ToChar(Data,5);
		DataNum = (char) ToChar(Data,7);
		date = new DateTime(Data,9);
		cNaIUnit = Data[15];
		nNaIValue = ToInt(Data,16);
		nNaI_dr_count = ToInt(Data,20);
		nNaI_cr_count = ToInt(Data,24);
		cSSDUnit = Data[28];
		nSSDrate = ToInt(Data,29);
		nSSD_dr_count = ToInt(Data,33);
		cPT = Data[34];
		nMTime = (short)ToChar(Data,38);
		nStatus = ToInt(Data,40);
		nHVVolt = (short)ToChar(Data,44);
		nThermoral = (short)ToChar(Data,46);
		nBattVoltage = (short)ToChar(Data,48);
		dateGPS = new DateTime(Data,50);
		gps = new GPSData(Data,56);
		cFOMA = Data[68];
		cValidType = Data[69];
	}
}
