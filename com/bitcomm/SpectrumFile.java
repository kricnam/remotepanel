package com.bitcomm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	String getFileName(int num)
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("spectrum/%02d-%02d%02d%02d/%02d%02d%02d%s%03d",
				data.nMachineNum,data.dateEnd.year%100,data.dateEnd.month,data.dateEnd.day,
				data.dateEnd.year%100,data.dateEnd.month,data.dateEnd.day,
				(data.dateEnd.hour < 12)?"A":"P",num);
		return sb.toString();
	}

	static String getFileName(int n, DateTime time ) throws IOException
	{
		
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("spectrum/%02d-%02d%02d%02d/%02d%02d%02d%s%03d.spc",
				n,time.year%100,time.month,time.day,
				time.year%100,time.month,time.day,
				"A",1);
		//System.out.println(sb.toString());
		int PT = getPT(sb.toString()); 
		sb.delete(0, sb.length()+1);
		formatter.format("spectrum/%02d-%02d%02d%02d/%02d%02d%02d%s%03d.spc",
				n,time.year%100,time.month,time.day,
				time.year%100,time.month,time.day,
				(time.hour < 12)?"A":"P",
				(time.hour * 60 + time.minute ) / PT+1);
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	static int getPT(String filename) throws IOException
	{
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
	    				data.dateEnd.hour = (byte)Integer.parseInt(strLine.substring(0,i-1));
	    				data.dateEnd.minute = (byte)Integer.parseInt(strLine.substring(i+1));
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

}
