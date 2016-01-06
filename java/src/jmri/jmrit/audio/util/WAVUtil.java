package jmri.jmrit.audio.util;

import com.jogamp.openal.ALConstants;
import com.jogamp.openal.ALException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Extracted out from jogamp
 * 
 * @author Matthew Harris
 */
public class WAVUtil {
    
  public static void wavutilLoadWAVFile(final String fileName,
                                     final int[] format,
                                     final ByteBuffer[] data,
                                     final int[] size,
                                     final int[] freq,
                                     final int[] loop) throws ALException {
    try {
      final WAVData wd = WAVLoader.loadFromFile(fileName);
      format[0] = wd.format;
      data[0] = wd.data;
      size[0] = wd.size;
      freq[0] = wd.freq;
      loop[0] = wd.loop ? ALConstants.AL_TRUE : ALConstants.AL_FALSE;
    } catch (final Exception e) {
      throw new ALException(e);
    }
  }

  public static void wavutilLoadWAVFile(InputStream stream,
                                     final int[] format,
                                     final ByteBuffer[] data,
                                     final int[] size,
                                     final int[] freq,
                                     final int[] loop) throws ALException {
    try {
      if (!(stream instanceof BufferedInputStream)) {
        stream = new BufferedInputStream(stream);
      }
      final WAVData wd = WAVLoader.loadFromStream(stream);
      format[0] = wd.format;
      data[0] = wd.data;
      size[0] = wd.size;
      freq[0] = wd.freq;
      loop[0] = wd.loop ? ALConstants.AL_TRUE : ALConstants.AL_FALSE;
    } catch (final Exception e) {
      throw new ALException(e);
    }
  }
}
