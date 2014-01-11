chipper {

//  baseWMS = "http://omar.ngaiost.org/cgi-bin/mapserv.sh"
  baseWMS = "http://${chipper.NetUtil.ipAddress}/cgi-bin/mapserv"


	chipImage {
		orthoImage='/data/celtic/staged/001/celtic/rpf__cadrg_1060889977_67001/a.toc'
	}	

	panSharpen {
		colorImage='/data1/test/data/geoeye1/GE1_Hobart_GeoStereo_NITF-NCD/001508507_01000SP00332258/5V090205M0001912264B220000100072M_001508507/Volume1/5V090205M0001912264B220000100072M_001508507.ntf'
		panImage='/data1/test/data/geoeye1/GE1_Hobart_GeoStereo_NITF-NCD/001508507_01000SP00332258/5V090205P0001912264B220000100282M_001508507/Volume1/5V090205P0001912264B220000100282M_001508507.ntf'		
	}

	twoColorMulti {
		redImage='/data1/space_coast_metric_private/3V050726P0000820271A0100007003410_00574200.ntf'
		blueImage='/data1/space_coast_metric_private/po_176062_pan_0000000.tif'		
	}

	hillShade {
//		mapImage='/data1/ossim-dem-test/san_fran.tif'
		mapImage='/data/sanfran/sanfran_map.tif'
		elevationPath='/data1/ossim-dem-test'
	}
}
