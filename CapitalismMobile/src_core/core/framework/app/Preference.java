package core.framework.app;

import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import core.framework.Core;

/**
 * 앱의 옵션을 관리한다.
 * 
 * @author 김현우
 */
public class Preference {
	
	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;
	
	private String mName;
	
	/*package*/ Preference() {
	}
	
	/*package*/ Preference(String name) {
		mName = name;
	}
	
	private void ensurePref() {
		if(mPref != null) return;
		mPref = Core.APP.getActivity().getSharedPreferences(
				(mName != null)? null : Preference.class.getSimpleName(), 0);
	}
	
	private void ensureEditor() {
		ensurePref();
		if(mEdit == null) mEdit = mPref.edit();
	}
	
	public String getString(String key, String defValue) {
		ensurePref();
		return mPref.getString(key, defValue);
	}
	
	public Set<String> getStringSet(String key, Set<String> defValues) {
		ensurePref();
		return mPref.getStringSet(key, defValues);
	}
	
	public int getInt(String key, int defValue) {
		ensurePref();
		return mPref.getInt(key, defValue);
	}
	
	public long getLong(String key, long defValue) {
		ensurePref();
		return mPref.getLong(key, defValue);
	}
	
	public float getFloat(String key, float defValue) {
		ensurePref();
		return mPref.getFloat(key, defValue);
	}
	
	public boolean getBoolean(String key, boolean defValue) {
		ensurePref();
		return mPref.getBoolean(key, defValue);
	}
	
	public Map<String, ?> getAll() {
		ensurePref();
		return mPref.getAll();
	}
	
	public boolean contains(String key) {
		ensurePref();
		return mPref.contains(key);
	}
	
	public Editor putString(String key, String value) {
		ensureEditor();
		return mEdit.putString(key, value);
	}
	
	public Editor putStringSet(String key, Set<String> values) {
		ensureEditor();
		return mEdit.putStringSet(key, values);
	}
	
	public Editor putInt(String key, int value) {
		ensureEditor();
		return mEdit.putInt(key, value);
	}
	
	public Editor putLong(String key, long value) {
		ensureEditor();
		return mEdit.putLong(key, value);
	}
	
	public Editor putFloat(String key, float value) {
		ensureEditor();
		return mEdit.putFloat(key, value);
	}
	
	public Editor putBoolean(String key, boolean value) {
		ensureEditor();
		return mEdit.putBoolean(key, value);
	}
	
	public Editor remove(String key) {
		ensureEditor();
		return mEdit.remove(key);
	}
	
	public Editor clear() {
		ensureEditor();
		return mEdit.clear();
	}
	
	public boolean commit() {
		ensureEditor();
		return mEdit.commit();
	}
	
	public void apply() {
		ensureEditor();
		mEdit.apply();
	}
	
	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		ensurePref();
		mPref.registerOnSharedPreferenceChangeListener(listener);
	}
	
	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		ensurePref();
		mPref.unregisterOnSharedPreferenceChangeListener(listener);
	}
	
}
