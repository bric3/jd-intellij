package org.eclipse.jface.preference;

/**
 * Eclipse artifact needed by the Java Decompiler native libs.
 */
@Deprecated
public interface IPreferenceStore {
    boolean getBoolean(String preferenceName);

    void setValue(String preferenceName, String value);
}
