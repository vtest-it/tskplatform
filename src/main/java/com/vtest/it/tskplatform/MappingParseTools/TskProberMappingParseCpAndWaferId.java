package com.vtest.it.tskplatform.MappingParseTools;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 
 * @author shawn.sun 
 * @category IT
 * @since 2017.05.26
 * @serial 1.0
 */
@Service
public class TskProberMappingParseCpAndWaferId {
	public  String  parse(File file) throws IOException
	{
		FileInputStream fileInputStream=new FileInputStream(file);
		byte[] bs=new byte[10000];
		fileInputStream.read(bs);
		fileInputStream.close();
		String Device=null;
		byte[] Device_BT=new byte[16];
		String Wafer_ID=null;
		byte[] Wafer_ID_BT=new byte[22];
		for (int i = 0; i < 216; i++) {
			if (i>19&&i<36) {
				Device_BT[i-20]=bs[i];
			}
			if (i>59&&i<81) {
				Wafer_ID_BT[i-60]=bs[i];
			}
		}
		Device=new String(Device_BT);
		Wafer_ID=new String(Wafer_ID_BT);
		String cp=Device.substring(3,4);
		return Wafer_ID.trim()+":CP"+cp;
	}
}
