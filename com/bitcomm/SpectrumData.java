package com.bitcomm;

public class SpectrumData extends MeasureData{
	short DataLength;
	short DataNum;
	DateTime dateEnd;
	int []Channel;
	byte nCollectTime;
	short Time;
	int nStatus;
	int nNaIData;
	DateTime dateGPS;
	GPSData gps;
	byte cFOMA;
	short nHVVolt;
	short nThermoral;
	short nBattVoltage;

	SpectrumData(byte[] Data)
	{
		Channel = new int[1000];
		DataLength = (short)ToChar(Data,5);
		DataNum = (short)ToChar(Data,7);
		dateEnd = new DateTime(Data,9);
		for(int i=0;i<1000;i++)
			Channel[i]=ToInt(Data,i+15);
		nCollectTime = Data[4015];
		Time = (short)ToChar(Data,4016);
		nStatus = ToInt(Data,4018);
		nNaIData = ToInt(Data,4022);
		dateGPS = new DateTime(Data,4026);
		gps = new GPSData(Data,4032);
		cFOMA = Data[4044];
		nHVVolt = (short)ToChar(Data,4045);
		nThermoral = (short)ToChar(Data,4047);
		nBattVoltage = (short)ToChar(Data,4049);		
	}
}
