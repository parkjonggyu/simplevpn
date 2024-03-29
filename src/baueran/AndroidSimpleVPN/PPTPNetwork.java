package baueran.AndroidSimpleVPN;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PPTPNetwork extends VPNNetwork
{
	private boolean encEnabled = false;

	public void setEncEnabled(boolean encEnabled)
	{
		this.encEnabled = encEnabled;
	}

	public boolean isEncEnabled()
	{
		return encEnabled;
	}

	@Override
	public boolean isInDB(Context ctx)
	{
		DatabaseAdapter adapter = new DatabaseAdapter(ctx);
		Cursor result = adapter.getPPTPCursor();
		
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext())
    		if (result.getString(0).equals(getName()))
    			return true;

		return false;
	}
	
	@Override
	public long write(Context ctx) 
	{
		DatabaseAdapter adapter = new DatabaseAdapter(ctx);
		ContentValues values =  new ContentValues();

		long errorCode = 0;
		int rowId = -1;
		
		Cursor cursor = adapter.getPPTPCursor();
		for (;cursor.moveToNext();) {
			if (cursor.getString(0).equals(getName())) {
				rowId = cursor.getPosition() + 1;
				break;
			}
		}
		
		values.put("name",     getName());
		values.put("server",   getServer());
		values.put("enc",      isEncEnabled()? "1" : "0");
		values.put("domains",  getDomains() != null? getDomains() : "");
		values.put("username", getEncUsername());
		values.put("password", getEncPassword());

		if (rowId > 0) 
			errorCode = adapter.update(rowId, "pptp", values);
		else {
			errorCode = adapter.insert("pptp", values);
			
			// Add VPN account name to list of stored and available VPNs
			// to be presented by ShowAllVPNsActivity
			values = new ContentValues();
			values.put("name", getName());
			values.put("type", "PPTP");
			errorCode = adapter.insert("vpn", values);
		}
		
		return errorCode;
	}
}
