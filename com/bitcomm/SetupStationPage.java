package com.bitcomm;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SetupStationPage extends PreferencePage {
	Text StationName;
	Text urlPort;
	Text MachineNum;
	String strBase;
	@Override
	public boolean performOk() {
		// TODO Auto-generated method stub
		
		IPreferenceStore store = getPreferenceStore();
		if (StationName!=null)
			store.setValue(strBase+"_Name", StationName.getText());
		if (urlPort!=null)
			store.setValue(strBase+"_Port", urlPort.getText());
		if (MachineNum!=null)
			store.setValue(strBase+"_MNUM", MachineNum.getText());
		return super.performOk();

	}

	@Override
	protected void performDefaults() {
		// TODO Auto-generated method stub
		super.performDefaults();
		StationName.setText(getTitle());
		urlPort.setText("9998");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite comp= new Composite(parent,SWT.NONE);
		comp.setLayout(new GridLayout(2,false));
		IPreferenceStore  prefStore = getPreferenceStore();
		Label num = new Label(comp,SWT.LEFT);
		num.setText(ConstData.strStationName);
		strBase = getTitle();
		strBase=strBase.replace(" ", "_");
		
		
		StationName = new Text(comp,SWT.BORDER);
		StationName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		StationName.setText(prefStore.getString(strBase+"_Name"));
		
		new Label(comp,SWT.LEFT).setText(ConstData.strURLPort);
		urlPort = new Text(comp,SWT.BORDER);
		urlPort.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		urlPort.setText(prefStore.getString(strBase+"_Port"));
		
		new Label(comp,SWT.LEFT).setText(ConstData.strMachineNum);
		MachineNum = new Text(comp,SWT.BORDER);
		MachineNum.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		MachineNum.setText(prefStore.getString(strBase+"_MNUM"));
		return comp;

	}

}
