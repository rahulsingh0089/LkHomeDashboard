package lenkeng.com.welcome.bean;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.lenkeng.bean.ApkBean;
import com.lenkeng.bean.Screen;

public class AppInfo implements Serializable {
    public static final int OPERATE_TYPE_ALL=1;
    public static final int OPERATE_TYPE_IR=2;
    public static final int OPERATE_TYPE_MOUSE=3;
    public static final int OPERATE_TYPE_GAME_HANDLE=4;
	

    private  String summary;
    private int recomm_index;
    private List<Screen> imgs;
	private String style;
    private String package_name;
    private int downloads;
	private String url;
	private String md5;
    private String version;
    private String category;
    
    @JsonProperty
    private String HDIcon;
    private String name;
    private int rating;
    private String icon;
    private String recommImage;
    private String banner_big;
    private String banner_small;
    private Long size;
    private int praise;
    private int reject;
    private int operateType;
    private long realSize;
    
    public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public int getPraise() {
		return praise;
	}
	public void setPraise(int praise) {
		this.praise = praise;
	}
	public int getReject() {
		return reject;
	}
	public void setReject(int reject) {
		this.reject = reject;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String imgs2String(){
    	StringBuilder tBuilder=new StringBuilder();
    	if(imgs!=null&&imgs.size()>0){
    		for (int j = 0; j < imgs.size(); j++) {
    		tBuilder.append(((Screen)imgs.get(j)).getUrl()).append(",");
    		}
    		tBuilder.deleteCharAt(tBuilder.length()-1);
    		
    	}
    	return tBuilder.toString();
    }
    
	public List<Screen> getImgs() {
		return imgs;
	}
	public void setImgs(List<Screen> imgs) {
		this.imgs = imgs;
	}
	
	
	
	
	public int getOperateType() {
		return operateType;
	}
	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}
	@Override
	public String toString() {
		return "AppInfo [summary=" + summary + ", recomm_index=" + recomm_index
				+ ", imgs=" + imgs + ", style=" + style + ", package_name="
				+ package_name + ", downloads=" + downloads + ", url=" + url
				+ ", version=" + version + ", category=" + category
				+ ", HDIcon=" + HDIcon + ", name=" + name + ", rating="
				+ rating + ", icon=" + icon + ", recommImage=" + recommImage
				+ ", banner_big=" + banner_big + ", banner_small="
				+ banner_small + ", size=" + size + ", praise=" + praise
				+ ", reject=" + reject + ", md5=" + md5  +",size="+size+",realSize="+realSize+"]";
	}
	public String getBanner_big() {
        return banner_big;
    }
    public void setBanner_big(String banner_big) {
        this.banner_big = banner_big;
    }
    public String getBanner_small() {
        return banner_small;
    }
    public void setBanner_small(String banner_small) {
        this.banner_small = banner_small;
    }
    
    public String getRecommImage() {
        return recommImage;
    }
    public void setRecommImage(String recommImage) {
        this.recommImage = recommImage;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public int getRecomm_index() {
        return recomm_index;
    }
    public void setRecomm_index(int recomm_index) {
        this.recomm_index = recomm_index;
    }
   
    public String getPackage_name() {
        return package_name;
    }
    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }
    public int getDownloads() {
        return downloads;
    }
    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    @JsonIgnore
    public String getHDIcon() {
        return HDIcon;
    }
    @JsonIgnore
    public void setHDIcon(String hDIcon) {
        HDIcon = hDIcon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
	public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }
    
    
    
	public long getRealSize() {
		return realSize;
	}
	public void setRealSize(long realSize) {
		this.realSize = realSize;
	}
	public ApkBean buildApkBean() {
		ApkBean tApk=new ApkBean();
		tApk.setName(this.getName());
		tApk.setPackageName(this.getPackage_name());
		tApk.setUrl(this.getUrl());
		tApk.setMd5(this.getMd5());
		tApk.setSize(this.getSize());
		tApk.setHdIcon(this.getHDIcon());
		tApk.setCategory(this.getCategory());
		tApk.setRealSize(this.getRealSize());
		return tApk;
	}
}
