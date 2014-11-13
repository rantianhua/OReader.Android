package cn.bandu.oreader.data;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface AppPrefs {

    @DefaultBoolean(true)
    boolean splash();
}
