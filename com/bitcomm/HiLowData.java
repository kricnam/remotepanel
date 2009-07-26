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
	int  nData;
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
	short nAttitude;
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
}
