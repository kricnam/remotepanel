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
		new Label(comp,SWT.LEFT).setText("站点数量");
		StationNum = new Text(comp,SWT.BORDER);
		StationNum.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		StationNum.setText(prefStore.getString("StationNum"));
		new Label(comp,SWT.LEFT).setText("服务器URL");
		urlAddress = new Text(comp,SWT.BORDER);
		urlAddress.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		urlAddress.setText(prefStore.getString("ServerURL"));
		
		return comp;
	}
	
	protected void performDefaults(){
		
		IPreferenceStore store = getPreferenceStore();
		StationNum.setText("4");//(store.getDefaultString("StationNum"));
		urlAddress.setText("http://localhost");//store.getDefaultString("ServerURL"));
		
		
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
			store.setValue("ServerURL", urlAddress.getText());
		
		return super.performOk();
	}

}
