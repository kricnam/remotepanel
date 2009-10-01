package com.bitcomm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Formatter;

public class DoesRateFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static String strHead="ChNo,DatNo,Date,Flag,NaI Dose Rate,Unit,NaI Counts(DR),NaI Counts(CR),SSD Dose Rate,Unit,SSD Counts(DR),PT,MT,Status,HV,Temp,LV,GPSDate,N,E,H,SAT,GEOD,FOM";
	ArrayList<DoesRateData>  dataArray;
	
	public DoesRateFile(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DoesRateFile(URI arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public DoesRateFile(String arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public DoesRateFile(File arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public DoesRateFile(int n,DateTime date)
	{
		super(getFileName(n,date));
	}
	
	static String getFileName(int nMachineNum,DateTime date)
	{
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("root/S%02d%02d%02d%02d00.dat", nMachineNum,
				date.year%100,date.month,date.day);
		return sb.toString();
	}
	
	void setData(DoesRateData data) throws IOException
	{
		if (dataArray==null) 
		{
			if (exists())
				load();
			else
				dataArray = new ArrayList<DoesRateData>();
		}
		int index = (data.date.hour * 60 + data.date.minute) / data.cPT;

		while (dataArray.size()< index+1)
			dataArray.add(null);
		dataArray.set(index, data);
	}
	
	void load() throws IOException
	{
	    FileInputStream fstream = new FileInputStream(this);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	   
	    strLine = br.readLine();
	    //Head
	    if (strLine.indexOf("ChNo,DatNo,Date,")>-1)
	    {
	    	dataArray = new ArrayList<DoesRateData>();
	    	int index=0;
	    	while((strLine=br.readLine())!=null)
	    	{
	    		DoesRateData does;
				try {
					does = new DoesRateData(strLine);
				} 
				catch (ParseException e) 
				{
					// TODO Auto-generated catch block
					does = null;
					//e.printStackTrace();
				} 
				catch (NumberFormatException ee)
				{
					does = null;
					//ee.printStackTrace();
				}
				if (does!=null)
					index = (does.date.hour*60+does.date.minute)/does.cPT;
				
				if (dataArray.size()< index+1) 
				{
					for (int i=0; i< index-dataArray.size()+1;i++)
						dataArray.add(null);
				}
				//System.out.print(String.valueOf(index)+"->");
				dataArray.set(index++, does);
				//System.out.println(String.valueOf(index));
	    		//if (does!=null) System.out.println(does.CSVString());
	    	}
	    }
	}
	
	void save() throws IOException
	{
		FileWriter fout = new FileWriter(this,false);
		fout.write(strHead+"\r\n");
		DoesRateData data;
		for(int i=0;i<dataArray.size();i++)
		{
			if ((data=dataArray.get(i))!=null)
			{
				fout.write(data.CSVString());
			}
			fout.write("\r\n");
		}
		
		fout.close();

	}

}
