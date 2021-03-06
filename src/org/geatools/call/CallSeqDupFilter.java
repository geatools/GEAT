/*******************************************************************************
 *  ========================================================================
 *  GEATools : a free Genomic Event Analysis Tools for the Java(tm) platform
 *  ========================================================================
 *
 *  (C) Copyright 2016, by Qi Wang and Contributors.
 *
 *  This file is part of GEATools.
 *
 *  GEATools is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 2.1 of the License, or
 *  (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 *  [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 *  Other names may be trademarks of their respective owners.]
 *
 *  (C) Copyright 2000-2014, by Original Author and Contributors.
 *
 *  Original Author: Qi Wang;
 *  Contributor(s): 
 *  Changes (from 1-Jan-2016)
 *  ---------------------------------------------------------------------------
 *******************************************************************************/
package org.geatools.call;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.geatools.GEAT;
import org.geatools.operation.FileOperate;
import org.geatools.seqprocess.SeqDupFilter;
import org.geatools.seqprocess.SeqOperation;

public class CallSeqDupFilter extends GEAT{
	
	public static void setHomeDir(String dir){
		homeDir=dir;
	}
	public static void setWorkingDir(String dir){
		workingDir=dir;
	}
	public static void setTmpDir(String dir){
		tmpDir=dir;
	}
	public static void delTmpDir(String dir){
		FileOperate.delFolder(dir);
	}
	
