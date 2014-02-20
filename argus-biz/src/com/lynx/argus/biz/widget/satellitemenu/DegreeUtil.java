package com.lynx.argus.biz.widget.satellitemenu;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 14-2-20 下午6:41
 */
public class DegreeUtil {

	public static float[] getDefaultDegrees(int count, float totalDegrees) {
		if (count < 1) {
			return new float[] {};
		}

		float[] result = null;
		int tmpCount = 0;
		if (count < 4) {
			tmpCount = count + 1;
		} else {
			tmpCount = count - 1;
		}

		result = new float[count];
		float delta = totalDegrees / tmpCount;

		for (int index = 0; index < count; index++) {
			int tmpIndex = index;
			if (count < 4) {
				tmpIndex = tmpIndex + 1;
			}
			result[index] = tmpIndex * delta;
		}

		return result;
	}

	public static float[] getLinearDegrees(int count, float totalDegrees) {
		if (count < 1) {
			return new float[] {};
		}

		if (count == 1) {
			return new float[] { 45 };
		}

		float[] result = null;
		int tmpCount = count - 1;

		result = new float[count];
		float delta = totalDegrees / tmpCount;

		for (int index = 0; index < count; index++) {
			int tmpIndex = index;
			result[index] = tmpIndex * delta;
		}

		return result;
	}
}
