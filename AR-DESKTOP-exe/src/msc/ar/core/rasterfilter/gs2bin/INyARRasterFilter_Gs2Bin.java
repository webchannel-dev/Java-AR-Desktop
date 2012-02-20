package msc.ar.core.rasterfilter.gs2bin;

import msc.ar.NyARException;
import msc.ar.core.raster.NyARBinRaster;
import msc.ar.core.raster.NyARGrayscaleRaster;

public interface INyARRasterFilter_Gs2Bin
{
	public void doFilter(NyARGrayscaleRaster i_input, NyARBinRaster i_output) throws NyARException;

}
