package com.lynx.argus.plugin.weather.model;

import com.google.gson.annotations.Expose;

/**
 *
 * @author chris.liu
 * @version 3/24/14 6:26 PM
 */
public class WeatherInfo {
	@Expose
	public String[] temp;
	@Expose
	public String[] wind;
	@Expose
	public String[] weather;
	@Expose
	public String suggestion;

	public Integer[] maxTemp;
	public Integer[] minTemp;

	public void setTemp(String[] temp) {
		this.temp = temp;
		generateMaxMinTemp(temp);
	}

	public String[] getTemp() {
		return temp;
	}

    public void setWind(String[] wind) {
        this.wind = wind;
    }

    public String[] getWind() {
		return wind;
	}

    public void setWeather(String[] weather) {
        this.weather = weather;
    }

    public String[] getWeather() {
		return weather;
	}

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSuggestion() {
		return suggestion;
	}

	public Integer[] getMaxTemp() {
		if (maxTemp == null) {
			generateMaxMinTemp(temp);
		}
		return maxTemp;
	}

	public Integer[] getMinTemp() {
		if (minTemp == null) {
			generateMaxMinTemp(temp);
		}
		return minTemp;
	}

	public void generateMaxMinTemp(String[] temp) {
		maxTemp = new Integer[temp.length];
		minTemp = new Integer[temp.length];
		for (int i = 0; i < temp.length; ++i) {
			String[] tmp = temp[i].replaceAll("\\℃", "").split("\\~");
			int max = Integer.parseInt(tmp[0]);
			int min = Integer.parseInt(tmp[1]);
			maxTemp[i] = max;
			minTemp[i] = min;
		}
	}
}
