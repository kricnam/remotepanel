package com.bitcomm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class SpectrumFile extends File {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm");
	SpectrumData data;
//	String getFileName(int num)
//	{
//		StringBuilder sb = new StringBuilder();
//		Formatter formatter = new Formatter(sb);
//		formatter.format("spectrum/%02d-%02d%02d%02d/%02d%02d%02d%s%03d",
//				data.nMachineNum,data.dateEnd.year%100,data.dateEnd.month,data.dateEnd.day,
//				data.dateEnd.year%100,data.dateEnd.month,data.dateEnd.day,
//				(data.dateEnd.hour < 12)?"A":"P",
//				num);
//		return sb.toString();
//	}
	static String getFileName(int n, DateTime time,int PT ) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("spectrum/%02d-%02d%02d%02d/%02d%02d%02d%s%03d.spc",
				n,time.year%100,time.month,time.day,
				time.year%100,time.month,time.day,
				(time.hour < 12)?"A":"P",
				(time.hour < 12)?(time.hour * 60 + time.minute ) / PT+1:((time.hour -12) * 60 + time.minute ) / PT+1);
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	static String getName(int n, DateTime time,int PT ) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%02d%02d%02d%s%03d",
				time.year%100,time.month,time.day,
				(time.hour < 12)?"A":"P",
				(time.hour < 12)?(time.hour * 60 + time.minute ) / PT+1:((time.hour -12) * 60 + time.minute ) / PT+1);
		//System.out.println(sb.toString());
		return sb.toString();
	}

	static String getFileName(int n, DateTime time ) throws IOException
	{
		int PT = getPT(n,time); 
		
		if (PT==-1) 
			{
				FileNotFoundException e = new FileNotFoundException();
				throw e;
			}
		
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("spectrum/%02d-%02d%02d%02d/%02d%02d%02d%s%03d.spc",
				n,time.year%100,time.month,time.day,
				time.year%100,time.month,time.day,
				(time.hour < 12)?"A":"P",
				(time.hour < 12)?(time.hour * 60 + time.minute ) / PT+1:((time.hour -12) * 60 + time.minute ) / PT+1);
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	
	static int getPT(int nMachine,DateTime date) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("spectrum/%02d-%02d%02d%02d",
				nMachine,date.year%100,date.month,date.day);
		File fl = new File(sb.toString());
		File [] files = fl.listFiles(new FilenameFilter(){

			public boolean accept(File dir, String name) {
				String filename = name.toLowerCase();  
				 if(filename.contains(".spc")) 	 return true;  
				 else return false;  
			}
		});
		
		if (files==null) return 10;
		
		int i=0;
		while (files.length>0)
		{
			String filename = files[i++].getAbsolutePath();
			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)
			{
				if (strLine.indexOf("PT=")>-1)
				{
					return Integer.parseInt(strLine.substring(strLine.indexOf("=")+1)); 
				}
			}
		};
		return -1;
	}
	
	public SpectrumFile(String arg0) throws IOException, ParseException {
		super(arg0);
	    FileInputStream fstream = new FileInputStream(arg0);
	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    //Read File Line By Line
	    strLine = br.readLine();
	    if (strLine.indexOf("[Control]")>-1)
	    {
	    	data = new SpectrumData();
	    	while ((strLine = br.readLine()) != null)   {
	    		if (strLine.indexOf("DatNo")>-1)
	    		{
	    			//System.out.println("DatNo");
	    			data.DataNum = (short)Integer.parseInt(strLine.substring(strLine.indexOf("=")+1));
	    			continue;
	    		}
	    		if (strLine.indexOf("Date")==0)
	    		{
	    			//System.out.println("Date");
	    			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd");
	    			Date date = formatter1.parse(strLine.substring(strLine.indexOf("=")+1));
	    			Calendar cal = Calendar.getInstance();
	    			cal.setTime(date);
	    			data.dateEnd.year = (short) cal.get(Calendar.YEAR);
	    			data.dateEnd.month = (byte) (cal.get(Calendar.MONTH) + 1);
	    			data.dateEnd.day = (byte) cal.get(Calendar.DAY_OF_MONTH);
	    			continue;
	    		}
	    		if (strLine.indexOf("Time")>-1)
	    		{
	    			//System.out.println("Time");
	    			strLine = strLine.substring(strLine.indexOf("=")+1);
	    			strLine = strLine.trim();
	    			int i = strLine.indexOf(":");
	    			if (i>0)
	    			{
	    				//System.out.println(strLine);
	    				data.dateEnd.hour = (byte)Integer.parseInt(strLine.substring(0,i));
	    				data.dateEnd.minute = (byte)Integer.parseInt(strLine.substring(i+1));
	    				//System.out.println(String.valueOf(data.dateEnd.hour));
	    				//System.out.println(String.valueOf(data.dateEnd.minute));
	    				data.dateEnd.bValid = true;
	    			}
	    			
	    			continue;
	    		}
	    		if (strLine.indexOf("[Spectrum]")>-1)
	    		{
	    			int i = 0;
	    			for(i=0;i<data.Channel.length;i++)
	    			{
	    				strLine = br.readLine();
	    				if (strLine!=null)
	    					data.Channel[i]=Integer.parseInt(strLine);
	    				//System.out.println(String.valueOf(i)+": "+String.valueOf(data.Channel[i]));
	    			}
	    			break;
	    		}
	    	};
	    	
	    }
	    //Close the input stream
	    in.close();
	}
	
	public SpectrumFile(SpectrumData Data) throws IOException {
		super(SpectrumFile.getFileName(Data.nMachineNum, 
				Data.dateEnd,
				Data.nCollectTime));
	}

	void Save(SpectrumData Data) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		
		File file=null;
		formatter.format("spectrum/%02d-%02d%02d%02d/", 
				Data.nMachineNum,Data.dateEnd.year%100,Data.dateEnd.month,
				Data.dateEnd.day);
		file = new File(sb.toString());
		if (!file.exists()) file.mkdirs();
		
		formatter = null;
		sb=null;
		sb = new StringBuilder();
		formatter = new Formatter(sb);
		
		FileWriter fout = new FileWriter(this);
	    
		sb.append("[Control]\r\nVersion=1.00\r\nPackets=1\r\n[Header]\r\n");
		formatter.format("ChNo=%d\r\nDatNo=%d\r\nDate=%04d/%02d/%02d\r\n"
				+"Time=%02d:%02d\r\nPT=%d\r\n",
				Data.nMachineNum,Data.DataNum,Data.dateEnd.year,Data.dateEnd.month,
				Data.dateEnd.day,
				Data.dateEnd.hour,Data.dateEnd.minute,Data.nCollectTime,Data.Time);
		sb.append("CalibA=5.0\r\nCalibB=0.0\r\n");
		formatter.format("Dose=%s\r\nStateCode=%s\r\nHV=%s\r\nTemp=%s\r\n"+
				"LV=%s\r\nGPSDate=%04d%02d%d %02d:%02d\r\nN=%03d%02d%02d\r\n"+
				"E=%03d%02d%02d\r\nH=%d\r\nSAT=%d\r\n"+
				"GEOD=%d\r\nFOM=%d\r\n[Spectrum]\r\n",
				String.valueOf(Data.nNaIData/10.0),Data.statusString(),String.valueOf(Data.nHVVolt/10.0),String.valueOf(Data.nThermoral/10.0),
				String.valueOf(Data.nBattVoltage/10.0),Data.dateGPS.year,
				Data.dateGPS.month,Data.dateGPS.day,Data.dateGPS.hour,Data.dateGPS.minute,
				Data.gps.laDegree,Data.gps.laMinute,Data.gps.laSecond10/10,
				Data.gps.lgDegree,Data.gps.lgMinute,Data.gps.lgSecond10/10,Data.gps.nAltitude,Data.gps.nSatlite,
				Data.gps.nGeoSys,Data.cFOMA);
		
		for (int i=0;i<1000;i++)
		{
			sb.append(String.valueOf(Data.Channel[i])+"\r\n");
		}
		
		fout.write(sb.toString());
		formatter.close();
		fout.close();

	}

}
