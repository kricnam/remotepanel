/**
 * 
 */
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

/**
 * @author mxx
 *
 */
public class SetupPage extends PreferencePage {
	Text StationNum;
	Text urlAddress;
	
	/* （非 Javadoc）
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		// TODO 自动生成方法存根
		Composite comp= new Composite(parent,SWT.NONE);
		comp.setLayout(new GridLayout(2,false));
		IPreferenceStore  prefStore = getPreferenceStore();
		Label num = new Label(comp,SWT.LEFT);
		num.setText(ConstData.strStationNum);
		num.setEnabled(false);
		StationNum = new Text(comp,SWT.BORDER);
		StationNum.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		StationNum.setText(prefStore.getString("StationNum"));
		StationNum.setEnabled(false);
		new Label(comp,SWT.LEFT).setText(ConstData.strURL);
		urlAddress = new Text(comp,SWT.BORDER);
		urlAddress.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		urlAddress.setText(prefStore.getString(ConstData.strKeyServerURL));
		
		return comp;
	}
	
	protected void performDefaults(){
		
		StationNum.setText("4");//(store.getDefaultString("StationNum"));
		urlAddress.setText("localhost");//store.getDefaultString("ServerURL"));
		
		
	}

	/* （非 Javadoc）
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		// TODO 自动生成方法存根
		IPreferenceStore store = getPreferenceStore();
		if (StationNum!=null)
			store.setValue("StationNum", StationNum.getText());
		if (urlAddress!=null)
			store.setValue(ConstData.strKeyServerURL, urlAddress.getText());
		
		return super.performOk();
	}

}
