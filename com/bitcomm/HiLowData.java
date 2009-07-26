/**
 * 
 */
package com.bitcomm;
/**
 * @author mxx
 *
 */
public class HiLowData {
	public enum ValueUnit {
		non,
		nGyh,
		uGyh,
		mGyh
	};
	short DataLength;
	char DataNum;
	DateTime date;
	byte cNaIUnit;
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
	short nVoltage;
	DateTime dateGPS;
	byte lgDegree;
	byte lgMinute;
	short lgSecond10;
	byte laDegree;
	byte laMinute;
	short laSecond10;
	short nAltitude;
	byte nSatlite;
	byte nGeoSys;
	byte cFOMA;
	byte cValidType;
	enum ValidType
	{
		none,
		NaI,
		SSD
	};
	int ToChar(byte Hi,byte Low)
	{
		return Hi << 8 + Low;
	}
	int ToChar(byte[] data, int start)
	{
		return data[start] << 8 + data[start+1];
	}
	int ToInt(byte Hi1,byte Hi2,byte Low1,byte Low2)
	{
		return  Hi1 << 24 + Hi2 << 16 + Low1 << 8 + Low2;
	}
	int ToInt(byte[] data,int start)
	{
		return  data[start]<<24 + data[start+1] << 16 
		+ data[start+2] << 8 + data[start+3];
	}

	HiLowData(byte[] Data)
	{
		DataLength =(short) ToChar(Data,5);
		DataNum = (char) ToChar(Data,7);
		date.year = (short)ToChar(Data,9);
		date.month = Data[11];
		date.day = Data[12];
		date.hour = Data[13];
		date.minute = Data[14];
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
		nVoltage = (short)ToChar(Data,48);
		dateGPS.year = (short)ToChar(Data,50);
		dateGPS.month = Data[52];
		dateGPS.day = Data[53];
		dateGPS.hour = Data[54];
		dateGPS.minute = Data[55];
		lgDegree = Data[56];
		lgMinute = Data[57];
		lgSecond10 = (short)ToChar(Data, 58);
		laDegree = Data[60];
		laMinute = Data[61];
		laSecond10 = (short)ToChar(Data,62);
		nAltitude = (short)ToChar(Data,64);
		nSatlite = Data[66];
		nGeoSys = Data[67];
		cFOMA = Data[68];
		cValidType = Data[69];
	}
}
