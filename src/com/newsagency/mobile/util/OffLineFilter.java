/**@文件:OffLineFilter.java
 * @作者:dkslbw@gmail.com
 * @日期:2014年9月28日 下午6:02:15*/
package com.newsagency.mobile.util;

import java.io.File;
import java.io.FilenameFilter;

/**@公司: 南京红松信息技术有限公司
 * @CLASS:OffLineFilter
 * @描述:	
 * @作者:dkslbw@gmail.com
 * @版本:v1.0
 * @日期:2014年9月28日 下午6:02:15*/

public class OffLineFilter implements FilenameFilter{   
	  
	  public boolean isOff(String file) {   
	    if (file.toLowerCase().startsWith("offline_")){   
	      return true;   
	    }else{   
	      return false;   
	    }   
	  }   
	 
	  public boolean accept(File dir,String fname){   
	    return (isOff(fname));   
	  
	  }   
	  
	}   
