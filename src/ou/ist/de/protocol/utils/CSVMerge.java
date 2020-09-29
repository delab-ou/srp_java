package ou.ist.de.protocol.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class CSVMerge {

	
	protected String[] proto;
	protected String[] type;
	public CSVMerge() {
		type=new String[] {"mcl_bn254","mcl_bls12_381","rsa_3072"};
		proto=new String[] {"ISDSR","SRDP","SRP"};
		
	}
	public void mergeCSVFile(String[] nodes,String dir,String suffix) {
		ArrayList<CSVFile> alcsv=null;
		ArrayList<String> alstr=null;
		String prefix=null;
		for(int i=0;i<proto.length;i++) {
			for(int j=0;j<type.length;j++) {
				if(proto[i].equalsIgnoreCase("isdsr")) {
					if(j==2) {
						break;
					}
				}
				prefix=proto[i]+"_"+type[j];
				alcsv=this.readCSVFiles(prefix, nodes, dir);
				alstr=this.merge(alcsv);
				this.writeMergeFile(alstr, prefix+suffix+".csv");
			}
		}
	}
	public void mergeLines() {
		String[] nodes=new String[] {"40","60","80","100"};
		String dir="result_line_int1";
		this.mergeCSVFile(nodes, dir,"_line");
	}
	public void mergeGrid() {
		String[] nodes=new String[] {"9","25","49","100"};
		String dir="result_grid_int1";
		this.mergeCSVFile(nodes, dir,"_grid");
	}
	public ArrayList<CSVFile> readCSVFiles(String prefix, String[] nodes, String dir){
		ArrayList<CSVFile> alcsv=new ArrayList<CSVFile>();
		CSVFile tmp=null;
		for(int i=0;i<nodes.length;i++) {
			tmp=this.readCSVFiles(dir+"/"+prefix+nodes[i]+".csv");
			if(tmp!=null) {
				alcsv.add(tmp);
			}
		}
		return alcsv;
	}
	public ArrayList<String> merge(ArrayList<CSVFile> alcsv){
		int length=alcsv.get(0).rows.size();
		ArrayList<String> alstr=new ArrayList<String>();
		String tmp=null;
		for(int i=0;i<length;i++) {
			tmp=new String("");
			for(int j=0;j<alcsv.size();j++) {
				tmp+=alcsv.get(j).getData(i)+",,";
			}
			alstr.add(tmp);
		}
		return alstr;
	}
	public void writeMergeFile(ArrayList<String> alstr,String file) {
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(file));
			for(int i=0;i<alstr.size();i++) {
				bw.write((alstr.get(i)+"\n"));
			}
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public CSVFile readCSVFiles(String file) {
		try {
			File f=new File(file);
			if(!f.exists()) {
				System.out.println("file "+file+" does not exist.");
				return null;
			}
			CSVFile csv=new CSVFile();
			csv.read(f);
			return csv;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public class CSVFile{
		protected String[] title;
		protected ArrayList<String[]> rows;
		
		public CSVFile() {
			title=new String[] {"SRC","DEST","seq","hops","time","rcvs","received"};
			rows=new ArrayList<String[]>();
		}
		public void read(File f) {
			try {
				BufferedReader br=new BufferedReader(new FileReader(f));
				String line=null;
				while((line=br.readLine())!=null) {
					System.out.println("line="+line);
					rows.add(line.split(","));
				}
				br.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		public void read(String file) {
			read(new File(file));
		}
		public String getData(int index) {
			if(index<rows.size()) {
				String[] tmp=rows.get(index);
				return tmp[2]+","+tmp[3]+","+tmp[4]+","+tmp[5]+","+tmp[6];
			}
			else {
				return ",,,,";
			}
		}
	}
	public static void main(String args[]) {
		CSVMerge cm=new CSVMerge();
		cm.mergeGrid();
		cm.mergeLines();
	}
}
