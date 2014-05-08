package lenkeng.com.welcome.bean;
/*
 * $Id: WeatherInfo.java 4 2013-12-12 04:19:52Z kf $
 */
public class WeatherInfo {
	private String city;
	private String city_en;
	private String date_y;
	private String week;
	private String[] temp;
	private String[] tempF;
	private String[] weather;
	private String[] wind;
	private String[] dates;
	public String[] getDates() {
		return dates;
	}

	public void setDates(String[] dates) {
		this.dates = dates;
	}

	public String[] getWind() {
		return wind;
	}

	public void setWind(String[] wind) {
		this.wind = wind;
	}

	private int[] image;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity_en() {
		return city_en;
	}

	public void setCity_en(String city_en) {
		this.city_en = city_en;
	}

	public String getDate_y() {
		return date_y;
	}

	public void setDate_y(String date_y) {
		this.date_y = date_y;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String[] getTemp() {
		return temp;
	}

	public void setTemp(String[] temp) {
		this.temp = temp;
	}

	public String[] getTempF() {
		return tempF;
	}

	public void setTempF(String[] tempF) {
		this.tempF = tempF;
	}

	public String[] getWeather() {
		return weather;
	}

	public void setWeather(String[] weather) {
		this.weather = weather;
	}

	public int[] getImage() {
		return image;
	}

	public void setImage(int[] image) {
		this.image = image;
	}
}
