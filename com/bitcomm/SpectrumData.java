package com.bitcomm;

import java.io.IOException;

public class SpectrumData extends MeasureData{
	int nMachineNum;
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
		nMachineNum=Data[1];
		DataLength = (short)ToChar(Data,5);
		DataNum = (short)ToChar(Data,7);
		dateEnd = new DateTime(Data,9);
		for(int i=0;i<1000;i++)
			Channel[i]=ToInt(Data,i*4+15);
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
	
	public SpectrumData() {
		Channel = new int[1000];
		nMachineNum=0;
		DataLength = 0;
		DataNum = 0;
		dateEnd = new DateTime();
		for(int i=0;i<1000;i++)
			Channel[i]=0;
		nCollectTime = 0;
		Time = 0;
		nStatus = 0;
		nNaIData = 0;
		dateGPS = new DateTime();
		gps = new GPSData();
		cFOMA = 0;
		nHVVolt = 0;
		nThermoral = 0;
		nBattVoltage = 0;		
	}
	
	void AdjustTime(int nOffSet)
	{
		dateEnd.addHour(nOffSet);
		dateGPS.addHour(nOffSet);
	}
	
	
	void Save()throws Exception, IOException
	{
		SpectrumFile file = new SpectrumFile(this);
		file.Save(this);
	}
	
	String statusString()
	{
		if ((nStatus & 0x00000080) > 0 )
			return "Mentenance";
		if ((nStatus & 0x00000001) > 0 )
			return "MachineTrouble";
		if ((nStatus & 0x00000008) > 0 )
			return "PreampTrouble";
		if ((nStatus & 0x00000004) > 0 )
			return "MachineTrouble";
		if ((nStatus & (1 << 4 | 1 << 11 | 1 << 12|1 << 13)) > 0 )
			return "PreampTrouble";
		if ((nStatus & (1 << 16 | 1 << 17 | 1 << 18)) > 0 )
			return "ComErr";
		if ((nStatus & (1 << 8 | 1 << 9 | 1 << 10)) > 0 )
			return "Alarm";
		if ((nStatus & (1 << 19)) > 0 )
			return "Other";
		return "Normal";
	}
}
