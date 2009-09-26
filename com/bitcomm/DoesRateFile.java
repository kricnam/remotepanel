package com.bitcomm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class DoesRateFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<HiLowData>  dataArray;
	
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
	    	dataArray = new ArrayList<HiLowData>();
	    	
	    }
	    	

	}

}
