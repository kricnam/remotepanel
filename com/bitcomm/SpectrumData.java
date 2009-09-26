package com.bitcomm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;

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

	
	void Save()throws Exception, IOException
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		int num = 1;
		File file=null;
		formatter.format("spectrum/%02d-%02d%02d%02d/", 
				nMachineNum,dateEnd.year%100,dateEnd.month,dateEnd.day);
		file = new File(sb.toString());
		if (!file.exists()) file.mkdirs();
		
		num+= (dateEnd.hour * 60 + dateEnd.minute ) / nCollectTime;
		
			sb = null;
			formatter=null;
			sb = new StringBuilder();
			formatter = new Formatter(sb);
			formatter.format("spectrum/%02d-%02d%02d%02d/%02d%02d%02d%s%03d",
				nMachineNum,dateEnd.year%100,dateEnd.month,dateEnd.day,
				dateEnd.year%100,dateEnd.month,dateEnd.day,
				(dateEnd.hour < 12)?"A":"P",num);
			file = new File(sb.toString()+".spc");
			num++;
		
		formatter = null;
		sb=null;
		sb = new StringBuilder();
		formatter = new Formatter(sb);
		
		FileWriter fout = new FileWriter(file);
	    
		sb.append("[Control]\r\nVersion=1.00\r\nPackets=1\r\n[Header]\r\n");
		formatter.format("ChNo=%d\r\nDatNo=%d\r\nDate=%04d/%02d/%02d\r\n"
				+"Time=%02d:%02d\r\nPT=%d\r\n",
				nMachineNum,DataNum,dateEnd.year,dateEnd.month,dateEnd.day,
				dateEnd.hour,dateEnd.minute,nCollectTime,Time);
		sb.append("CalibA=5.0\r\nCalibB=0.0\r\n");
		formatter.format("Dose=%s\r\nStateCode=%d\r\nHV=%s\r\nTemp=%s\r\n"+
				"LV=%s\r\nGPSDate=%04d%02d%d %02d:%02d\r\nN=%03d%02d%02d\r\n"+
				"E=%03d%02d%02d\r\nH=%d\r\nSAT=%d\r\n"+
				"GEOD=%d\r\nFOM=%d\r\n[Spectrum]\r\n",
				String.valueOf(nNaIData/10.0),nStatus,String.valueOf(nHVVolt/10.0),String.valueOf(nThermoral/10.0),
				String.valueOf(nBattVoltage/10.0),dateGPS.year,dateGPS.month,dateGPS.day,dateGPS.hour,dateGPS.minute,gps.laDegree,gps.laMinute,gps.laSecond10/10,
				gps.lgDegree,gps.lgMinute,gps.lgSecond10/10,gps.nAltitude,gps.nSatlite,
				gps.nGeoSys,cFOMA);
		
		for (int i=0;i<1000;i++)
		{
			sb.append(String.valueOf(Channel[i])+"\r\n");
		}
		
		fout.write(sb.toString());
		formatter.close();
		fout.close();
	}
}