	public static void doWork(String[] args){		
		 
		 if(homeDir==null) homeDir=GEAT.getClassPath();			   
		 if(tmpDir==null){
		   String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		   tmpDir=homeDir+"/tmp/"+timeStamp;
		 } 
		 if(workingDir==null) workingDir=homeDir+"/working";
		 File dir=new File(tmpDir);
		 if(!dir.exists()) FileOperate.newFolder(tmpDir);
		 dir=null;
		 dir=new File(workingDir);
		 if(!dir.exists()) FileOperate.newFolder(workingDir);
		 dir=null;
			
		 Map<String, List<String>> params=getCommandLineParams(args);
		 if(params.get("task")!=null){	
			if(params.get("task").size()>0){
			  taskName=params.get("task").get(0).trim();
			  if(taskName!=null){
			    if(!taskName.equalsIgnoreCase("SeqDupFilter")){					  
			      System.err.println("Error: '-task' is invalid");
				  return;	
			    }
			  }
			}
		 }else{
		   System.err.println("Error: '-task' is invalid");
		   return;		
		 }
		 
		 if(params.get("fastq")!=null){
			 isFastqOK=false;
			 if(params.get("fastq").size()>0){
		       fastq=params.get("fastq").get(0).trim();
		       if(fastq!=null){
		    	 if(SeqOperation.isFASTQSeq(fastq)){
		    	   isFastqOK=true;
		    	   //doSeqQCFilter=true;
		    	   seqType=SeqOperation.SEQTYPE_SINGLEEND;
		    	 }else{
			       System.err.println("The -fastq file doesn't exist or isn't a fastq file:(");
			       return;
			     }		    	
		       }
		     }else{
		       System.err.println("You didn't provide fastq file :(");
		       return;
		     }
		   }
		   
		   if(params.get("fastq2")!=null){
			   isFastq2OK=false;
			   if(params.get("fastq2").size()>0){
			       fastq2=params.get("fastq2").get(0).trim();
			       if(fastq2!=null){
			    	 if(SeqOperation.isFASTQSeq(fastq2)){
			    	   isFastq2OK=true;
			    	   //doSeqQCFilter=true;
			    	   seqType=SeqOperation.SEQTYPE_PAIREND;
			    	 }else{
				       System.err.println("The -fastq2 file doesn't exist or isn't a fastq file :(");
				       return;
				     }			    
			       }
			   }else{
			       System.err.println("You didn't provide fastq2 file :(");
			       return;
			   }
			}   
		   
		   //####### set querySeq #######
		   if(params.get("fastqList")!=null){		    	
		     if(params.get("fastqList").size()>0){
				String fastqFiles=params.get("fastqList").get(0).trim();
				fastqList=FileOperate.getRowListFromFile(fastqFiles);
				if(SeqOperation.isFASTQSeq(fastqList)){					
					seqType=SeqOperation.SEQTYPE_SINGLEEND;
					//doSeqQCFilter=true;					
				}else{	
					System.err.println("Error: '-fastqList' file doesn't exist or doesn't contain a fastq file.");
					fastqList=new ArrayList<String>();					
					return;
				}
				fastqFiles=null;
			 }else{
				System.err.println("Illegal '-fastqList' parameter usage :(");			
				return;
			 }
		   }
		   
		   if(params.get("fastqList2")!=null){		    	
		      if(params.get("fastqList2").size()>0){
		    	 String fastqFiles=params.get("fastqList2").get(0).trim();					
		    	 fastqList2=FileOperate.getRowListFromFile(fastqFiles);
				 if(SeqOperation.isFASTQSeq(fastqList2)){					
					seqType=SeqOperation.SEQTYPE_PAIREND;
					//doSeqQCFilter=true;				 
				 }else{						
					System.err.println("Error: '-fastqList2' file doesn't exist or doesn't contain a fastq file.");
					fastqList2 = new ArrayList<String>();			
				    return;				    
				 }
				 fastqFiles=null;
			  }else{
				 System.err.println("Illegal '-fastqList2' parameter usage :(");			
				 return;
			  }
		   }
		   
		   //####### set output #######
		   boolean  doOutput=false;
		   if(params.get("outName")!=null){			
				if(params.get("outName").size()>0){
					 outName=params.get("outName").get(0).trim();			
				}else{
					 System.err.println("Illegal '-outName' parameter usage :(");
					 return;
				}
		   }
			
		   if(params.get("outTag")!=null){		  	
				if(params.get("outTag").size()>0){
					 outTag=params.get("outTag").get(0).trim();						 
				}else{
					 System.err.println("Illegal '-outTag' parameter usage :(");
					 return;
				}
		   }
			
		   if(params.get("outDir")!=null){			 
			   if(params.get("outDir").size()>0){
				 outDir=params.get("outDir").get(0).trim();
				 File f=new File(outDir);
			     if (f.exists()){
				   doOutput=true;
				   f=null;
				 }else if (outDir!=null){
				   FileOperate.newFolder(outDir);
				   doOutput=true;
				 }			   
			   }else{
				 System.err.println("Illegal '-outDir' parameter usage :(");
		
				 return;
				}
		   }		   
		   if(!doOutput){			   
				 outDir=homeDir+"/working";
				 dir=new File(outDir);
				 if(!dir.exists()) FileOperate.newFolder(outDir);	
		         dir=null;
				 doOutput=true;  		
		   }
		   
		   String inSeqFile = null;	
		   if(isFastaOK){
			   inSeqFile=fasta;
		   }else if(isFastqOK){
			   inSeqFile=fastq;
		   }	
		   if(params.get("filter_dupSeq")!=null){
			  boolean doDupSeqFilter=false;
			  System.out.println("Total seq num: "+SeqOperation.getSeqNum(inSeqFile));
			  if(outDir==null) outDir=inSeqFile.substring(0,inSeqFile.lastIndexOf("/"));			     
			     
			  if(params.get("filter_dupSeq").size()>1 
						 && (params.get("filter_dupSeq").size() % 2 == 0)){
				   
				   System.out.println("================Removing duplicated sequences===================");
				   //List<SeqInfo> seqObjList = new ArrayList<SeqInfo>();
				   int k=0;	
				   for(int i=0;i<params.get("filter_dupSeq").size();i=i+2){
					 k++;
					 seqNameFile=params.get("filter_dupSeq").get(i).trim();
					 seqNameCol=params.get("filter_dupSeq").get(i+1).trim();
				     if(seqNameFile!=null && isInteger(seqNameCol)){
				    	doDupSeqFilter=false;
				    	File f=new File(seqNameFile);
				    	if(f.exists()) doDupSeqFilter=true;
				    	f=null;
				    	if(Integer.parseInt(seqNameCol)<=1) 
				    	  seqNameColIdx=0;
				    	else
				    	  seqNameColIdx=Integer.parseInt(seqNameCol)-1;

				    	System.out.println("Checking for No."+k+" ......");
				    	if(doDupSeqFilter){				    	
				    	  System.out.println("Seq after filter: "
				    	         +SeqDupFilter.filterExactDup(inSeqFile,seqNameFile,0));
				    	  //System.out.println("+");				   
				    	}else{
						  System.out.println("Warning: ["+seqNameFile+"] doesn't exist, we ignored it!!!");
						}		    	
				     }else{
						System.out.println("Warning: '-filter_dupSeq' parameter is illeagl. We skiped it!!!");
					 }
				   }//for
		
			  }else{
					System.out.println("Warning: '-filter_dupSeq' parameter is illeagl. We skiped it!!!");
			  }
				  
			  if(!doDupSeqFilter) System.out.println("Warning: '-filter_dupSeq' parameter is illeagl. We skiped it!!!");
		   }
		   
		   //delTmpDir(tmpDir);	
		   if(tmpFiles!=null){
			 for(String tmpFile: tmpFiles){
			   FileOperate.delFile(tmpFile);
			 }
		   }
    }

}
