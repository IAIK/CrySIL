package obj;

import java.util.Arrays;

public class CK_DATE {
	private Integer[] year = { 0, 0, 0, 0 };
	private Integer[] month = { 0, 0 };
	private Integer[] day = { 0, 0 };

	public CK_DATE(int year0, int year1, int year2, int year3, int month0,
			int month1, int day0, int day1) {
		year[0] = year0;
		year[1] = year1;
		year[2] = year2;
		year[3] = year3;
		month[0] = month0;
		month[1] = month1;
		day[0] = day0;
		day[1] = day1;
	}

	public CK_DATE(Integer[] year, Integer[] month, Integer[] day) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public boolean equals(Object obj) {
		CK_DATE other = (CK_DATE) obj;
		if (Arrays.deepEquals((Object[]) year, (Object[]) other.year)
				&& Arrays.deepEquals((Object[]) day, (Object[]) other.day)
				&& Arrays.deepEquals(month, other.month)) {
			return true;
		}
		return false;
	}
}
