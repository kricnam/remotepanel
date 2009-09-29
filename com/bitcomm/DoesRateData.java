/**
 * 
 */
package com.bitcomm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Formatter;

/**
 * @author mxx
 *
 */
public class DoesRateData extends MeasureData{
	final static byte	nGyh = 1;
	final static byte	uGyh = 2;
	final static byte	mGyh = 3;
	final static byte	NaI=1;
	final static byte   SSD=2;
	final static String strHead="ChNo,DatNo,Date,Flag,NaI Dose Rate,Unit,NaI Counts(DR),NaI Counts(CR),SSD Dose Rate,Unit,SSD Counts(DR),PT,MT,Status,HV,Temp,LV,GPSDate,N,E,H,SAT,GEOD,FOM";
	int nMachineNum;
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
	
	DoesRateData(String strLine) throws ParseException
	{
		parse(strLine);
	}
	
	DoesRateData(byte[] Data)
	{
		nMachineNum = Data[1];
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
		cPT = Data[37];
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
	
	String CSVString()
	{
		return String.valueOf(nMachineNum)+","+
		String.valueOf((int)DataNum) + "," +
		date.CSVString() + "," +
		String.valueOf((int)cValidType) + ","+
		String.valueOf((float)nNaIValue/10.0)+","+
		String.valueOf((int)cNaIUnit)+","+
		String.valueOf((int)nNaI_dr_count)+","+
		String.valueOf((int)nNaI_cr_count)+","+
		String.valueOf((int)nSSDrate)+","+
		String.valueOf((int)cSSDUnit)+","+
		String.valueOf((int)nSSD_dr_count)+","+
		String.valueOf((int)cPT)+","+
		String.valueOf((int)nMTime)+","+
		String.valueOf((int)nStatus)+","+
		String.valueOf((float)nHVVolt/10.0)+","+
		String.valueOf((float)nThermoral/10.0)+","+
		String.valueOf((float)nBattVoltage/10.0)+","+
		dateGPS.CSVString()+","+
		gps.CSVString()+","+
		String.valueOf((int)cFOMA);
	}
	void Save() throws Exception, IOException
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("root/S%02d%02d%02d%02d00.dat", nMachineNum,
				date.year%100,date.month,date.day);
		String strFileName = sb.toString();
		File file = new File(strFileName);
		boolean head = !file.exists();
		FileWriter fout = new FileWriter(file,true);
		if (head) fout.write(strHead+"\r\n");
		fout.write(CSVString()+"\r\n");
		fout.close();
	}
	void parse(String str) throws ParseException
	{
		String []fields=str.split(",");
		nMachineNum = Integer.parseInt(fields[0]);
		DataLength =(short) 0;
		DataNum = (char) Integer.parseInt(fields[1]);
		date = new DateTime(fields[2]);
		cValidType = (byte)Integer.parseInt(fields[3]);;
		cNaIUnit = (byte)Integer.parseInt(fields[5]);;
		nNaIValue = (int)(Double.parseDouble(fields[4])*10);
		nNaI_dr_count = Integer.parseInt(fields[6]);
		nNaI_cr_count = Integer.parseInt(fields[7]);
		
		nSSDrate = (int)(Double.parseDouble(fields[8])*10);
		cSSDUnit = (byte)Integer.parseInt(fields[9]);
		
		nSSD_dr_count = Integer.parseInt(fields[10]);
		cPT = (byte)Integer.parseInt(fields[11]);
		nMTime = (short)Integer.parseInt(fields[12]);
		nStatus = Integer.parseInt(fields[13]);
		
		nHVVolt = (short)(Double.parseDouble(fields[14])*10);
		nThermoral = (short)(Double.parseDouble(fields[15])*10);
		nBattVoltage = (short)(Double.parseDouble(fields[16])*10);
		dateGPS = new DateTime(fields[17]);
		gps = new GPSData(fields[18],fields[19],fields[20],fields[21],fields[22]);
		
		cFOMA = (byte)Integer.parseInt(fields[23]);
		
	}
}
