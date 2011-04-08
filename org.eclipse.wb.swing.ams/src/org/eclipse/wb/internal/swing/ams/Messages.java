package org.eclipse.wb.internal.swing.ams;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
  private static final String BUNDLE_NAME = "org.eclipse.wb.internal.swing.ams.messages"; //$NON-NLS-1$
  public static String ConfigurationReader_errGroup_noDescriptionAttribute;
  public static String ConfigurationReader_errGroup_noNameAttribute;
  public static String ConfigurationReader_errProperty_noNameAttribute;
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
